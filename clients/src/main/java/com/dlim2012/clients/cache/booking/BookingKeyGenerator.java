package com.dlim2012.clients.cache.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class BookingKeyGenerator {

    private final ObjectMapper objectMapper;

    // Default constructor for cases where no ObjectMapper is provided.
    public BookingKeyGenerator() {
        this.objectMapper = new ObjectMapper();
    }

    // Overloaded constructor to accept an ObjectMapper.
    public BookingKeyGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    // Add your key generation methods here...
}
