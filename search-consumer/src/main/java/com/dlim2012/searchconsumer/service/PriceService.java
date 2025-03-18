package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.elasticsearch.document.Price;
import com.dlim2012.clients.elasticsearch.document.Rooms;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private final Integer NUM_RETRY_UPDATE = 2;
    private final ElasticSearchUtils elasticSearchUtils;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

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
     * Updates the prices for a given hotel
     */
    public void updateHotelPrices(Hotel hotel, PriceUpdateDetails priceUpdateDetails) {
        for (Rooms rooms : hotel.getRooms()) {
            if (rooms.getRoomsId().equals(priceUpdateDetails.getRoomsId())) {
                if (rooms.getPriceVersion() <= priceUpdateDetails.getPriceVersion()) {
                    rooms.setPriceVersion(priceUpdateDetails.getPriceVersion());
                    rooms.setPrice(priceUpdateDetails.getPriceDtoList().stream()
                            .map(priceDto -> Price.builder()
                                    .id(priceDto.getPriceId().toString())
                                    .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                                    .roomsId(priceUpdateDetails.getRoomsId())
                                    .priceInCents(priceDto.getPriceInCents())
                                    .build())
                            .toList());
                }
            }
        }
    }

    /**
     * Updates price details in OpenSearch
     */
    public void updatePrices(PriceUpdateDetails details) throws InterruptedException {
        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                // Fetch hotel from OpenSearch
                Hotel hotel = getHotelById(details.getHotelId().toString());

                updateHotelPrices(hotel, details);
                saveHotel(hotel);
                return;
            } catch (ResourceNotFoundException e) {
                log.error(e.getMessage());
                return;
            } catch (Exception e) {
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
            }
        }
        log.error("Updating prices for hotel {} failed after {} retries.", details.getHotelId(), NUM_RETRY_UPDATE);
    }
}
