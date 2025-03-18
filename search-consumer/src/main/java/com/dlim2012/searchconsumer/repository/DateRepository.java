package com.dlim2012.searchconsumer.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DateRepository {

    private final RestHighLevelClient restHighLevelClient;

    /**
     * Save or update a date document in AWS OpenSearch.
     * @param id Date ID
     * @param dateData Date details as key-value pairs
     * @return Indexed document ID
     */
    public String saveDate(String id, Map<String, Object> dateData) {
        try {
            IndexRequest request = new IndexRequest("dates").id(id).source(dateData);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("Date indexed with ID: {}", response.getId());
            return response.getId();
        } catch (IOException e) {
            log.error("Error indexing date: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve date details by ID.
     * @param id Date ID
     * @return Date details as Map
     */
    public Map<String, Object> findDateById(String id) {
        try {
            GetRequest request = new GetRequest("dates", id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            log.error("Error fetching date by ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Delete a date document from AWS OpenSearch.
     * @param id Date ID
     * @return True if successfully deleted, false otherwise
     */
    public boolean deleteDate(String id) {
        try {
            DeleteRequest request = new DeleteRequest("dates", id);
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("Deleted date with ID: {}", id);
            return response.getResult().toString().equals("DELETED");
        } catch (IOException e) {
            log.error("Error deleting date: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if a date exists by ID.
     * @param id Date ID
     * @return True if date exists, false otherwise
     */
    public boolean existsById(String id) {
        try {
            GetRequest request = new GetRequest("dates", id);
            return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error checking date existence: {}", e.getMessage());
            return false;
        }
    }
}
