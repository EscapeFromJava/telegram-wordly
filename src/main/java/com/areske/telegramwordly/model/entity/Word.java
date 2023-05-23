package com.areske.telegramwordly.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    @Column(name = "count_win", columnDefinition = "integer default 0")
    private Integer countWin;
    @Column(name = "count_total", columnDefinition = "integer default 0")
    private Integer countTotal;
}
