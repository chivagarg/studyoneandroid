package com.demo.demoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.demoapp.domain.DailyWord;

import java.util.Arrays;
import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    private Context context;
    private List<DailyWord> dailyWords;

    // Provide a suitable constructor (depends on the kind of dataset)
    public WordListAdapter(Context mContext, List<DailyWord> dailyWords) {
        this.context = mContext;
        this.dailyWords = dailyWords;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class WordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public WordViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false);
        WordViewHolder vh = new WordViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView)holder.cardView.findViewById(R.id.card_view_word)).setText(dailyWords.get(position).getWord());
        ((TextView)holder.cardView.findViewById(R.id.card_view_pronunciation)).setText(dailyWords.get(position).getPronunciation());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dailyWords == null ? 0 : dailyWords.size();
    }

    void setDailyWords(List<DailyWord> dailyWords) {
        this.dailyWords = dailyWords;
    }
}
