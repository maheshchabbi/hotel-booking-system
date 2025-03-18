package com.dlim2012.clients.cassandra.repository;

import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.entity.BookingMainStatus;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingArchiveByUserIdRepository
        extends CassandraRepository<BookingArchiveByUserId, String> {  // Ensure correct ID type

    // Query must include the partition key (userId) for AWS Keyspaces performance
    List<BookingArchiveByUserId> findByUserIdAndMainStatusAndEndDateTimeGreaterThanEqual(
            String userId, BookingMainStatus mainStatus, LocalDateTime startDateTime);

    List<BookingArchiveByUserId> findByUserIdAndMainStatusAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThan(
            String userId, BookingMainStatus mainStatus, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
