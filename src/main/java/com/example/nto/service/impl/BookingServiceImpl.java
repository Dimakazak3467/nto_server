package com.example.nto.service.impl;

import com.example.nto.entity.Booking;
import com.example.nto.entity.Employee;
import com.example.nto.entity.Place;
import com.example.nto.repository.BookingRepository;
import com.example.nto.repository.PlaceRepository;
import com.example.nto.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PlaceRepository placeRepository;

    @Value("${booking.days-ahead:3}")
    private int daysAhead;

    @Override
    public Map<String, Map<String, Object>> getUserBookings(Employee employee) {
        Map<String, Map<String, Object>> bookingMap = new LinkedHashMap<>();

        List<Booking> bookings = bookingRepository.findByEmployee(employee);
        for (Booking booking : bookings) {
            String dateStr = booking.getDate().toString();
            Map<String, Object> info = new HashMap<>();
            info.put("id", booking.getPlace().getId()); // ID места, а не бронирования!
            info.put("place", booking.getPlace().getPlace());
            bookingMap.put(dateStr, info);
        }

        return bookingMap;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getAvailableBookings() {
        LocalDate today = LocalDate.now();
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();

        // Получаем все места
        List<Place> allPlaces = placeRepository.findAll();

        // Для текущей даты + daysAhead (по умолчанию 3, итого 4 дня)
        for (int i = 0; i <= daysAhead; i++) {
            LocalDate date = today.plusDays(i);
            String dateStr = date.toString();

            // Получаем ВСЕ бронирования на эту дату (любых пользователей)
            List<Booking> bookedPlaces = bookingRepository.findByDateBetween(date, date);
            Set<Long> bookedPlaceIds = bookedPlaces.stream()
                    .map(b -> b.getPlace().getId())
                    .collect(Collectors.toSet());

            // Фильтруем свободные места (исключаем занятые ЛЮБЫМ пользователем)
            List<Map<String, Object>> availablePlaces = allPlaces.stream()
                    .filter(place -> !bookedPlaceIds.contains(place.getId()))
                    .map(place -> {
                        Map<String, Object> placeInfo = new HashMap<>();
                        placeInfo.put("id", place.getId());
                        placeInfo.put("place", place.getPlace());
                        return placeInfo;
                    })
                    .collect(Collectors.toList());

            result.put(dateStr, availablePlaces);
        }

        return result;
    }

    @Override
    @Transactional
    public void createBooking(Employee employee, LocalDate date, Long placeId) {
        // Проверяем, существует ли место
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        // Проверяем, не занято ли место на эту дату
        Optional<Booking> existingPlaceBooking = bookingRepository.findByDateAndPlace(date, place);
        if (existingPlaceBooking.isPresent()) {
            throw new BookingConflictException("Place already booked");
        }

        // Проверяем, нет ли у сотрудника бронирования на эту дату
        Optional<Booking> existingEmployeeBooking = bookingRepository.findByDateAndEmployee(date, employee);
        if (existingEmployeeBooking.isPresent()) {
            throw new BookingConflictException("Employee already has booking for this date");
        }

        // Создаем новое бронирование
        Booking booking = new Booking();
        booking.setDate(date);
        booking.setPlace(place);
        booking.setEmployee(employee);

        bookingRepository.save(booking);
    }

    public static class BookingConflictException extends RuntimeException {
        public BookingConflictException(String message) {
            super(message);
        }
    }
}