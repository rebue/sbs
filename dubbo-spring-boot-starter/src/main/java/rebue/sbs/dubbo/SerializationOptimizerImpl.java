package rebue.sbs.dubbo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.dubbo.common.serialize.support.SerializationOptimizer;

import rebue.robotech.ra.BooleanRa;
import rebue.robotech.ra.CountRa;
import rebue.robotech.ra.IdRa;
import rebue.robotech.ra.IntegerRa;
import rebue.robotech.ra.ListRa;
import rebue.robotech.ra.LongRa;
import rebue.robotech.ra.PageRa;
import rebue.robotech.ra.PojoRa;
import rebue.robotech.ra.StringRa;
import rebue.robotech.ro.Ro;
import rebue.robotech.to.ListTo;

public class SerializationOptimizerImpl implements SerializationOptimizer {

    // private final ResourcePatternResolver _resolver = new PathMatchingResourcePatternResolver();
    // private final CachingMetadataReaderFactory _cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
    private List<Class<?>> _classes;

    @Override
    public Collection<Class<?>> getSerializableClasses() {
        if (_classes != null) {
            return _classes;
        }

        _classes = new LinkedList<>();
        _classes.add(ListTo.class);
        _classes.add(Ro.class);
        _classes.add(BooleanRa.class);
        _classes.add(CountRa.class);
        _classes.add(IdRa.class);
        _classes.add(IntegerRa.class);
        _classes.add(ListRa.class);
        _classes.add(LongRa.class);
        _classes.add(PageRa.class);
        _classes.add(PojoRa.class);
        _classes.add(StringRa.class);

        // 搜索符合条件的类在分布式中会带来灾难，因为不同微服务中无法保证同样的顺序来注册类
        // addClasses("**/mo/**Mo.class");
        // addClasses("**/jo/**Jo.class");
        // addClasses("**/ra/**Ra.class");
        // addClasses("**/to/**To.class");

        return _classes;
    }

    // /**
    // * 添加符合条件的类到_classes中
    // *
    // * @param condition 搜索类的条件
    // */
    // private void addClasses(final String condition) {
    // try {
    // final Resource[] resources = _resolver
    // .getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + condition);
    // for (final Resource resource : resources) {
    // final MetadataReader metadataReader = _cachingMetadataReaderFactory.getMetadataReader(resource);
    // if (metadataReader.getClassMetadata().isInterface()) {
    // continue;
    // }
    // _classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
    // }
    // } catch (final IOException | ClassNotFoundException e) {
    // e.printStackTrace();
    // throw new RuntimeException("优化序列化扫描类时出现异常");
    // }
    // }

}
