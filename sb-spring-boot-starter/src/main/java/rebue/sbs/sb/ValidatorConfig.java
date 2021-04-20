package rebue.sbs.sb;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * 配置hibernate Validator为快速失败返回模式
 * 校验时只要一发现一项不符合就抛异常，而不是等到所有检验完了才返回
 * 
 * @author zbz
 *
 */
@Configuration(proxyBeanMethods = false)
public class ValidatorConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        /** 设置validator模式为快速失败返回 */
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public Validator validator() {
        final ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .addProperty("hibernate.validator.fail_fast", "true").buildValidatorFactory();
        final Validator        validator        = validatorFactory.getValidator();
        return validator;
    }
}
