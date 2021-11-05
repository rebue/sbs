package rebue.sbs.sensitive.plugin;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import rebue.wheel.api.annotation.Desensitize;
import rebue.wheel.api.strategy.DesensitizeStrategy;

@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class
}))
public class DesensitizePlugin implements Interceptor {

    @Override
    public Object intercept(final Invocation invocation) throws Throwable {
        @SuppressWarnings("unchecked")
        final List<Object> records = (List<Object>) invocation.proceed();
        // 对结果集脱敏
        records.forEach(this::desensitize);
        return records;
    }

    private void desensitize(final Object source) {
        // 拿到返回值类型
        final Class<?> sourceClass = source.getClass();
        // 初始化返回值类型的 MetaObject
        final MetaObject metaObject = SystemMetaObject.forObject(source);
        // 捕捉到属性上的标记注解 @Sensitive 并进行对应的脱敏处理
        Stream.of(sourceClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Desensitize.class))
                .forEach(field -> doDesensitize(metaObject, field));
    }

    private void doDesensitize(MetaObject metaObject, Field field) {
        // 拿到属性名
        String name = field.getName();
        // 获取属性值
        String value = (String) metaObject.getValue(name);
        // 只有字符串类型才能脱敏 而且不能为null
        if (String.class == metaObject.getGetterType(name) && value != null) {
            Desensitize              annotation          = field.getAnnotation(Desensitize.class);
            // 获取对应的脱敏策略 并进行脱敏
            DesensitizeStrategy      desensitizeStrategy = annotation.value();

            Function<String, String> desensitizer        = null;
            if (desensitizeStrategy != null)
                desensitizer = desensitizeStrategy.getDesensitizer();
            else
                desensitizer = str -> {
                    if (StringUtils.isBlank(str)) return "";
                    str = str.trim();
                    return str.replaceAll(annotation.regex(), annotation.replacement());
                };
            // 把脱敏后的值塞回去
            metaObject.setValue(name, desensitizer.apply(value));
        }
    }
}
