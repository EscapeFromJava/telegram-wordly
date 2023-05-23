package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.entity.Word;
import com.areske.telegramwordly.model.response.YandexValidateDto;
import com.areske.telegramwordly.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidatorService {

    @Value("${yandex.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final WordRepository wordRepository;

    public boolean validateWord(String word) {
        YandexValidateDto response = webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey)
                        .queryParam("lang", "ru-ru")
                        .queryParam("text", word).build())
                .retrieve()
                .bodyToMono(YandexValidateDto.class)
                .block();
        if (!response.getDef().isEmpty()) {
            return response.getDef().get(0).getText().equalsIgnoreCase(word);
        }
        return false;
    }

    @Transactional
    public SendMessage report(Long chatId, String word) {
        String answer;
        Optional<Word> optionalWord = wordRepository.findByWord(word);
        if (optionalWord.isPresent()) {
            Word currWord = optionalWord.get();
            currWord.setIsValid(false);
            wordRepository.save(currWord);
            answer = "Слово [" + word + "] было исключено из словаря";
        } else {
            answer = "Слово не найдено";
        }
        return new SendMessage(String.valueOf(chatId), answer);
    }
}
