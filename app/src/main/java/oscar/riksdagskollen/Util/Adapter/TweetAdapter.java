package oscar.riksdagskollen.Util.Adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.JSONModel.Twitter.TweetURL;
import oscar.riksdagskollen.Util.View.CircularImageView;

public class TweetAdapter extends RiksdagenViewHolderAdapter {

    private static final Comparator<Tweet> DEFAULT_COMPARATOR = new Comparator<Tweet>() {
        @Override
        public int compare(Tweet a, Tweet b) {
            return 0;
        }
    };
    private Fragment fragment;
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


    public TweetAdapter(List<Tweet> tweetsList, OnItemClickListener clickListener, Fragment fragment) {
        super(clickListener);
        addAll(tweetsList);
        setSortedList(tweets);
        this.fragment = fragment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tweet_item, parent, false);
            return new TweetAdapter.TweetViewHolder(itemView, fragment);
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
        private final NetworkImageView image;
        private final CircularImageView authorView;
        private final Fragment fragment;
        private final TextView screenName;
        private final TextView retweetStatus;


        public TweetViewHolder(View view, Fragment fragment) {
            super(view);
            tweetText = view.findViewById(R.id.tweet_text);
            date = view.findViewById(R.id.publicerad);
            image = view.findViewById(R.id.image);
            authorView = view.findViewById(R.id.author_img);
            authorText = view.findViewById(R.id.author);
            screenName = view.findViewById(R.id.author_screen_name);
            retweetStatus = view.findViewById(R.id.retweet_status);
            this.fragment = fragment;
        }

        void bind(Tweet tweet, final OnItemClickListener listener) {

            authorText.setText(tweet.getUser().getName());
            screenName.setText(String.format("@%s", tweet.getUser().getScreen_name()));
            if (fragment.getActivity() != null) {
                Glide
                        .with(fragment)
                        .load(tweet.getUser().getProfile_image_url_https())
                        .into(authorView);
            }

            if (tweet.hasMedia()) {
                image.setVisibility(View.VISIBLE);
                image.setDefaultImageResId(R.drawable.ic_placeholder_image_web);
                image.setImageUrl(tweet.getImageUrl(),
                        RiksdagskollenApp.getInstance().getRequestManager().getmImageLoader());
            } else {
                image.setVisibility(View.GONE);
            }

            final Tweet tweetF = tweet;
            authorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabs.openTab(fragment.getContext(), "https://twitter.com/" + tweetF.getUser().getScreen_name());
                }
            });


            SpannableStringBuilder builder = new SpannableStringBuilder();

            if (tweet.isRetweet()) {
                Tweet reTweet = tweet.getRetweeted_status();
                String RTUser = tweet.getText().split(":")[0].substring(3);

                retweetStatus.setText(String.format("retweetade %s:", RTUser));
                builder.append(reTweet.getText());
                tweet = reTweet;
            } else {
                retweetStatus.setVisibility(View.GONE);
                builder.append(tweet.getText());
            }


            Pattern pattern = Pattern.compile("https?://t\\.co/\\S+");
            final Matcher matcher = pattern.matcher(builder);
            // Check all occurrences
            while (matcher.find()) {
                final String url = matcher.group();
                // Link to media, remove from tweet and set as link for image
                if (tweet.hasMedia() && !tweetURLsContainsURL(tweet, url)) {
                    try {
                        builder.delete(matcher.start(), matcher.end());
                    } catch (Exception e) {
                        // Quick fix if image url cannot be matched.
                        //  Tweet that caused error:
                        // SR-journalist om ungdomar som engagerar sig för klimatet och erbjuder konstruktiv lösning: "trams", "välbeställda ungdomar"
                        //    (tweeten nu borttagen, från denna tråd:  https://t.co/HSBdHB6NLb
                        System.out.println("Failed to match tweet image URL");
                        e.printStackTrace();
                    }
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomTabs.openTab(fragment.getContext(), url);
                        }
                    });
                } else {
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            CustomTabs.openTab(fragment.getContext(), url);
                        }
                    };
                    builder.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }

            tweetText.setText(builder);
            tweetText.setMovementMethod(LinkMovementMethod.getInstance());

            try {
                date.setText(getTwitterDate(tweet.getCreated_at()));
            } catch (ParseException e) {
                date.setText(tweet.getCreated_at());
                e.printStackTrace();
            }

            final Tweet finalTweet = tweet;
            tweetText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(finalTweet);
                }
            });


        }

        private boolean tweetURLsContainsURL(Tweet tweet, String url) {
            if (!tweet.hasUrls()) return false;
            for (TweetURL tweetURL : tweet.getTweetURLS()) {
                if (tweetURL.getUrl().equals(url)) return true;
            }
            return false;
        }


        private static String getTwitterDate(String date) throws ParseException {

            final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.US);
            sf.setLenient(true);

            Locale locale = new Locale("swe", "sv_SE");
            DateFormat dateFormat = DateFormat.getDateTimeInstance(
                    DateFormat.DEFAULT, DateFormat.SHORT, locale);

            return dateFormat.format(sf.parse(date));
        }
    }
}
