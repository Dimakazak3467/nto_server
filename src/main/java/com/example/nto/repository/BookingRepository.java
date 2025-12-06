package com.example.nto.repository;

import com.example.nto.entity.Booking;
import com.example.nto.entity.Employee;
import com.example.nto.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TODO: ДОРАБОТАТЬ в рамках задания
 * =================================
 * МОЖНО: Добавлять методы, аннотации, зависимости
 * НЕЛЬЗЯ: Изменять название класса и пакета
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByEmployee(Employee employee);

    List<Booking> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<Booking> findByDateAndPlace(LocalDate date, Place place);

    Optional<Booking> findByDateAndEmployee(LocalDate date, Employee employee);
}