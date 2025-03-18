package com.dlim2012.searchconsumer.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoomsRepository {

    private final RestHighLevelClient restHighLevelClient;

    /**
     * Save or update a room document in AWS OpenSearch.
     * @param id Room ID
     * @param roomData Room details as key-value pairs
     * @return Indexed document ID
     */
    public String saveRoom(String id, Map<String, Object> roomData) {
        try {
            IndexRequest request = new IndexRequest("rooms").id(id).source(roomData);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("Room indexed with ID: {}", response.getId());
            return response.getId();
        } catch (IOException e) {
            log.error("Error indexing room: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve room details by ID.
     * @param id Room ID
     * @return Room details as Map
     */
    public Map<String, Object> findRoomById(String id) {
        try {
            GetRequest request = new GetRequest("rooms", id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            log.error("Error fetching room by ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Delete a room document from AWS OpenSearch.
     * @param id Room ID
     * @return True if successfully deleted, false otherwise
     */
    public boolean deleteRoom(String id) {
        try {
            DeleteRequest request = new DeleteRequest("rooms", id);
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("Deleted room with ID: {}", id);
            return response.getResult().toString().equals("DELETED");
        } catch (IOException e) {
            log.error("Error deleting room: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if a room exists by ID.
     * @param id Room ID
     * @return True if room exists, false otherwise
     */
    public boolean existsById(String id) {
        try {
            GetRequest request = new GetRequest("rooms", id);
            return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error checking room existence: {}", e.getMessage());
            return false;
        }
    }
}
