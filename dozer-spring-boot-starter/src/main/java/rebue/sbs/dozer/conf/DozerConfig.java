package rebue.sbs.dozer.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;

@Configuration
@EnableConfigurationProperties(DozerConfig.class)
public class DozerConfig {
//    @Value("${sbs.dozer.mappingFiles:classpath*:conf/dozer/*.xml}")
//    private Resource[] mappingFiles;

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean(
            @Value("classpath*:conf/dozer/*.xml") Resource[] resources) {
        final DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean = new DozerBeanMapperFactoryBean();
        // Other configurations
        dozerBeanMapperFactoryBean.setMappingFiles(resources);
        return dozerBeanMapperFactoryBean;
    }

//    @Bean
//    public Mapper dozerMapper() throws Exception {
//        DozerBeanMapperFactoryBean factory = new DozerBeanMapperFactoryBean();
//        factory.setMappingBuilders(Arrays.asList(beanMappingBuilder()));
//        Resource[] resources = new Resource[mappingFiles.length];
//        for (int i = 0; i < resources.length; i++) {
//            resources[i] = new ClassPathResource(mappingFiles[i]);
//        }
//        factory.setMappingFiles(resources);
//        return factory.getObject();
//    }
}
