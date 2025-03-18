package com.dlim2012.search.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HotelRepository {

    private final RestHighLevelClient restHighLevelClient;

    public String searchHotels(String query) {
        try {
            // Create search request for "hotel" index
            SearchRequest searchRequest = new SearchRequest("hotel");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.queryStringQuery(query));
            searchRequest.source(searchSourceBuilder);

            // Execute search request
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse.toString();
        } catch (IOException e) {
            log.error("Search failed due to an IO exception: {}", e.getMessage());
            throw new RuntimeException("Search failed", e);
        }
    }
}
