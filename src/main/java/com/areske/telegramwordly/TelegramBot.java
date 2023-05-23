package com.areske.telegramwordly;

import com.areske.telegramwordly.config.BotConfig;
import com.areske.telegramwordly.service.GameService;
import com.areske.telegramwordly.service.ValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final GameService gameService;
    private final ValidatorService validatorService;
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().toLowerCase();
            long chatId = update.getMessage().getChatId();

            if (message.startsWith("report")) {
                sendMessage(validatorService.report(chatId, message.split(" ")[1]));
            } else {
                switch (message) {
                    case "/info" -> sendMessage(gameService.getInfo(chatId, update.getMessage()));
                    case "/start" -> sendMessage(gameService.startGame(chatId, update.getMessage().getChat().getFirstName()));
                    default -> sendMessage(gameService.sendWord(chatId, message));
                }
            }
        }
    }

    private void sendMessage(SendMessage sendMessage) throws TelegramApiException {
        execute(sendMessage);
    }
}
