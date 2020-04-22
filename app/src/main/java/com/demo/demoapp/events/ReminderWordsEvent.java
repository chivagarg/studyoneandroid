package com.demo.demoapp.events;

import com.demo.demoapp.domain.DailyWord;
import com.demo.demoapp.domain.ReminderWords;

public class ReminderWordsEvent {
    private ReminderWords reminderWords;
    private DailyWord wordOfTheDay;

    public ReminderWordsEvent(ReminderWords reminderWords, DailyWord wordOfTheDay) {
        this.reminderWords = reminderWords;
        this.wordOfTheDay = wordOfTheDay;
    }

    public ReminderWords getReminderWords() {
        return reminderWords;
    }

    public DailyWord getWordOfTheDay(){
        return wordOfTheDay;
    }
}

