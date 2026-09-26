package com.example.tsis.controller;

import com.example.tsis.service.MoodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @GetMapping
    public String get(@RequestParam("mood") String mood) {
        return moodService.getMood(mood);
    }

    @PostMapping
    public String add(@RequestBody String mood) {
        return moodService.getMood(mood);
    }
}
