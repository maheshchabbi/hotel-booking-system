package com.dlim2012.searchconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchConsumerRunner implements CommandLineRunner {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting SearchConsumerRunner...");

        // Create a search request for the "hotel" index
        SearchRequest searchRequest = new SearchRequest("hotel");
        searchRequest.source(new SearchSourceBuilder().size(10)); // Limit to 10 results

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("Fetched Hotels: {}", objectMapper.writeValueAsString(searchResponse.getHits().getHits()));
        } catch (Exception e) {
            log.error("Error fetching hotels from OpenSearch: {}", e.getMessage());
        }
    }
}
