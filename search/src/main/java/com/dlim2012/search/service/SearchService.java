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

    /**
     * Executes a search query on the "hotel" index based on the city provided in the request.
     * @param request A HotelSearchRequest containing search criteria.
     * @return A HotelSearchResponse with the search results.
     */
    public HotelSearchResponse search(HotelSearchRequest request) {
        try {
            log.info("Starting search with request: {}", request);

            // Build the search request for index "hotel"
            SearchRequest searchRequest = new SearchRequest("hotel");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchQuery("city", request.getCity()));
            searchRequest.source(searchSourceBuilder);

            // Execute the search request
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("Search response: {}", searchResponse);

            // TODO: Convert searchResponse into HotelSearchResponse DTO
            HotelSearchResponse response = new HotelSearchResponse();
            // Populate response with the data from searchResponse as needed

            return response;
        } catch (IOException e) {
            log.error("Error executing search: {}", e.getMessage(), e);
            throw new RuntimeException("Search operation failed", e);
        }
    }

    /**
     * Performs price aggregation based on the provided request.
     * @param request A PriceAggRequest containing aggregation parameters.
     * @return A list of PriceAggResponse objects representing the aggregation result.
     */
    public List<PriceAggResponse> aggPrice(PriceAggRequest request) {
        log.info("Aggregating price for request: {}", request);
        // TODO: Implement actual aggregation logic
        return Collections.emptyList();
    }

    /**
     * Checks room availability for a given hotel.
     * @param hotelId The ID of the hotel.
     * @param request A RoomsAvailabilityRequest containing room availability criteria.
     * @return A RoomsAvailabilityResponse with availability information.
     */
    public RoomsAvailabilityResponse getRoomsAvailability(Integer hotelId, RoomsAvailabilityRequest request) {
        log.info("Getting room availability for hotel: {} with request: {}", hotelId, request);
        // TODO: Implement actual room availability logic
        return new RoomsAvailabilityResponse();
    }
}

