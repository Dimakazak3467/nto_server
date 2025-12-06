package com.example.nto.service;

import com.example.nto.entity.Employee;

import java.util.Optional;

/**
 * TODO: ДОРАБОТАТЬ в рамках задания
 * =================================
 * МОЖНО: Добавлять методы, аннотации, зависимости
 * НЕЛЬЗЯ: Изменять название класса и пакета
 */
public interface EmployeeService {

    Optional<Employee> findByCode(String code);
}