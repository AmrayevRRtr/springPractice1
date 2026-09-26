package com.example.tsis.service;

import com.example.tsis.config.AppProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class MoodService {

    private final AppProperties appProperties;
    private final ObjectProvider<MoodAuditor> moodAuditor;

    public MoodService(AppProperties appProperties, ObjectProvider<MoodAuditor> moodAuditor) {
        this.appProperties = appProperties;
        this.moodAuditor = moodAuditor;
    }

    public String getMood(String mood) {
        moodAuditor.ifAvailable(auditor -> auditor.record(mood));
        return "My mood at start of the course is " + mood
                + " (" + appProperties.environment() + ")";
    }
}
