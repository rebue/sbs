/**
 * RabbitAnnotationDrivenConfiguration在spring的代码中未声明为public，不能使用，所以复制过来
 *
 * 完全复制 org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration 类
 * 并给类添加public
 *
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.amqp;

import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring AMQP annotation driven endpoints.
 *
 * @author Stephane Nicoll
 * @author Josh Thornhill
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableRabbit.class)
public class RabbitAnnotationDrivenConfiguration {

    private final ObjectProvider<MessageConverter>              messageConverter;

    private final ObjectProvider<MessageRecoverer>              messageRecoverer;

    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

    private final RabbitProperties                              properties;

    RabbitAnnotationDrivenConfiguration(final ObjectProvider<MessageConverter> messageConverter,
        final ObjectProvider<MessageRecoverer> messageRecoverer,
        final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers, final RabbitProperties properties) {
        this.messageConverter         = messageConverter;
        this.messageRecoverer         = messageRecoverer;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
        this.properties               = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer() {
        final SimpleRabbitListenerContainerFactoryConfigurer configurer = new SimpleRabbitListenerContainerFactoryConfigurer();
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setMessageRecoverer(messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(
            retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        configurer.setRabbitProperties(properties);
        return configurer;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    @ConditionalOnMissingBean(name = "rabbitListenerContainerFactory")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "simple", matchIfMissing = true)
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
                                                                              final SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                              final ConnectionFactory connectionFactory) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer() {
        final DirectRabbitListenerContainerFactoryConfigurer configurer = new DirectRabbitListenerContainerFactoryConfigurer();
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setMessageRecoverer(messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(
            retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        configurer.setRabbitProperties(properties);
        return configurer;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    @ConditionalOnMissingBean(name = "rabbitListenerContainerFactory")
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "direct")
    DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(
                                                                              final DirectRabbitListenerContainerFactoryConfigurer configurer,
                                                                              final ConnectionFactory connectionFactory) {
        final DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableRabbit
    @ConditionalOnMissingBean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    static class EnableRabbitConfiguration {

    }

}
