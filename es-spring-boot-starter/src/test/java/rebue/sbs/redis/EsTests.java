package rebue.sbs.redis;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import rebue.sbs.es.EsConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EsConfig.class)
@Slf4j
public class EsTests {

    @Resource
    private RestHighLevelClient esClient;

    @Test
    public void test01() {
        final SearchRequest searchRequest = new SearchRequest("gdp_tops*");
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("city", "北京市"));
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            final SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Arrays.stream(response.getHits().getHits()).forEach(i -> {
                log.info(i.getIndex());
                log.info(i.getSourceAsString());
            });
            log.info("total:{}", response.getHits().getTotalHits());
        } catch (final IOException e) {
            log.error("test failed", e);
        }
    }

}
