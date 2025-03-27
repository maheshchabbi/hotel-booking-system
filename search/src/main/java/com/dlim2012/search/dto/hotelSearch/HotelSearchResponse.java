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
public class HotelSearchResponse {
    private List<HotelSearchResponseItem> hotelList;
    private Integer numResults;
    private Long minPrice;
    private Long maxPrice;
}

