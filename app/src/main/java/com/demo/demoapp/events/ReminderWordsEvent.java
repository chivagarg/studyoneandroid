package com.demo.demoapp.events;

import com.demo.demoapp.domain.ReminderWords;

public class ReminderWordsEvent {
    private ReminderWords reminderWords;

    public ReminderWordsEvent(ReminderWords reminderWords) {
        this.reminderWords = reminderWords;
    }

    public ReminderWords getReminderWords() {
        return reminderWords;
    }
}

