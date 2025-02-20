package com.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.model.User;

@Service
public class UserService {

    // Utilisation d'une map en mémoire pour simuler une base de données
    private Map<String, User> users = new HashMap<>();

    // Authentifier l'utilisateur
    public boolean authenticate(User user) {
        User existingUser = users.get(user.getUsername());
        return existingUser != null && existingUser.getPassword().equals(user.getPassword());
    }

    // Inscrire un nouvel utilisateur
    public boolean register(User user) {
        if (users.containsKey(user.getUsername())) {
            return false;  // Si l'utilisateur existe déjà, ne pas permettre l'inscription
        }
        users.put(user.getUsername(), user);  // Enregistrer l'utilisateur en mémoire
        return true;
    }
}