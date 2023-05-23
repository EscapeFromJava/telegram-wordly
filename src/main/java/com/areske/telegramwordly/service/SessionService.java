package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.Profile;
import com.areske.telegramwordly.model.Session;
import com.areske.telegramwordly.model.entity.Word;
import com.areske.telegramwordly.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final ProfileService PROFILE_SERVICE;
    private final WordRepository WORD_REPOSITORY;
    private final Map<Long, Session> SESSIONS = new HashMap<>();

    public Optional<Session> getSession(Long chatId) {
        return Optional.ofNullable(SESSIONS.get(chatId));
    }

    @Transactional(readOnly = true)
    public Session createSession(Long chatId, String name) {
        Word randomWord = WORD_REPOSITORY.getRandomWord();
        Profile profile = PROFILE_SERVICE.createProfile(chatId, name);
        Session session = new Session(profile, randomWord.getWord());
        SESSIONS.put(chatId, session);
        return session;
    }

    public void clearSession(Session session, boolean isWin) {
        if (isWin) {
            PROFILE_SERVICE.incrementWins(session.getProfile());
        }
        session.getWords().clear();
        session.getExactly().clear();
        session.getNotExactly().clear();
    }

    public void decrementCounter(Session currSession) {
        currSession.setCounter(currSession.getCounter() - 1);
    }

}
