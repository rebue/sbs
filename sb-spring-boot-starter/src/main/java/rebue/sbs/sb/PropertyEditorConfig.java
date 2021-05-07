package rebue.sbs.sb;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化StringToLong转换器
 */
@Configuration(proxyBeanMethods = false)
public class PropertyEditorConfig {
    @Bean
    public CustomEditorConfigurer customEditorConfigurer() {
        final CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        // 有两种注册方式 这是第一种
        customEditorConfigurer.setPropertyEditorRegistrars(new PropertyEditorRegistrar[] {
            new ArrayPropertyEditor()
        });
        return customEditorConfigurer;
    }

}
