package com.dlim2012.search.dto.hotelSearch;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchRequest {

    private Boolean useRecommended;

    // Address fields
    private String city;
    private String state;
    private String country;
    private Double latitude;
    private Double longitude;

    // Date fields
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    // Size fields
    @NotNull
    private Integer numAdult;
    
    @NotNull
    private Integer numChild;
    
    private Integer numBed;
    
    @NotNull
    private Integer numRoom;

    // Price fields
    private Long priceMin;
    private Long priceMax;

    // Filters
    private List<String> propertyTypes;
    private List<Integer> propertyRating;
    private List<String> hotelFacility;
    private List<String> roomsFacility;
}

