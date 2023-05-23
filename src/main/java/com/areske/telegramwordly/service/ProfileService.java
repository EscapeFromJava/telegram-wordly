package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.entity.Profile;
import com.areske.telegramwordly.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository PROFILE_REPOSITORY;

    public Profile createProfile(Long chatId, String name) {
        Profile profile = new Profile(chatId, name);
        PROFILE_REPOSITORY.save(profile);
        return profile;
    }

    public Optional<Profile> getProfile(Long chatId) {
        return PROFILE_REPOSITORY.findByChatId(chatId);
    }

    @Transactional
    public void incrementWins(String name) {
        Optional<Profile> optionalProfile = PROFILE_REPOSITORY.findByName(name);
        if (optionalProfile.isPresent()) {
            Profile currProfile = optionalProfile.get();
            currProfile.setWins(currProfile.getWins() + 1);
            PROFILE_REPOSITORY.save(currProfile);
        }
    }

    @Transactional
    public void incrementGames(String name) {
        Optional<Profile> optionalProfile = PROFILE_REPOSITORY.findByName(name);
        if (optionalProfile.isPresent()) {
            Profile currProfile = optionalProfile.get();
            currProfile.setGames(currProfile.getGames() + 1);
            PROFILE_REPOSITORY.save(currProfile);
        }
    }

}
