package com.demo.demoapp.domain;

import java.util.List;

public class ReminderWords {
    private List<DailyWord> reminders;

    public ReminderWords(List<DailyWord> reminders) {
        this.reminders = reminders;
    }
}
