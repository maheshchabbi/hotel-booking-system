package com.dlim2012.clients.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@UserDefinedType("booking_archive_room")
public class BookingArchiveRoom {

    @CassandraType(type = CassandraType.Name.INT)
    private Integer roomsId;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String roomsName;

    @CassandraType(type = CassandraType.Name.BIGINT)
    private Long roomId;

    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private LocalDateTime startDateTime;

    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private LocalDateTime endDateTime;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String guestName;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String guestEmail;
}
