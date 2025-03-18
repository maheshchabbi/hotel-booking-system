package com.dlim2012.search.service;

import com.dlim2012.clients.entity.PropertyType;
import com.dlim2012.search.dto.count.NumberByCityRequest;
import com.dlim2012.search.dto.count.NumberByPropertyTypeRequest;
import com.dlim2012.search.dto.count.NumberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountHotelService {

    private final RestHighLevelClient client;

    /**
     * Count hotels based on city, state, and country filters.
     *
     * @param request NumberByCityRequest containing location filters
     * @return NumberResponse with count of matching hotels
     */
    public NumberResponse numHotelByCity(NumberByCityRequest request) {
        try {
            CountRequest countRequest = new CountRequest("hotel");
            BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();

            // Match location filters
            if (request.getCountry() != null && !request.getCountry().isEmpty()) {
                hotelBool.must(QueryBuilders.matchQuery("country", request.getCountry()).operator(Operator.AND));
            }
            if (request.getState() != null && !request.getState().isEmpty()) {
                hotelBool.must(QueryBuilders.matchQuery("state", request.getState()).operator(Operator.AND));
            }
            if (request.getCity() != null && !request.getCity().isEmpty()) {
                hotelBool.must(QueryBuilders.matchQuery("city", request.getCity()).operator(Operator.AND));
            }

            countRequest.query(hotelBool);
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            log.info("Hotel count for city [{}]: {}", request.getCity(), countResponse.getCount());

            return NumberResponse.builder().count((int) countResponse.getCount()).build();
        } catch (IOException e) {
            log.error("Error counting hotels for city [{}]: {}", request.getCity(), e.getMessage());
            return NumberResponse.builder().count(0).build();
        }
    }

    /**
     * Count hotels for multiple city requests.
     *
     * @param requests List of NumberByCityRequest objects
     * @return List of NumberResponse objects with hotel counts
     */
    public List<NumberResponse> numHotelByCity(List<NumberByCityRequest> requests) {
        return requests.stream()
                .map(this::numHotelByCity)
                .collect(Collectors.toList());
    }

    /**
     * Count hotels by property type.
     *
     * @param request NumberByPropertyTypeRequest with property type filter
     * @return NumberResponse with count of matching hotels
     */
    public NumberResponse numHotelByPropertyType(NumberByPropertyTypeRequest request) {
        try {
            CountRequest countRequest = new CountRequest("hotel");
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("propertyTypeOrdinal",
                    PropertyType.valueOf(request.getPropertyType()).ordinal());

            countRequest.query(matchQuery);
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            log.info("Hotel count for property type [{}]: {}", request.getPropertyType(), countResponse.getCount());

            return NumberResponse.builder().count((int) countResponse.getCount()).build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid property type [{}]: {}", request.getPropertyType(), e.getMessage());
            return NumberResponse.builder().count(0).bu
