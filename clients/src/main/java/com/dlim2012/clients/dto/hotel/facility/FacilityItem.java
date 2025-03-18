package com.dlim2012.clients.dto.hotel.facility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityItem {
    private String name;
    private String description;
    // Add other fields as required...
}
