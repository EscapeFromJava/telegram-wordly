package com.areske.telegramwordly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    private Long chatId;
    private String name;
    private int games = 0;
    private int wins = 0;

    public Profile(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

}
