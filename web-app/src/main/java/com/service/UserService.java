package com.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.model.User;

@Service
public class UserService {

    private Map<String, User> users = new HashMap<>();

    public boolean authenticate(User user) {
        User existingUser = users.get(user.getUsername());
        return existingUser != null && existingUser.getPassword().equals(user.getPassword());
    }

    public boolean register(User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }
}