package rebue.sbs.sb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnExpression("${server.ssl.enabled:true}")
@ConditionalOnProperty(name = "server.ssl.enabled", havingValue = "true")
public class SslConfig {

    /**
     * 监听http的端口号
     */
    @Value("${server.ssl.http.port:80}")
    private int port;

    @Bean
    public UndertowServletWebServerFactory undertowServletWebServerFactory() {
        final UndertowServletWebServerFactory undertowFactory = new UndertowServletWebServerFactory();
        // 监听80端口
        undertowFactory.addBuilderCustomizers(builder -> {
            builder.addHttpListener(port, "0.0.0.0");
        });
        // 将http的80端口重定向到https的443端口上
        undertowFactory.addDeploymentInfoCustomizers(deploymentInfo -> {
            deploymentInfo
                    .addSecurityConstraint(new SecurityConstraint()
                            .addWebResourceCollection(new WebResourceCollection().addUrlPattern("/**"))
                            .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
                            .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
                    .setConfidentialPortManager(exchange -> 443);
        });

        return undertowFactory;
    }

}
