package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.View.CircularImageView;

public class TweetAdapter extends RiksdagenViewHolderAdapter {

    private static final Comparator<Tweet> DEFAULT_COMPARATOR = new Comparator<Tweet>() {
        @Override
        public int compare(Tweet a, Tweet b) {
            return 0;
        }
    };
    private Comparator<Tweet> mComparator = DEFAULT_COMPARATOR;
    private final SortedList<Tweet> tweets = new SortedList<>(Tweet.class, new SortedList.Callback<Tweet>() {
        @Override
        public int compare(Tweet o1, Tweet o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Tweet oldItem, Tweet newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(Tweet item1, Tweet item2) {
            return false;
        }


        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });


    public TweetAdapter(List<Tweet> tweetsList, OnItemClickListener clickListener) {
        super(clickListener);
        addAll(tweetsList);
        setSortedList(tweets);


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tweet_item, parent, false);
            return new TweetAdapter.TweetViewHolder(itemView);
        } else {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < headers.size()) {
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else if (position >= headers.size() + tweets.size()) {
            View v = footers.get(position - tweets.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            Tweet document = tweets.get(position);
            ((TweetAdapter.TweetViewHolder) holder).bind(document, this.clickListener);
        }
    }


    @Override
    public void addAll(List<?> items) {
        tweets.addAll((Collection<Tweet>) items);
    }

    @Override
    public void removeAll(List<?> items) {
        tweets.beginBatchedUpdates();
        for (Object item : items) {
            tweets.remove((Tweet) item);
        }
        tweets.endBatchedUpdates();
    }

    @Override
    public void replaceAll(List<?> items) {
        tweets.beginBatchedUpdates();
        tweets.clear();
        tweets.addAll((Collection<Tweet>) items);
        tweets.endBatchedUpdates();
    }


    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        private final TextView tweetText;
        private final TextView date;
        private final TextView authorText;
        private final TextView authorScreenNameText;
        private final NetworkImageView image;
        private final CircularImageView authorView;


        public TweetViewHolder(View textView) {
            super(textView);
            tweetText = textView.findViewById(R.id.tweet_text);
            date = textView.findViewById(R.id.publicerad);
            image = textView.findViewById(R.id.image);
            authorView = textView.findViewById(R.id.author_img);
            authorText = textView.findViewById(R.id.author);
            authorScreenNameText = textView.findViewById(R.id.screen_name);

        }

        public void bind(Tweet tweet, final OnItemClickListener listener) {
            authorText.setText(tweet.getUser().getName());
            authorScreenNameText.setText("@" + tweet.getUser().getScreen_name());
            if (tweet.isRetweet()) {
                Tweet reTweet = tweet.getRetweeted_status();
                String RTString = tweet.getText().split(":")[0];
                tweetText.setText(RTString + ": " + reTweet.getText());
                tweet = reTweet;
            } else {
                tweetText.setText(tweet.getText());
            }
            date.setText(tweet.getCreated_at());
            if (tweet.hasMedia()) {
                image.setVisibility(View.VISIBLE);
                image.setDefaultImageResId(R.drawable.ic_placeholder_image_web);
                image.setImageUrl(tweet.getImageUrl(),
                        RiksdagskollenApp.getInstance().getRequestManager().getmImageLoader());
            } else {
                image.setVisibility(View.GONE);
            }


        }
    }
}
