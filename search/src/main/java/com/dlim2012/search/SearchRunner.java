package com.dlim2012.search;

import com.dlim2012.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Slf4j
@RequiredArgsConstructor
public class SearchRunner implements CommandLineRunner {

    private final SearchService searchService;

    @Override
    public void run(String... args) throws Exception {
        // Mock request for testing
        HotelSearchRequest request = new HotelSearchRequest();
        request.setCity("New York");

        // Call updated method
        searchService.search(request);
    }
}

