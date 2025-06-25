package com.megacity.vehicle_reservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.megacity.vehicle_reservation.entity.Car;
import com.megacity.vehicle_reservation.entity.Customer;
import com.megacity.vehicle_reservation.service.CarService;
import com.megacity.vehicle_reservation.service.CustomerService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CarService carService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer-dashboard")
    public String customerDashboard(HttpSession session, HttpServletResponse response, Model model) {
        // Check if the user is logged in and has the customer role
        if (session.getAttribute("loggedInUser") == null || 
            !"customer".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            return "redirect:/login"; // Redirect to login if not logged in or not a customer
        }

        // Fetch all cars from the database
        List<Car> cars = carService.getAllCars();
        model.addAttribute("cars", cars);

        // Prevent browser from caching the customer dashboard page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "customer-dashboard"; // Returns the customer-dashboard.html page
    }

    @GetMapping("/getCars")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }
    
    @PostMapping("/bookCar/{carId}")
    public ResponseEntity<String> bookCar(@PathVariable Long carId, @RequestBody Customer customer) {
        Car car = carService.getCarById(carId);
        if (car == null || !car.isAvailable()) {
            return ResponseEntity.badRequest().body("Car not available");
        }

        customer.setCar(car); // Assign car to the customer
        customerService.addCustomer(customer);

        // Mark the car as unavailable
        car.setAvailable(false);
        carService.updateCar(car);

        return ResponseEntity.ok("Car booked successfully!");
    }


}
