package com.demo.demoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.demo.demoapp.events.UserPhotoEvent;
import com.google.android.material.navigation.NavigationView;
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

// https://stackoverflow.com/questions/53131591/androidx-navigation-view-setnavigationitemselectedlistener-doesnt-work
// https://developer.android.com/guide/navigation/navigation-ui#add_a_navigation_drawer

public class WelcomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle hamburger;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;
    private DailyWord dailyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Required to show hamburger menu.
        // https://stackoverflow.com/questions/28071763/toolbar-navigation-hamburger-icon-missing
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        hamburger = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_closed);
        drawerLayout.addDrawerListener(hamburger);
        hamburger.setDrawerIndicatorEnabled(true);
        hamburger.syncState();

        setNavigationViewListener();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        User user = gson.fromJson(json, User.class);

        fetchDailyWords(user.getToken());

        new AsyncTaskLoadImage().execute(user.getPhotoUrl());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.logout:
                sharedPreferences.edit().remove("User").commit();
                Intent nextIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(nextIntent);
                break;
            case R.id.all_words:
                Toast.makeText(this, "All words clicked", Toast.LENGTH_LONG).show();
                break;
            default:
                return false;
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
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
            EventBus.getDefault().post(new UserPhotoEvent(bitmap));
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

                            EventBus.getDefault().post(new ReminderWordsEvent(new ReminderWords(repeatedWords), dailyWord));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof AuthFailureError) {
                    Toast.makeText(WelcomeActivity.this, "Authentication failure", Toast.LENGTH_LONG).show();
                }
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
