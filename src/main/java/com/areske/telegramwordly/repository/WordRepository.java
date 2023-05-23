package com.areske.telegramwordly.repository;

import com.areske.telegramwordly.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM words WHERE is_valid IS TRUE ORDER BY random() LIMIT 1")
    Word getRandomWord();

    Optional<Word> findByWord(String word);
}
