package com.megacity.vehicle_reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.megacity.vehicle_reservation.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Show login page
    @GetMapping("/login")
    public String login(HttpSession session, HttpServletResponse response) {
        // Check if the user is already logged in
        if (session.getAttribute("loggedInUser") != null) {
            String role = (String) session.getAttribute("userRole");
            // Add null check for role
            if (role != null) {
                if (role.equalsIgnoreCase("admin")) {
                    return "redirect:/admin-dashboard"; // Redirect to admin dashboard
                } else if (role.equalsIgnoreCase("customer")) {
                    return "redirect:/customer-dashboard"; // Redirect to customer dashboard
                }
            }
        }

        // Prevent browser from caching the login page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "login"; // Returns the login.html page
    }

    // Handle login form submission
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password,
                              @RequestParam String role, RedirectAttributes redirectAttributes, HttpSession session) {

        // Validate login credentials
        if (userService.validateLogin(username, password)) {
            // Get the user role from the database
            String userRole = userService.getUserRole(username);
            System.out.println("Role from form: " + role); // Debug logging
            System.out.println("Role from database: " + userRole); // Debug logging

            // Convert both roles to lowercase to avoid case sensitivity issues
            if (userRole != null && userRole.trim().toLowerCase().equals(role.trim().toLowerCase())) {
                // Set session attributes to store login status and role
                session.setAttribute("loggedInUser", username); // Store the username
                session.setAttribute("userRole", userRole); // Store the user role

                // If the role is "admin", redirect to the admin dashboard
                if (role.equalsIgnoreCase("admin")) {
                    return "redirect:/admin-dashboard"; // Redirect to admin dashboard
                }
                // If the role is "customer", redirect to the customer dashboard
                else if (role.equalsIgnoreCase("customer")) {
                    return "redirect:/customer-dashboard"; // Redirect to customer dashboard
                }
            } else {
                // If the role does not match, show error message
                redirectAttributes.addFlashAttribute("error", "Role mismatch");
                return "redirect:/login"; // Redirect to login page with error message
            }
        }

        // If login failed, show error message
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login"; // Redirect to login page with error message
    }

    // Show admin dashboard
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, HttpServletResponse response) {
        // Check if the user is logged in and has the admin role
        if (session.getAttribute("loggedInUser") == null || !"admin".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            return "redirect:/login"; // Redirect to login if not logged in or not an admin
        }

        // Prevent browser from caching the admin dashboard page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "admin-dashboard"; // Returns the admin-dashboard.html page
    }

    // Show customer dashboard
    @GetMapping("/customer-dashboard")
    public String customerDashboard(HttpSession session, HttpServletResponse response) {
        // Check if the user is logged in and has the customer role
        if (session.getAttribute("loggedInUser") == null || !"customer".equalsIgnoreCase((String) session.getAttribute("userRole"))) {
            return "redirect:/login"; // Redirect to login if not logged in or not a customer
        }

        // Prevent browser from caching the customer dashboard page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "customer-dashboard"; // Returns the customer-dashboard.html page
    }

    // Show registration page
    @GetMapping("/register")
    public String register() {
        return "register"; // Returns the register.html page
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String handleRegistration(@RequestParam String username, @RequestParam String password,
                                    @RequestParam String role) {
        userService.registerUser(username, password, role);
        return "redirect:/login"; // After registration, redirect to login page
    }

    // Logout the user and invalidate the session
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // Invalidate the session to log out the user
        session.invalidate();

        // Prevent browser from caching the login page
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "redirect:/login"; // Redirect to login page after logout
    }
}