package com.example.tsis.service;

import com.example.tsis.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.mood-audit", name = "enabled", havingValue = "true")
public class MoodAuditor {

    private static final Logger log = LoggerFactory.getLogger(MoodAuditor.class);

    private final AppProperties appProperties;

    public MoodAuditor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void record(String mood) {
        log.info("[{}] mood received: {}", appProperties.environment(), mood);
    }
}
