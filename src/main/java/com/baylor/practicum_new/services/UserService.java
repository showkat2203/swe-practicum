package com.baylor.practicum_new.services;

import com.baylor.practicum_new.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    public Optional<User> authenticateUser(String email, String password);

    User getUserById(Long userId);
    Optional<User> findByEmail(String email);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);
}
