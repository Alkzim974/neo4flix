package com.neo4flix.userservice.service;

import com.neo4flix.userservice.dto.UserSummaryDto;
import com.neo4flix.userservice.model.User;
import com.neo4flix.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public UserSummaryDto getProfileSummary(String username) {
        return toSummaryDto(getProfile(username));
    }

    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public void addFriend(String currentUsername, String friendUsername) {
        if (currentUsername.equals(friendUsername)) {
            throw new RuntimeException("Vous ne pouvez pas vous ajouter vous-même");
        }
        // Vérifie que les deux utilisateurs existent
        userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur courant introuvable"));
        userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Ami introuvable : " + friendUsername));

        // Crée la relation directement avec Cypher MERGE (évite les doublons)
        userRepository.createFriendship(currentUsername, friendUsername);
    }

    public List<UserSummaryDto> getFriends(String currentUsername) {
        return userRepository.findFriendsByUsername(currentUsername).stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public void removeFriend(String currentUsername, String friendUsername) {
        userRepository.deleteFriendship(currentUsername, friendUsername);
    }

    private UserSummaryDto toSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
