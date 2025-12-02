package com.example.crmbackend.listener;

import com.example.crmbackend.config.RabbitMQConfig;
import com.example.crmbackend.repository.ActivityRepository;
import com.example.crmbackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.REMINDER_QUEUE)
public class ReminderListener {

    private final ActivityRepository activityRepository;
    private final EmailService emailService;

    @RabbitHandler
    public void handleReminder(Long activityId) {
        System.out.println("Reminder sending ...");
        activityRepository.findById(activityId).ifPresent(activity -> {
            if (activity.isSubscribedReminder() && !activity.isSentReminder()) {
                // get email address by ownerId
                String ownerEmail = activityRepository.findActivityEmailById(activityId);
                if (ownerEmail == null) {ownerEmail = "yeeh1616.delft@gmail.com";}
                String subject = "CRM Activity Reminder: " + activity.getType();
                String text = "You have an activity:\n\n" +
                        "type: " + activity.getType() + "\n" +
                        "content: " + activity.getContent() + "\n" +
                        "time: " + activity.getNextFollowUpAt();

                emailService.sendReminderEmail(ownerEmail, subject, text);

                activity.setSentReminder(true);
                activityRepository.save(activity);
                System.out.println("Reminder email sent for activity " + activity.getId());
            }
        });
    }
}
