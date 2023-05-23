package com.areske.telegramwordly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {

    private Long id;
    private String name;
    private int games = 0;
    private int wins = 0;

}
