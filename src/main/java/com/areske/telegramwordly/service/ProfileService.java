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

    private final ProfileRepository profileRepository;

    public Profile createProfile(Long chatId, String name) {
        Profile profile = new Profile(chatId, name);
        profileRepository.save(profile);
        return profile;
    }

    public Optional<Profile> getProfile(Long chatId) {
        return profileRepository.findByChatId(chatId);
    }

    @Transactional
    public void incrementWins(String name) {
        Optional<Profile> optionalProfile = profileRepository.findByName(name);
        if (optionalProfile.isPresent()) {
            Profile currProfile = optionalProfile.get();
            currProfile.setWins(currProfile.getWins() + 1);
            profileRepository.save(currProfile);
        }
    }

    @Transactional
    public void incrementGames(String name) {
        Optional<Profile> optionalProfile = profileRepository.findByName(name);
        if (optionalProfile.isPresent()) {
            Profile currProfile = optionalProfile.get();
            currProfile.setGames(currProfile.getGames() + 1);
            profileRepository.save(currProfile);
        }
    }

}
