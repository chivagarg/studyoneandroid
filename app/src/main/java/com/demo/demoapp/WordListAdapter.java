package com.demo.demoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.demoapp.domain.DailyWord;
import com.demo.demoapp.domain.User;
import com.google.gson.Gson;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_REMINDER_WORD = 2;

    private List<DailyWord> dailyWords;
    private DailyWord wordOfTheDay;
    private Bitmap userPhoto;
    private User user;
    private Context context;

    WordListAdapter(Context context, List<DailyWord> dailyWords, User user) {
        this.context = context;
        this.dailyWords = dailyWords;
        this.user = user;
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout containerLinearLayout;
        public HeaderViewHolder(LinearLayout v) {
            super(v);
            containerLinearLayout = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        switch (viewType) {
            case TYPE_REMINDER_WORD:
                CardView v = (CardView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.word_list_item, parent, false);
                WordViewHolder vh = new WordViewHolder(v);
                return vh;
            case TYPE_HEADER:
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.welcome_user_list_item, parent, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(linearLayout);
                return headerViewHolder;
        }
        throw new IllegalArgumentException("not reachable");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                if (user != null) {
                    ((TextView) headerViewHolder.containerLinearLayout.findViewById(R.id.welcome_text))
                            .setText(user.getName());
                }
                if (userPhoto != null) {
                    ImageView imageView = headerViewHolder.containerLinearLayout.findViewById(R.id.user_image);
                    imageView.setImageBitmap(userPhoto);
                }
                if (dailyWords.size() != 0) {
                    TextView wordsForReviewCount = headerViewHolder.containerLinearLayout.findViewById(R.id.word_for_review_count);
                    wordsForReviewCount.setText("(" + dailyWords.size() + ")");
                }

                if (wordOfTheDay != null) {
                    Button goToDailyWordButton = headerViewHolder.containerLinearLayout.findViewById(R.id.go_to_daily_word_button);
                    goToDailyWordButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent myIntent = new Intent(context, WordDisplayActivity.class);
                                    myIntent.putExtra("DailyWord", new Gson().toJson(wordOfTheDay));
                                    context.startActivity(myIntent);;
                                }
                            }
                    );
                }
                break;

            case TYPE_REMINDER_WORD:
                WordViewHolder wordViewHolder = (WordViewHolder) holder;
                final DailyWord dailyWord = dailyWords.get(position - 1);
                ((TextView)wordViewHolder.cardView.findViewById(R.id.card_view_word)).setText(dailyWord.getWord());
                ((TextView)wordViewHolder.cardView.findViewById(R.id.card_view_pronunciation)).setText(dailyWord.getPronunciation());
                wordViewHolder.cardView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(context, WordDisplayActivity.class);
                                myIntent.putExtra("DailyWord", new Gson().toJson(dailyWord));
                                context.startActivity(myIntent);;
                            }
                        }
                );
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dailyWords == null ? 0 : dailyWords.size() + 1; // include header
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        return TYPE_REMINDER_WORD;
    }

    void setDailyWords(List<DailyWord> dailyWords) {
        this.dailyWords = dailyWords;
    }

    void setWordOfTheDay(DailyWord wordOfTheDay) {
        this.wordOfTheDay = wordOfTheDay;
    }

    void setUserPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }
}
