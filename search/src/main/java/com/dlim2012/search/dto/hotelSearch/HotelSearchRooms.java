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
public class HotelSearchRooms {
    private Integer roomsId;
    private String displayName;
    private Integer maxAdult;
    private Integer maxChild;
    private Integer numBed;
    private Integer recommended;
    private Integer quantity;
    private Long price;
    private List<BedInfo> bedInfoList;
}

