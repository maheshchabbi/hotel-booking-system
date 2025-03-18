package com.dlim2012.booking.service;

import com.dlim2012.clients.mysql_booking.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import static com.dlim2012.clients.cache.CacheConfig.bookingIdKeyName;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    @CachePut(cacheNames = "booking", key = "#booking.id")
    public Booking cacheBookingForTTL(Booking booking) {
        log.info("Caching booking {} in Redis", booking);
        return booking;
    }

    @CacheEvict(cacheNames = "booking", key = "#booking.id")
    public void cacheBookingEvict(Booking booking) {
        log.info("Evicting booking {} from Redis", booking);
    }

    @CachePut(cacheNames = bookingIdKeyName, key = "#bookingId")
    public String cacheBookingIdForTTL(Long bookingId) {
        log.info("Caching bookingId {} in Redis", bookingId);
        return "";
    }

    @CacheEvict(cacheNames = bookingIdKeyName, key = "#bookingId")
    public void cacheBookingIdEvict(Long bookingId) {
        log.info("Evicting bookingId {} from Redis", bookingId);
    }

    @CachePut(cacheNames = "hotel-booking", key = "{#booking.id, #booking.hotelManagerId}")
    @CacheEvict(cacheNames = "user-booking", key = "{#booking.id, #booking.userId}")
    public Booking putBooking(Booking booking) {
        log.info("Updating hotel-booking cache and evicting user-booking cache for booking {}", booking);
        return booking;
    }
}
