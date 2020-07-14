package rebue.sbs.es;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EsConfig {
    private static final int    ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME    = "http";

    /**
     * 使用实际ES的地址
     * 使用冒号隔开ip和端口1
     */
    @Value("${elasticsearch.host}")
    String[]                    hosts;

    @Bean
    public RestClientBuilder restClientBuilder() {
        final HttpHost[] httpHosts = Arrays.stream(hosts).map(this::makeHttpHost).filter(Objects::nonNull).toArray(HttpHost[]::new);
        log.debug("es hosts:{}", Arrays.toString(httpHosts));
        return RestClient.builder(httpHosts);
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(@Autowired final RestClientBuilder restClientBuilder) {
//        restClientBuilder.setMaxRetryTimeoutMillis(60000);
        return new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost makeHttpHost(final String s) {
        assert StringUtils.isNotEmpty(s);
        final String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            final String ip = address[0];
            final int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }
}
