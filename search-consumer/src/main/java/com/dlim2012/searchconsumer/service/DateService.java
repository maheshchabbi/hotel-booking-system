package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Dates;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.elasticsearch.document.Rooms;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchVersion;
import com.dlim2012.clients.utils.PriceService;
import com.dlim2012.searchconsumer.repository.DateRepository;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import com.dlim2012.searchconsumer.repository.RoomsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DateService {

    private final RoomsRepository roomsRepository;
    private final DateRepository dateRepository;
    private final HotelRepository hotelRepository;
    private final PriceService priceService;
    private final ElasticSearchUtils elasticSearchUtils;
    private final RestHighLevelClient restHighLevelClient;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    private final Integer NUM_RETRY_UPDATE = 2;

    public DateService(RoomsRepository roomsRepository, DateRepository dateRepository, HotelRepository hotelRepository, 
                       PriceService priceService, ElasticSearchUtils elasticSearchUtils, 
                       ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
        this.roomsRepository = roomsRepository;
        this.dateRepository = dateRepository;
        this.hotelRepository = hotelRepository;
        this.priceService = priceService;
        this.elasticSearchUtils = elasticSearchUtils;
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

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
        IndexRequest indexRequest = new IndexRequest("hotel").id(hotel.getId()).source(objectMapper.convertValue(hotel, Map.class));
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("Saved hotel with ID: {}", response.getId());
    }

    public void updateRooms(RoomsSearchDetails details) throws IOException, InterruptedException {
        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                Hotel hotel = getHotelById(details.getHotelId().toString());

                Rooms newRooms = modelMapper.map(details, Rooms.class);
                List<Rooms> newRoomsList = new ArrayList<>();

                if (hotel.getRooms() != null) {
                    newRoomsList = hotel.getRooms().stream()
                        .filter(rooms -> !rooms.getRoomsId().equals(details.getRoomsId()))
                        .collect(Collectors.toList());
                }
                newRoomsList.add(newRooms);
                hotel.setRooms(newRoomsList);

                saveHotel(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e) {
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Update Rooms for hotel {} failed after {} retries.", details.getHotelId(), NUM_RETRY_UPDATE);
    }

    public void updateRoomsVersion(RoomsSearchVersion newVersionDetails) throws IOException, InterruptedException {
        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                Hotel hotel = getHotelById(newVersionDetails.getHotelId().toString());

                if (hotel.getRooms() != null) {
                    for (Rooms rooms : hotel.getRooms()) {
                        if (rooms.getRoomsId().equals(newVersionDetails.getRoomsId())) {
                            rooms.setPriceVersion(newVersionDetails.getPriceVersion());
                            rooms.setPrice(newVersionDetails.getPriceDto().stream()
                                .map(priceDto -> Price.builder()
                                    .id(priceDto.getPriceId().toString())
                                    .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                                    .roomsId(newVersionDetails.getRoomsId())
                                    .priceInCents(priceDto.getPriceInCents())
                                    .build())
                                .collect(Collectors.toList()));
                        }
                    }
                }
                saveHotel(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e) {
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Update Rooms version for hotel {} failed after {} retries.", newVersionDetails.getHotelId(), NUM_RETRY_UPDATE);
    }

    public void deleteRooms(RoomsSearchDeleteRequest request) throws InterruptedException {
        for (int i = 0; i < NUM_RETRY_UPDATE; i++) {
            try {
                Hotel hotel = getHotelById(request.getHotelId().toString());

                List<Rooms> newRoomsList = hotel.getRooms().stream()
                    .filter(rooms -> !rooms.getRoomsId().equals(request.getRoomsId()))
                    .collect(Collectors.toList());

                hotel.setRooms(newRoomsList);
                saveHotel(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e) {
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Delete Rooms {} for hotel {} failed after {} retries.", request.getRoomsId(), request.getHotelId(), NUM_RETRY_UPDATE);
    }
}
