package com.megacity.vehicle_reservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megacity.vehicle_reservation.entity.Driver;
import com.megacity.vehicle_reservation.repository.DriverRepository;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}
