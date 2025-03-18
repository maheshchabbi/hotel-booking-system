package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Facility;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelsNewDayDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexingService {

    private final DateService dateService;
    private final PriceService priceService;
    private final HotelRepository hotelRepository;
    private final ElasticSearchUtils elasticSearchUtils;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Random random = new Random();
    private final Integer NUM_RETRY_UPDATE = 2;

    /**
     * Fetch hotel data from OpenSearch
     */
    private Hotel getHotelById(String hotelId) throws IOException {
        GetRequest getRequest = new GetRequest("hotel", hotelId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        if (!getResponse.isExists()) {
            throw new ResourceNotFoundException("Hotel ID " + hotelId + " not found in OpenSearch.");
        }
        return objectMapper.readValue(getResponse.getSourceAsBytes(), Hotel.class);
    }

    /**
     * Save hotel data to OpenSearch
     */
    private void saveHotel(Hotel hotel) throws IOException {
        IndexRequest indexRequest = new IndexRequest("hotel")
                .id(hotel.getId())
                .source(objectMapper.convertValue(hotel, Map.class));
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("Saved hotel with ID: {}", response.getId());
    }

    /**
     * Save or update a hotel in OpenSearch
     */
    public Hotel saveHotel(HotelSearchDetails hotelDetails) throws IOException, InterruptedException {
        Hotel hotel = modelMapper.map(hotelDetails, Hotel.class);
        hotel.setId(hotelDetails.getId().toString());
        hotel.setFacility(hotelDetails.getFacility().stream()
                .map(dto -> Facility.builder().id(dto.getId()).build())
                .toList());
        hotel.setRooms(new ArrayList<>());
        hotel.setGeoPoint(new GeoPoint(hotelDetails.getLatitude(), hotelDetails.getLongitude()));

        saveHotel(hotel);
        return hotel;
    }

    /**
     * Update an existing hotel in OpenSearch
     */
    public Hotel updateHotel(HotelSearchDetails hotelDetails) throws IOException, InterruptedException {
        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                Hotel hotel = getHotelById(hotelDetails.getId().toString());

                hotel.setName(hotelDetails.getName());
                hotel.setPropertyTypeOrdinal(hotelDetails.getPropertyTypeOrdinal().toString());
                hotel.setNeighborhood(hotelDetails.getNeighborhood());
                hotel.setZipcode(hotelDetails.getZipcode());
                hotel.setCity(hotelDetails.getCity());
                hotel.setState(hotelDetails.getState());
                hotel.setCountry(hotelDetails.getCountry());
                hotel.setGeoPoint(new GeoPoint(hotelDetails.getLatitude(), hotelDetails.getLongitude()));
                hotel.setPropertyRating(hotelDetails.getPropertyRating());
                hotel.setFacility(hotelDetails.getFacility().stream()
                        .map(dto -> Facility.builder().id(dto.getId()).build())
                        .toList());

                saveHotel(hotel);
                return hotel;
            } catch (ResourceNotFoundException e) {
                log.error(e.getMessage());
                return null;
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
            TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
        }
        log.error("Update of hotel {} failed after {} retries.", hotelDetails.getId(), NUM_RETRY_UPDATE);
        return null;
    }

    /**
     * Delete a hotel from OpenSearch
     */
    public void delete(HotelSearchDeleteRequest request) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest("hotel", request.getHotelId().toString());
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (response.getResult().toString().equals("DELETED")) {
                log.info("Deleted hotel with ID: {}", request.getHotelId());
            } else {
                log.warn("Hotel ID {} not found for deletion.", request.getHotelId());
            }
        } catch (IOException e) {
            log.error("Error deleting hotel ID {}: {}", request.getHotelId(), e.getMessage());
        }
    }

    /**
     * Bulk update hotels, prices, and dates in OpenSearch
     */
    public void bulkUpdate(HotelsNewDayDetails request) throws InterruptedException {
        Integer today = elasticSearchUtils.toInteger(LocalDate.now());

        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                for (int hotelId = request.getStartId(); hotelId < request.getEndId(); hotelId++) {
                    try {
                        Hotel hotel = getHotelById(String.valueOf(hotelId));

                        // Update dates
                        DatesUpdateDetails dateDetails = request.getDatesUpdateDetailsMap().getOrDefault(hotelId, null);
                        if (dateDetails != null) {
                            dateService.updateHotelDates(hotel, dateDetails);
                        }

                        // Update prices
                        List<PriceUpdateDetails> priceDetailsList = request.getPriceUpdateDetailsMap().getOrDefault(hotelId, null);
                        if (priceDetailsList != null) {
                            for (PriceUpdateDetails priceDetails : priceDetailsList) {
                                priceService.updateHotelPrices(hotel, priceDetails);
                            }
                        }

                        // Save updated hotel data
                        saveHotel(hotel);
                    } catch (Exception e) {
                        log.error("Error processing hotel ID {}: {}", hotelId, e.getMessage());
                    }
                }
                return;
            } catch (Exception e) {
                log.error("Bulk update failed: {}", e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
            }
        }
        log.error("Updating hotels {} ~ {} failed after {} retries.", request.getStartId(), request.getEndId(), NUM_RETRY_UPDATE);
    }
}
