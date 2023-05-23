package com.areske.telegramwordly;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private String WORD = "";
    private final List<String> WORDS = List.of("лоскут", "медведь", "ожог", "фартук", "антиквар", "Норвегия", "пешка", "наручник", "суслик", "морщина", "соль", "киста", "киностудия", "берег", "носорог", "ком", "бард", "кайма", "терка", "кимоно", "пробка", "сверло", "золото", "егерь", "Венесуэла", "поплавок", "вспышка", "кисель", "вентиль", "складка", "ядро", "огород", "енот", "олень", "кастет", "порох", "уран", "Бриджтаун", "клеймо", "спирт", "кляча", "кларнет", "Дагестан", "зерно", "корсет", "время", "дверка", "грунт", "наркотик", "глина", "клен", "шнурок", "кипарис", "караул", "навоз", "дирижер", "сфинкс", "болт", "восход", "гарнизон", "бант", "капиталист", "каратэ", "монгол", "семя", "злоумышленник", "шишка", "флейта", "чулан", "выгон", "горизонт", "марганец", "кадык", "купальник", "камзол", "керосин", "кукуруза", "вершина", "плесень", "лотос", "пылесос", "залог", "балерина", "караулка", "коса", "титан", "пуля", "нога", "дракон", "газопровод", "уксус", "бизнесмен", "белладонна", "бомба", "Бразилия", "цветок", "ножны", "журавль", "пламя");
    private Set<String> PLAYER = new HashSet<>();
    private Set<Character> EXACTLY = new HashSet<>();
    private Set<Character> NOT_EXACTLY = new HashSet<>();
    private final Map<Long, PlayerProfile> SESSIONS = new HashMap<>();

    private PlayerProfile activeProfile;

    private int counter;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/info" -> getInfoByChatId(chatId, update.getMessage());
                case "/start" -> startCommandReceived(chatId, update.getMessage());
                default -> sendWord(chatId, update.getMessage().getText().toLowerCase());
            }
        }
    }

    @SneakyThrows
    private void getInfoByChatId(long chatId, Message message) {
        String answer = "Привет " + message.getChat().getFirstName();
        if (!SESSIONS.containsKey(chatId)) {
            answer += "\nСтатистика отсутствует";
            answer += "\nПохоже что ты еще не играл в нашу игру. Введи команду /start чтобы начать";
        } else {
            PlayerProfile profile = SESSIONS.get(chatId);
            answer += "\nСтатистика игр: ";
            answer += "\nВсего игр сыграно: " + profile.getGames();
            answer += "\nПобед: " + profile.getWins();
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(answer);
        execute(sendMessage);
    }

    private void startGameSession(Message message) {
        if (!SESSIONS.containsKey(message.getChatId())) {
            PlayerProfile profile = new PlayerProfile();
            profile.setId(message.getChatId());
            profile.setName(message.getChat().getFirstName());
            profile.setGames(profile.getGames() + 1);
            SESSIONS.put(message.getChatId(), profile);
            activeProfile = profile;
        } else {
            PlayerProfile profile = SESSIONS.get(message.getChatId());
            profile.setGames(profile.getGames() + 1);
            SESSIONS.put(message.getChatId(), profile);
            activeProfile = profile;
        }
    }

    @SneakyThrows
    private void startCommandReceived(Long chatId, Message message) {
        startGameSession(message);
        PLAYER = new HashSet<>();
        EXACTLY = new HashSet<>();
        NOT_EXACTLY = new HashSet<>();
        WORD = WORDS.get(new Random().nextInt(WORDS.size()));
        counter = WORD.length();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Игра началась" + "\nДлина слова: " + WORD.length() + "\nКоличество попытк: " + counter);
        execute(sendMessage);
    }

    @SneakyThrows
    private void sendWord(Long chatId, String word) {
        String answer;

        if (WORD.equals("")) {
            answer = "Введите команду /start чтобы начать игру";
        } else if (word.equalsIgnoreCase(WORD)) {
            answer = "Ты выиграл";
            WORD = "";
            activeProfile.setWins(activeProfile.getWins() + 1);
            refreshProfile();
        } else if (word.length() != WORD.length()) {
            answer = "Слово должно быть длиной " + WORD.length() + " символов";
        } else {
            counter--;

            if (counter > 0) {
                PLAYER.add(word);

                char[] chars = WORD.toCharArray();

                for (String playerWord : PLAYER) {
                    for (int i = 0; i < chars.length; i++) {
                        if (playerWord.charAt(i) == chars[i]) {
                            EXACTLY.add(chars[i]);
                            chars[i] = String.valueOf(chars[i]).toUpperCase().charAt(0);
                        }
                        if (WORD.contains(String.valueOf(playerWord.charAt(i))) && playerWord.charAt(i) != chars[i]) {
                            NOT_EXACTLY.add(playerWord.charAt(i));
                            for (Character character : EXACTLY) {
                                NOT_EXACTLY.remove(character);
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

                answer = String.valueOf(chars) + "\nТочные буквы: " + EXACTLY + "\nНеточные буквы: " + NOT_EXACTLY + "\nОсталось попыток: " + counter;

            } else {
                answer = "Ты проиграл!";
                WORD = "";
                refreshProfile();
            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(answer);
        execute(sendMessage);
    }

    private void refreshProfile() {
        PLAYER = new HashSet<>();
        EXACTLY = new HashSet<>();
        NOT_EXACTLY = new HashSet<>();
        SESSIONS.put(activeProfile.getId(), activeProfile);
    }
}
