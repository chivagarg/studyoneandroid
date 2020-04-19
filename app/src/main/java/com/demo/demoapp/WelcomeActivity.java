package com.demo.demoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.demoapp.domain.DailyWord;
import com.demo.demoapp.domain.ReminderWords;
import com.demo.demoapp.domain.User;
import com.demo.demoapp.events.ReminderWordsEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.demo.demoapp.Constants.DAILY_WORDS_URL;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Button goToDailyWordButton;
    private DailyWord dailyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        TextView textView = findViewById(R.id.welcome_text);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        User user = gson.fromJson(json, User.class);

        fetchDailyWords(user.getToken());

        textView.setText("Welcome " + user.getName());

        ImageView imageView = findViewById(R.id.user_image);

        new AsyncTaskLoadImage(imageView).execute(user.getPhotoUrl());

        goToDailyWordButton = findViewById(R.id.go_to_daily_word_button);
        goToDailyWordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(WelcomeActivity.this, WordDisplayActivity.class);
                        myIntent.putExtra("DailyWord", new Gson().toJson(dailyWord));
                        WelcomeActivity.this.startActivity(myIntent);;
                    }
                }
        );


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    private void fetchDailyWords(final String token) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DAILY_WORDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resp = null;
                        try {
                            resp = new JSONObject(response);
                            dailyWord = toDailyWord(resp.getJSONObject("wordOfTheDay"));

                            List<DailyWord> repeatedWords = new ArrayList<>();
                            JSONArray repeatedWordJson = resp.getJSONArray("repeatedWords");
                            for (int i = 0; i < repeatedWordJson.length(); ++i) {
                                JSONObject jsonWord = (JSONObject) (resp.getJSONArray("repeatedWords").get(i));
                                repeatedWords.add(toDailyWord(jsonWord));
                            }
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            editor.putString("WordOfTheDay", gson.toJson(dailyWord));
                            editor.putString("ReminderWords", gson.toJson(repeatedWords));
                            editor.commit();

                            EventBus.getDefault().post(new ReminderWordsEvent(new ReminderWords(repeatedWords)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                String bearerToken = "Bearer " + token;
                params.put("Authorization",bearerToken);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private DailyWord toDailyWord(JSONObject jsonWord) throws JSONException {
        String word = jsonWord.getString("word");
        String meaning = jsonWord.getString("meaning");
        String pronunciation = jsonWord.getString("pronunciation");
        return new DailyWord(word, meaning, pronunciation);
    }

}
