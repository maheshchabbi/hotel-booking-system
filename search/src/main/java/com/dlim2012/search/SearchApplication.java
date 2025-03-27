package com.dlim2012.search;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.search.config.ElasticSearchHighLevelClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Import({
        // Exception handling
        ApplicationExceptionHandler.class,
        // Elasticsearch utilities
        ElasticSearchUtils.class,
        // AWS OpenSearch Client Configuration
        ElasticSearchHighLevelClientConfig.class
})
@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}

