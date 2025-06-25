package com.megacity.vehicle_reservation.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.megacity.vehicle_reservation.entity.Car;
import com.megacity.vehicle_reservation.entity.Driver;
import com.megacity.vehicle_reservation.service.CarService;
import com.megacity.vehicle_reservation.service.DriverService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarService carService;
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, HttpServletResponse response, Model model) {
        // Check if the user is logged in and has the admin role
        if (session.getAttribute("loggedInUser") == null || !"admin".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            return "redirect:/login"; // Redirect to login if not logged in or not an admin
        }

        // Fetch all drivers and cars from the database
        List<Driver> drivers = driverService.getAllDrivers();
        List<Car> cars = carService.getAllCars();

        // Add them to the model to pass to the view
        model.addAttribute("drivers", drivers);
        model.addAttribute("cars", cars);

        // Prevent browser from caching the admin dashboard page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "admin-dashboard"; // Returns the admin-dashboard.html page
    }


    @RequestMapping("/admin-dashboard")
    public String adminDashboard(Model model) {
        // Fetch all drivers from the database
        List<Driver> drivers = driverService.getAllDrivers();
        // Fetch all cars from the database
        List<Car> cars = carService.getAllCars();

        // Add them to the model to pass to the view
        model.addAttribute("drivers", drivers);
        model.addAttribute("cars", cars);

        return "admin-dashboard";  // Return the view name
    }



    @PostMapping("/addDriver")
    @ResponseBody
    public String addDriver(@RequestParam String name, @RequestParam String contactNumber) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setContactNumber(contactNumber);
        driverService.saveDriver(driver);

        // Return a success response in JSON format
        return "{\"status\": \"success\", \"message\": \"Driver added successfully\"}";
    }


    
    @RequestMapping("/getDrivers")
    @ResponseBody
    public List<Driver> getDrivers() {
        return driverService.getAllDrivers();  // Returns a list of drivers as JSON
    }


    @PostMapping("/addCar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCar(@RequestParam String model, @RequestParam String registrationNumber,
    												  @RequestParam BigDecimal price, @RequestParam boolean available, @RequestParam Long driverId) {
        Car car = new Car();
        car.setModel(model);
        car.setRegistrationNumber(registrationNumber);
        car.setPrice(price);
        car.setAvailable(available);
        Driver driver = driverService.getDriverById(driverId);
        car.setDriver(driver);
        carService.saveCar(car);

        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Car details added successfully");
        response.put("driver", driver);  // Include driver information in the response

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/getCars")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }
    
    @GetMapping("/getCar/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return car != null ? ResponseEntity.ok(car) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/deleteCar/{id}")
    public ResponseEntity<Map<String, String>> deleteCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
    @PutMapping("/updateCar/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCar(
        @PathVariable Long id,
        @RequestParam String model,
        @RequestParam String registrationNumber,
        @RequestParam BigDecimal price,
        @RequestParam boolean available,
        @RequestParam Long driverId
    ) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Car not found"));
        }

        // Update the car details
        car.setModel(model);
        car.setRegistrationNumber(registrationNumber);
        car.setPrice(price);
        car.setAvailable(available);
        Driver driver = driverService.getDriverById(driverId);
        car.setDriver(driver);

        // Save the updated car
        carService.saveCar(car);

        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Car details updated successfully");
        response.put("driver", driver);  // Include driver information in the response

        return ResponseEntity.ok(response);
    }
}
