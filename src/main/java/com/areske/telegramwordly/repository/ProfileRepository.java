package com.areske.telegramwordly.repository;

import com.areske.telegramwordly.model.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByName(String name);

    Optional<Profile> findByChatId(Long id);

}
