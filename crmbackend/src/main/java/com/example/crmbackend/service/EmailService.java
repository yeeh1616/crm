package com.example.crmbackend.service;

/**
 * Service interface for email operations
 */
public interface EmailService {
    void sendReminderEmail(String to, String subject, String text);
}
