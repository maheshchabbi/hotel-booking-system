package com.dlim2012.search.dto.hotelSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchResponseItem {
    private Integer hotelId;
    private String hotelName;
    private String propertyType;
    private String neighborhood;
    private String city;
    private String state;
    private String zipcode;
    private Double distance;
    private Integer numRoom;
    private Long totalPrice;
    private Integer maxFreeCancellationDays;
    private Integer noPrepaymentDays;
    private Boolean breakfast;
    private List<HotelSearchRooms> roomsList;
    private Double score;
}

