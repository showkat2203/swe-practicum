package com.baylor.practicum_new.controller;

import com.baylor.practicum_new.dto.LoginRequestDTO;
import com.baylor.practicum_new.dto.UserResponseDTO;
import com.baylor.practicum_new.entities.User;
import com.baylor.practicum_new.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String userEmail = user.getEmail();

        try {
            User savedUser = userService.createUser(user);
            return new ResponseEntity<>(Collections.singletonMap("userId", savedUser.getUserId()), HttpStatus.CREATED);
        } catch (RuntimeException e) {

            if ("Email already exists".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        Optional<User> user = userService.authenticateUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        if (user.isPresent()) {
            UserResponseDTO userResponse = new UserResponseDTO();
            userResponse.setUserId(user.get().getUserId());
            userResponse.setName(user.get().getName());
            userResponse.setEmail(user.get().getEmail());

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId){
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long userId,
                                           @RequestBody User user){
        user.setUserId(userId);
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>("User successfully deleted!", HttpStatus.OK);
    }
}