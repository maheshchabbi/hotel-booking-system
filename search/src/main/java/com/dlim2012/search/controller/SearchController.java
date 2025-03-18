package com.dlim2012.search.controller;

import com.dlim2012.search.dto.count.NumberByCityRequest;
import com.dlim2012.search.dto.count.NumberByPropertyTypeRequest;
import com.dlim2012.search.dto.count.NumberResponse;
import com.dlim2012.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.search.dto.hotelSearch.HotelSearchResponse;
import com.dlim2012.search.dto.priceAgg.PriceAggRequest;
import com.dlim2012.search.dto.priceAgg.PriceAggResponse;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityRequest;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityResponse;
import com.dlim2012.search.service.CountHotelService;
import com.dlim2012.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin
public class SearchController {

    private final SearchService searchService;
    private final CountHotelService countHotelService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint is working.");
    }

    @PostMapping("/hotel")
    public ResponseEntity<HotelSearchResponse> searchHotel(@RequestBody @Valid HotelSearchRequest hotelSearchRequest) {
        log.info("Hotel search requested.");
        try {
            return ResponseEntity.ok(searchService.search(hotelSearchRequest));
        } catch (IOException e) {
            log.error("Error occurred during hotel search: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/price")
    public ResponseEntity<List<PriceAggResponse>> aggPrice(@RequestBody PriceAggRequest request) {
        log.info("Price aggregation requested.");
        try {
            return ResponseEntity.ok(searchService.aggPrice(request));
        } catch (IOException e) {
            log.error("Error during price aggregation: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/hotel/{hotelId}/availability")
    public ResponseEntity<RoomsAvailabilityResponse> getRoomsAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody RoomsAvailabilityRequest request) {
        log.info("Checking availability for hotel ID: {}", hotelId);
        try {
            return ResponseEntity.ok(searchService.getRoomsAvailability(hotelId, request));
        } catch (IOException e) {
            log.error("Error checking room availability for hotel {}: {}", hotelId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/count/city")
    public ResponseEntity<List<NumberResponse>> numHotelByCity(@RequestBody List<NumberByCityRequest> request) {
        log.info("Counting hotels by city.");
        try {
            return ResponseEntity.ok(countHotelService.numHotelByCity(request));
        } catch (IOException e) {
            log.error("Error during hotel count by city: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/count/property-type")
    public ResponseEntity<List<NumberResponse>> numHotelByPropertyType(@RequestBody List<NumberByPropertyTypeRequest> request) {
        log.info("Counting hotels by property type.");
        try {
            return ResponseEntity.ok(countHotelService.numHotelByPropertyType(request));
        } catch (IOException e) {
            log.error("Error during hotel count by property type: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
