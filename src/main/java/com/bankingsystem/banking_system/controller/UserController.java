package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:4200", "https://baking-system-frontend.vercel.app"})
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
