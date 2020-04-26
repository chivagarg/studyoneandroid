package com.demo.demoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.demoapp.domain.DailyWord;
import com.google.gson.Gson;

public class WordDisplayActivity extends AppCompatActivity {

    Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.word_display);

        DailyWord dailyWord = null;

        String dwString;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dwString = extras.getString("DailyWord");
            dailyWord = new Gson().fromJson(dwString, DailyWord.class);
        } else {
            // Read from storage
        }

        TextView dailyWordTitle = findViewById(R.id.daily_word_title);
        dailyWordTitle.setText(dailyWord.getWord());

//        View contentLayout = findViewById(R.id.content_layout);
//        ViewGroup.LayoutParams layoutParams = contentLayout.getLayoutParams();
//        layoutParams.width = dailyWordTitle.getWidth();
//        contentLayout.setLayoutParams(layoutParams);

        ((TextView)findViewById(R.id.daily_word_pronunciation)).setText("[" + dailyWord.getPronunciation() + "]");
        ((TextView)findViewById(R.id.daily_word_meaning)).setText(dailyWord.getMeaning());

        Button close = findViewById(R.id.close_button);
        close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
