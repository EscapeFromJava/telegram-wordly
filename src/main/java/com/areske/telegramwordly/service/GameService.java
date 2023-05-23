package com.areske.telegramwordly.service;


import com.areske.telegramwordly.model.Session;
import com.areske.telegramwordly.model.entity.Profile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SessionService SESSION_SERVICE;
    private final ProfileService PROFILE_SERVICE;
    private final ValidatorService VALIDATOR_SERVICE;

    @SneakyThrows
    public SendMessage sendWord(Long chatId, String word) {
        Session currSession = SESSION_SERVICE.getSession(chatId).get();
        String answer;

        if (!VALIDATOR_SERVICE.validateWord(word)) {
            answer = "Ваше слово не прошло валидацию";
        } else if (currSession.getCurrentWord().equals("")) {
            answer = "Введите команду /start чтобы начать игру";
        } else if (word.equalsIgnoreCase(currSession.getCurrentWord())) {
            answer = "Ты выиграл";
            SESSION_SERVICE.clearSession(currSession, true);
        } else if (word.length() != currSession.getCurrentWord().length()) {
            answer = incorrectWordLengthAnswer(currSession);
        } else {
            SESSION_SERVICE.decrementCounter(currSession);

            if (currSession.getCounter() > 0) {
                currSession.getWords().add(word);

                char[] chars = currSession.getCurrentWord().toCharArray();

                for (String playerWord : currSession.getWords()) {
                    for (int i = 0; i < chars.length; i++) {

                        if (playerWord.charAt(i) == chars[i]) {
                            currSession.getExactly().add(chars[i]);
                            chars[i] = String.valueOf(chars[i]).toUpperCase().charAt(0);
                        }

                        if (currSession.getCurrentWord().contains(String.valueOf(playerWord.charAt(i))) && playerWord.charAt(i) != chars[i]) {
                            currSession.getNotExactly().add(playerWord.charAt(i));
                            for (Character character : currSession.getExactly()) {
                                currSession.getNotExactly().remove(character);
                            }
                        }
                    }
                }

                for (int i = 0; i < chars.length; i++) {
                    String currentChar = String.valueOf(chars[i]);
                    if (currentChar.equals(currentChar.toLowerCase())) {
                        chars[i] = '*';
                    }
                }

                answer = String.valueOf(chars)
                        + "\nТочные буквы: " + currSession.getExactly()
                        + "\nНеточные буквы: " + currSession.getNotExactly()
                        + "\nОсталось попыток: " + currSession.getCounter();

            } else {
                answer = "Ты проиграл!"
                        + "\nОтвет: " + currSession.getCurrentWord()
                        + "\nЕсли слово не прошло валидацию, то введите команду report <word> и исключите его из словаря"
                        + "\nПример: report яблоко";
                SESSION_SERVICE.clearSession(currSession, false);
            }
        }
        return createMessage(chatId, answer);
    }

    private static String incorrectWordLengthAnswer(Session currSession) {
        return "Слово должно быть длиной " + currSession.getCurrentWord().length() + " символов";
    }

    @SneakyThrows
    public SendMessage startGame(Long chatId, String name) {
        Session session = checkSession(chatId, name);

        String currentWord = session.getCurrentWord();
        session.setCounter(currentWord.length());

        String answer = "Игра началась"
                + "\nДлина слова: " + currentWord.length()
                + "\nКоличество попытк: " + currentWord.length();
        return createMessage(chatId, answer);
    }

    @SneakyThrows
    private Session checkSession(Long chatId, String name) {
        Optional<Session> optionalSession = SESSION_SERVICE.getSession(chatId);
        if (optionalSession.isPresent()) {
            Session session = optionalSession.get();
            PROFILE_SERVICE.incrementGames(name);
            SESSION_SERVICE.updateWord(session);
            return session;
        }
        return SESSION_SERVICE.createSession(chatId, name);
    }

    @SneakyThrows
    public SendMessage getInfo(Long chatId, Message message) {
        StringBuilder answer = new StringBuilder();
        answer.append("Привет ").append(message.getChat().getFirstName());
        Optional<Profile> optionalProfile = PROFILE_SERVICE.getProfile(chatId);
        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            answer.append("\nСтатистика игр: ");
            answer.append("\nВсего игр сыграно: ").append(profile.getGames());
            answer.append("\nПобед: ").append(profile.getWins());
        } else {
            answer.append("\nСтатистика отсутствует");
            answer.append("\nПохоже что ты еще не играл в нашу игру. Введи команду /start чтобы начать");
        }
        return createMessage(chatId, String.valueOf(answer));
    }

    public SendMessage createMessage(Long chatId, String answer) throws TelegramApiException {
        return new SendMessage(String.valueOf(chatId), answer);
    }

}
