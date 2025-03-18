package com.dlim2012.searchconsumer.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HotelRepository {

    private final RestHighLevelClient restHighLevelClient;

    /**
     * Save or update a hotel document in Elasticsearch.
     * @param id Hotel ID
     * @param hotelData Hotel details as key-value pairs
     * @return Indexed document ID
     */
    public String saveHotel(String id, Map<String, Object> hotelData) {
        try {
            IndexRequest request = new IndexRequest("hotel").id(id).source(hotelData);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("Hotel indexed with ID: {}", response.getId());
            return response.getId();
        } catch (IOException e) {
            log.error("Error indexing hotel: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve hotel details by ID.
     * @param id Hotel ID
     * @return Hotel details as Map
     */
    public Map<String, Object> findHotelById(String id) {
        try {
            GetRequest request = new GetRequest("hotel", id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            log.error("Error fetching hotel by ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Delete a hotel document from Elasticsearch.
     * @param id Hotel ID
     * @return True if successfully deleted, false otherwise
     */
    public boolean deleteHotel(String id) {
        try {
            DeleteRequest request = new DeleteRequest("hotel", id);
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("Deleted hotel with ID: {}", id);
            return response.getResult().toString().equals("DELETED");
        } catch (IOException e) {
            log.error("Error deleting hotel: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Find hotels with ID greater than the given value.
     * @param minId Minimum hotel ID
     * @return Search response from Elasticsearch
     */
    public SearchResponse findHotelsGreaterThan(int minId) {
        try {
            SearchRequest searchRequest = new SearchRequest("hotel");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.rangeQuery("id").gt(minId));
            searchRequest.source(sourceBuilder);

            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error searching hotels: {}", e.getMessage());
            return null;
        }
    }
}
