package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.entity.Profile;
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

    private final ProfileService profileService;
    private final WordRepository wordRepository;
    private final Map<Long, Session> SESSIONS = new HashMap<>();

    public Optional<Session> getSession(Long chatId) {
        return Optional.ofNullable(SESSIONS.get(chatId));
    }

    @Transactional
    public Session createSession(Long chatId, String name) {
        Word randomWord = getWord();
        Profile profile = profileService.createProfile(chatId, name);
        Session session = new Session(profile, randomWord.getWord());
        SESSIONS.put(chatId, session);
        return session;
    }

    public void clearSession(Session session, boolean isWin) {
        if (isWin) {
            profileService.incrementWins(session.getProfile().getName());
        }
        session.getWords().clear();
        session.getExactly().clear();
        session.getNotExactly().clear();

        String currentWord = session.getCurrentWord();
        Word word = wordRepository.findByWord(currentWord).get();
        word.setCountWin(word.getCountWin() + 1);
        wordRepository.save(word);

    }

    public void decrementCounter(Session currSession) {
        currSession.setCounter(currSession.getCounter() - 1);
    }

    @Transactional
    public void updateWord(Session session) {
        Word randomWord = getWord();

        session.setCurrentWord(randomWord.getWord());
    }

    private Word getWord() {
        Word randomWord = wordRepository.getRandomWord();
        randomWord.setCountTotal(randomWord.getCountTotal() + 1);
        wordRepository.save(randomWord);
        return randomWord;
    }
}
