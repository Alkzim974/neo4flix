package com.neo4flix.userservice.service;

import com.neo4flix.userservice.model.User;
import com.neo4flix.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addFriend(String currentUsername, String friendUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur courant introuvable"));
        
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Ami introuvable"));

        // Vérification de ne pas s'ajouter soi-même
        if (currentUsername.equals(friendUsername)) {
            throw new RuntimeException("Vous ne pouvez pas vous ajouter vous-même");
        }

        currentUser.getFriends().add(friend);
        userRepository.save(currentUser);
    }

    public java.util.Set<User> getFriends(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return currentUser.getFriends();
    }

    public void removeFriend(String currentUsername, String friendUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        currentUser.getFriends().removeIf(friend -> friend.getUsername().equals(friendUsername));
        userRepository.save(currentUser);
    }
}
