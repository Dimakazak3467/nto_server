package com.example.nto.service;

import com.example.nto.entity.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * TODO: ДОРАБОТАТЬ в рамках задания
 * =================================
 * МОЖНО: Добавлять методы, аннотации, зависимости
 * НЕЛЬЗЯ: Изменять название класса и пакета
 */
public interface BookingService {

    Map<String, Map<String, Object>> getUserBookings(Employee employee);

    Map<String, List<Map<String, Object>>> getAvailableBookings();

    void createBooking(Employee employee, LocalDate date, Long placeId);
}