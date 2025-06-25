package com.megacity.vehicle_reservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megacity.vehicle_reservation.entity.Car;
import com.megacity.vehicle_reservation.repository.CarRepository;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public void saveCar(Car car) {
        carRepository.save(car);
    }
    
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
    
    // Fetch available cars
    public List<Car> getAvailableCars() {
        return carRepository.findByAvailableTrue(); // Assuming the 'available' field is a boolean indicating availability
    }
    
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }
    
    public Car updateCar(Car car) {
        return carRepository.save(car); // Save the updated car
    }
    
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }


}
