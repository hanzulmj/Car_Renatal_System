package com.megacity.vehicle_reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megacity.vehicle_reservation.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
	List<Car> findByAvailableTrue();
}