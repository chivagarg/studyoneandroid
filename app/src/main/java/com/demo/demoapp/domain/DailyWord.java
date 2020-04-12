package com.demo.demoapp.domain;

public class DailyWord {
    private String word;
    private String meaning;
    private String pronunciation;

    public DailyWord(String word, String meaning, String pronunciation){
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning(){
        return meaning;
    }

    public String getPronunciation(){
        return pronunciation;
    }
}
