package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {
    //todo переделать на БД
    private final Map<Long, Profile> PROFILES = new HashMap<>();

    public Profile createProfile(Long chatId, String name) {
        Profile profile = new Profile(chatId, name);
        PROFILES.put(chatId, profile);
        return profile;
    }

    public Optional<Profile> getProfile(Long chatId) {
        return Optional.ofNullable(PROFILES.get(chatId));
    }

    public void incrementWins(Profile profile) {
        profile.setWins(profile.getWins() + 1);
    }

    public void incrementGames(Profile profile) {
        profile.setGames(profile.getGames() + 1);
    }

}
