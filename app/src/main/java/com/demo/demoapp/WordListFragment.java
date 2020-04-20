package com.demo.demoapp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.demoapp.domain.DailyWord;
import com.demo.demoapp.domain.User;
import com.demo.demoapp.events.ReminderWordsEvent;
import com.demo.demoapp.events.UserPhotoEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

public class WordListFragment extends Fragment {
    private RecyclerView recyclerView;
    private WordListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView reminderWordCountText;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        user = gson.fromJson(json, User.class);

        View view =  inflater.inflate(R.layout.word_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.words);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        adapter = new WordListAdapter(new ArrayList<DailyWord>(), user);
        recyclerView.setAdapter(adapter);
        reminderWordCountText = (TextView)view.findViewById(R.id.word_for_review_count);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReminderWordsEvent reminderWordsEvent) {
        reminderWordCountText.setText("(" + reminderWordsEvent.getReminderWords().get().size() + ")");
        adapter.setDailyWords(reminderWordsEvent.getReminderWords().get());
        adapter.notifyDataSetChanged();
        /* Do something */
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserPhotoEvent userPhotoEvent) {
        adapter.setUserPhoto(userPhotoEvent.getUserPhotoBitmap());
        adapter.notifyItemChanged(0);
        /* Do something */
    }
}

