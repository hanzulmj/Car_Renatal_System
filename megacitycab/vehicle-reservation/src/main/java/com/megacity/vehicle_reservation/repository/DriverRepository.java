package com.megacity.vehicle_reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megacity.vehicle_reservation.entity.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {}
