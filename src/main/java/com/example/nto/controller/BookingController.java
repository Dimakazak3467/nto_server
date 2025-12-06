package com.example.nto.controller;

import com.example.nto.entity.Employee;
import com.example.nto.service.BookingService;
import com.example.nto.service.EmployeeService;
import com.example.nto.service.impl.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: ДОРАБОТАТЬ в рамках задания
 * =================================
 * МОЖНО: Добавлять методы, аннотации, зависимости
 * НЕЛЬЗЯ: Изменять название класса и пакета
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final EmployeeService employeeService;
    private final BookingService bookingService;

    @GetMapping("/{code}/auth")
    public ResponseEntity<Void> authenticate(@PathVariable String code) {
        try {
            Optional<Employee> employee = employeeService.findByCode(code);
            if (employee.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{code}/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable String code) {
        try {
            Optional<Employee> employeeOpt = employeeService.findByCode(code);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Employee employee = employeeOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("name", employee.getName());
            response.put("photoUrl", employee.getPhotoUrl());
            response.put("booking", bookingService.getUserBookings(employee));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{code}/booking")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getAvailableBookings(@PathVariable String code) {
        try {
            Optional<Employee> employee = employeeService.findByCode(code);
            if (employee.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, List<Map<String, Object>>> availableBookings = bookingService.getAvailableBookings();
            return ResponseEntity.ok(availableBookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{code}/book")
    public ResponseEntity<Void> createBooking(
            @PathVariable String code,
            @RequestBody BookingRequest request) {
        try {
            Optional<Employee> employeeOpt = employeeService.findByCode(code);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            LocalDate date = LocalDate.parse(request.getDate());
            bookingService.createBooking(employeeOpt.get(), date, request.getPlaceId());

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (BookingServiceImpl.BookingConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO для запроса бронирования
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BookingRequest {
        private String date;
        private Long placeId;
    }
}