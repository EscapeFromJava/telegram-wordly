package com.areske.telegramwordly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    private String name;
    private int games = 0;
    private int wins = 0;

    public Profile(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

}
