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
        return ResponseEntity.ok(searchService.search(hotelSearchRequest));
    }

    @PostMapping("/price")
    public ResponseEntity<List<PriceAggResponse>> aggPrice(@RequestBody PriceAggRequest request) {
        log.info("Price aggregation requested.");
        return ResponseEntity.ok(searchService.aggPrice(request));
    }

    @PostMapping("/hotel/{hotelId}/availability")
    public ResponseEntity<RoomsAvailabilityResponse> getRoomsAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody RoomsAvailabilityRequest request) {
        log.info("Checking availability for hotel ID: {}", hotelId);
        return ResponseEntity.ok(searchService.getRoomsAvailability(hotelId, request));
    }

    @PostMapping("/count/city")
    public ResponseEntity<List<NumberResponse>> numHotelByCity(@RequestBody List<NumberByCityRequest> request) {
        log.info("Counting hotels by city.");
        return ResponseEntity.ok(countHotelService.numHotelByCity(request));
    }

    @PostMapping("/count/property-type")
    public ResponseEntity<List<NumberResponse>> numHotelByPropertyType(@RequestBody List<NumberByPropertyTypeRequest> request) {
        log.info("Counting hotels by property type.");
        return ResponseEntity.ok(countHotelService.numHotelByPropertyType(request));
    }
}

