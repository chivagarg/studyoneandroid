package com.demo.demoapp;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.demoapp.domain.DailyWord;
import com.demo.demoapp.events.ReminderWordsEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

public class WordListFragment extends Fragment {
    private RecyclerView recyclerView;
    private WordListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.word_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.words);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        adapter = new WordListAdapter(view.getContext(), new ArrayList<DailyWord>());
        recyclerView.setAdapter(adapter);
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
        System.out.println("Received reminder count: " + reminderWordsEvent.getReminderWords().get().size());
        adapter.setDailyWords(reminderWordsEvent.getReminderWords().get());
        adapter.notifyDataSetChanged();
        /* Do something */
    }
}

