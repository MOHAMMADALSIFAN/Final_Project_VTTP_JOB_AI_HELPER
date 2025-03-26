package com.resume.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    @Autowired
    private  JdbcTemplate jdbcTemplate;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
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

            if (messageText.startsWith("/start ")) {
                String token = messageText.substring(7).trim();
                if (!token.isEmpty()) {
                    String userId = getUserIdFromToken(token);
                    if (userId != null) {
                        storeChatIdForUser(userId, chatId);
  
                        sendTextMessage(chatId, "Welcome to Job AI Helper Bot! You are now connected. "
                                + "You will receive your resume when it's generated.");
                        deleteToken(token);
                    } else {
                        sendTextMessage(chatId, "Invalid or expired token. Please generate a new QR code from the web app.");
                    }
                } else {
                    sendTextMessage(chatId, "Welcome to Job AI Helper Bot! Please scan a QR code from the web app to connect your account.");
                }
            } else if (messageText.equals("/help")) {
                sendTextMessage(chatId, "This bot allows you to receive your generated resume as a PDF. "
                        + "Scan the QR code from the web app to connect your account.");
            } else {
                sendTextMessage(chatId, "I don't understand that command. Type /help for assistance.");
            }
        }
    }

/**
 * Check if a user has connected Telegram
 */
public boolean hasTelegramConnection(String userId) {
    try {
        Long chatId = getChatIdForUser(userId);
        return chatId != null;
    } catch (Exception e) {
        return false;
    }
}

    /**
     * Generate a unique token for the user to link their Telegram account
     */
    public String generateLinkToken(String userId) {
        String token = UUID.randomUUID().toString();

        try {
            // Store the token in the database
            jdbcTemplate.update(
                "INSERT INTO telegram_tokens (token, user_id, created_at) VALUES (?, ?, NOW())",
                token, userId);
            
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            // If there was an error inserting, try using the temporary method
            return generateTempLinkToken(userId);
        }
    }
    
    /**
     * Generate a unique token for temporary/anonymous users
     * This should be used when there's no authenticated user or we can't store a regular token
     */
    public String generateTempLinkToken(String tempId) {
        String token = "temp_" + UUID.randomUUID().toString();
        
        try {
            System.out.println("Created temporary token: " + token + " for tempId: " + tempId);
            try {
                jdbcTemplate.update(
                    "INSERT INTO telegram_tokens (token, user_id, created_at) VALUES (?, ?, NOW())",
                    token, tempId);
            } catch (Exception ex) {
                System.out.println("Could not store temporary token in database: " + ex.getMessage());
            }
            
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return token; 
        }
    }

    /**
     * Send the resume PDF to the user's Telegram chat
     */
    public boolean sendResumePdf(String userId, byte[] pdfBytes, String fileName) {
        Long chatId = getChatIdForUser(userId);

        if (chatId == null) {
            System.out.println("No Telegram chat found for user: " + userId);
            return false;
        }

        try {
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId.toString());
            sendDocument.setDocument(new InputFile(new ByteArrayInputStream(pdfBytes), fileName));
            sendDocument.setCaption("Here is your generated resume!");
            execute(sendDocument);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to send a text message
     */
    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the user ID associated with a token
     */
    private String getUserIdFromToken(String token) {
        try {
            System.out.println("Looking up token: " + token);
            
            String userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM telegram_tokens WHERE token = ? AND created_at > NOW() - INTERVAL 1 HOUR",
                String.class,
                token
            );
            
            System.out.println("Found user ID: " + userId + " for token: " + token);
            return userId;
        } catch (Exception e) {
            System.err.println("Error looking up token: " + token + " - " + e.getMessage());
            return null; 
        }
    }

    /**
     * Store the chat ID for a user
     */
    private void storeChatIdForUser(String userId, long chatId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM telegram_chats WHERE user_id = ?",
                Integer.class,
                userId
        );

        if (count != null && count > 0) {
            jdbcTemplate.update(
                    "UPDATE telegram_chats SET chat_id = ?, updated_at = NOW() WHERE user_id = ?",
                    chatId, userId
            );
        } else {
            jdbcTemplate.update(
                    "INSERT INTO telegram_chats (user_id, chat_id, created_at) VALUES (?, ?, NOW())",
                    userId, chatId
            );
        }
    }

    /**
     * Delete a token after it's been used
     */
    private void deleteToken(String token) {
        jdbcTemplate.update("DELETE FROM telegram_tokens WHERE token = ?", token);
    }

    /**
     * Get the chat ID for a user
     */
    private Long getChatIdForUser(String userId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT chat_id FROM telegram_chats WHERE user_id = ?",
                    Long.class,
                    userId
            );
        } catch (Exception e) {
            return null;
        }
    }
}