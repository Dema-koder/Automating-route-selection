package org.example.project;

import org.example.project.service.job.LeetcodeTags;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void getLeetcodeTags() {
        LeetcodeTags.getLeetcodeTags();
    }
}
