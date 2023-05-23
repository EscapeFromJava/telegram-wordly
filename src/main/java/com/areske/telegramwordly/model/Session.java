package com.areske.telegramwordly.model;

import com.areske.telegramwordly.model.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    private Profile profile;
    private String currentWord;
    private int counter = 0;
    private Set<String> words = new HashSet<>();
    private Set<Character> exactly = new HashSet<>();
    private Set<Character> notExactly = new HashSet<>();

    public Session(Profile profile, String currentWord) {
        this.profile = profile;
        this.currentWord = currentWord;
    }

}
