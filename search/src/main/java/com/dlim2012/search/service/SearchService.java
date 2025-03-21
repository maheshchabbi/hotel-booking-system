package com.dlim2012.search.service;

import com.dlim2012.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.search.dto.hotelSearch.HotelSearchResponse;
import com.dlim2012.search.dto.priceAgg.PriceAggRequest;
import com.dlim2012.search.dto.priceAgg.PriceAggResponse;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityRequest;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final RestHighLevelClient restHighLevelClient;

    // ✅ Fix for `search(HotelSearchRequest request)`
    public HotelSearchResponse search(HotelSearchRequest request) {
        try {
            SearchRequest searchRequest = new SearchRequest("hotel");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchQuery("city", request.getCity()));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("Search results: {}", searchResponse.toString());

            // Convert response to DTO
            HotelSearchResponse response = new HotelSearchResponse();
            // You may need to parse searchResponse and populate the DTO accordingly
            return response;
        } catch (IOException e) {
            log.error("Search failed: {}", e.getMessage());
            throw new RuntimeException("Search failed", e);
        }
    }

    // ✅ Fix for `aggPrice(PriceAggRequest request)`
    public List<PriceAggResponse> aggPrice(PriceAggRequest request) {
        log.info("Aggregating price: {}", request);
        // Mock response - replace with actual logic
        return Collections.emptyList();
    }

    // ✅ Fix for `getRoomsAvailability(Integer hotelId, RoomsAvailabilityRequest request)`
    public RoomsAvailabilityResponse getRoomsAvailability(Integer hotelId, RoomsAvailabilityRequest request) {
        log.info("Checking room availability for hotel: {}", hotelId);
        // Mock response - replace with actual logic
        return new RoomsAvailabilityResponse();
    }
}

