package com.areske.telegramwordly.service;

import com.areske.telegramwordly.model.response.YandexValidateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ValidatorService {

    @Value("${yandex.api.key}")
    private String apiKey;
    private final WebClient webClient;

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

}
