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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.Operator;
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
     * @param request NumberByCityRequest containing location filters.
     * @return NumberResponse with the count of matching hotels.
     */
    public NumberResponse numHotelByCity(NumberByCityRequest request) {
        try {
            CountRequest countRequest = new CountRequest("hotel");
            BoolQueryBuilder hotelBool = QueryBuilders.boolQuery();

            if (request.getCountry() != null && !request.getCountry().isEmpty()) {
                // Assuming country is stored as a keyword for exact match
                hotelBool.must(QueryBuilders.termQuery("country.keyword", request.getCountry()));
            }
            if (request.getState() != null && !request.getState().isEmpty()) {
                hotelBool.must(QueryBuilders.termQuery("state.keyword", request.getState()));
            }
            if (request.getCity() != null && !request.getCity().isEmpty()) {
                // Use term query on the "city.keyword" field for an exact match.
                hotelBool.must(QueryBuilders.termQuery("city.keyword", request.getCity()));
            }

            countRequest.query(hotelBool);
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            log.info("Hotel count for city [{}]: {}", request.getCity(), countResponse.getCount());

            return NumberResponse.builder()
                    .count((int) countResponse.getCount())
                    .build();
        } catch (IOException e) {
            log.error("Error counting hotels for city [{}]: {}", request.getCity(), e.getMessage(), e);
            return NumberResponse.builder().count(0).build();
        }
    }

    /**
     * Overloaded method to process a list of city requests.
     *
     * @param requests List of NumberByCityRequest objects.
     * @return List of NumberResponse objects.
     */
    public List<NumberResponse> numHotelByCity(List<NumberByCityRequest> requests) {
        return requests.stream()
                .map(this::numHotelByCity)
                .collect(Collectors.toList());
    }

    /**
     * Count hotels by property type.
     *
     * @param request NumberByPropertyTypeRequest with property type filter.
     * @return NumberResponse with count of matching hotels.
     */
    public NumberResponse numHotelByPropertyType(NumberByPropertyTypeRequest request) {
        try {
            CountRequest countRequest = new CountRequest("hotel");
            // Option 1: If your index stores the property type as text/keyword:
            countRequest.query(QueryBuilders.termQuery("propertyType.keyword", request.getPropertyType()));

            // Option 2: If your documents store the ordinal value:
            // int ordinal = PropertyType.valueOf(request.getPropertyType()).ordinal();
            // countRequest.query(QueryBuilders.termQuery("propertyTypeOrdinal", ordinal));

            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            log.info("Hotel count for property type [{}]: {}", request.getPropertyType(), countResponse.getCount());
            return NumberResponse.builder()
                    .count((int) countResponse.getCount())
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid property type [{}]: {}", request.getPropertyType(), e.getMessage());
            return NumberResponse.builder().count(0).build();
        } catch (IOException e) {
            log.error("Error counting hotels for property type [{}]: {}", request.getPropertyType(), e.getMessage(), e);
            return NumberResponse.builder().count(0).build();
        }
    }

    /**
     * Overloaded method to process a list of property type requests.
     *
     * @param requests List of NumberByPropertyTypeRequest objects.
     * @return List of NumberResponse objects.
     */
    public List<NumberResponse> numHotelByPropertyType(List<NumberByPropertyTypeRequest> requests) {
        return requests.stream()
                .map(this::numHotelByPropertyType)
                .collect(Collectors.toList());
    }
}

