    package com.resume.backend.config;

    import com.resume.backend.service.TelegramBotService;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.telegram.telegrambots.meta.TelegramBotsApi;
    import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
    import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

    @Configuration
    public class TelegramConfig {

        @Bean
        public TelegramBotsApi telegramBotsApi(TelegramBotService botService) throws TelegramApiException {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(botService);
            return api;
        }
    }