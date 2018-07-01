package oscar.riksdagskollen.Util.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.ProtocolReaderActivity;
import oscar.riksdagskollen.Activity.SearchedVoteAcitivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.Callback.VoteCallback;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;

/**
 * Created by oscar on 2018-03-29.
 */

public class DecisionListAdapter extends RiksdagenViewHolderAdapter {
    private final List<DecisionDocument> decisionDocuments;
    private Context context;
    private final RecyclerView recyclerView;

    public DecisionListAdapter(List<DecisionDocument> items, final OnItemClickListener listener, RecyclerView recyclerView) {
        super(items, listener);
        this.clickListener = listener;
        this.recyclerView = recyclerView;
        decisionDocuments = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.decision_list_item, parent, false);
            return new DecisionView(itemView);
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
        } else if (position >= headers.size() + decisionDocuments.size()) {
            View v = footers.get(position - decisionDocuments.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            DecisionDocument document = decisionDocuments.get(position);
            ((DecisionView) holder).bind(document, this.clickListener);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class DecisionView extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView body;
        private final TextView justDate;
        private final TextView debateDate;
        private final TextView decisionDate;
        private final TextView betName;
        private final ImageView expandIcon;

        private final Button fullBet;
        private final Button searchVote;

        private final View itemView;

        int rotationAngle = 0;



        public DecisionView(final View view) {
            super(view);
            this.itemView = view;
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body_text);
            expandIcon = view.findViewById(R.id.expand_icon);
            justDate = view.findViewById(R.id.justering_date);
            debateDate = view.findViewById(R.id.debatt_date);
            decisionDate = view.findViewById(R.id.beslut_date);
            fullBet = view.findViewById(R.id.full_bet_link);
            betName = view.findViewById(R.id.bet_name);
            searchVote = view.findViewById(R.id.search_vote);
        }

        public void bind(final DecisionDocument item, final OnItemClickListener listener) {
            title.setText(item.getTitel());
            betName.setText("Betänkande:  " + item.getRm() + ":" + item.getBeteckning());
            body.setText(Html.fromHtml(item.getNotis()));
            justDate.setText(dateStringBuilder("Justering: ",item.getJusteringsdag()));
            debateDate.setText(dateStringBuilder("Debatt: ", item.getDebattdag()));
            decisionDate.setText(dateStringBuilder("Beslut: ", item.getBeslutsdag()));
            fullBet.setText("Läs fullständigt betänkande");
            searchVote.setText("Visa voteringar");
            if(!item.hasVotes()) searchVote.setVisibility(View.GONE);


            fullBet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisionWithId(
                            new DecisionsCallback() {
                                @Override
                                public void onDecisionsFetched(List<DecisionDocument> decisions) {
                                    if(decisions.isEmpty()){
                                        Toast.makeText(context,"Kunde inte hitta betänkande.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(context, ProtocolReaderActivity.class);
                                        intent.putExtra("url", item.getDokument_url_html());
                                        intent.putExtra("title",item.getNotisrubrik());
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFail(VolleyError error) {
                                    Toast.makeText(context,"Kunde inte hitta betänkande.", Toast.LENGTH_LONG).show();
                                }
                            },
                            item.getDok_id()
                    );
                }
            });

            if (item.isExpanded()){
                body.setVisibility(View.VISIBLE);
                fullBet.setVisibility(View.VISIBLE);
                if(item.hasVotes())searchVote.setVisibility(View.VISIBLE);
                expandIcon.setRotation(180);
            } else {
                body.setVisibility(View.GONE);
                fullBet.setVisibility(View.GONE);
                searchVote.setVisibility(View.GONE);
                expandIcon.setRotation(0    );
            }
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                    if (item.isExpanded()) {
                        expandItemView(false, item);
                        item.setExpanded(false);
                    } else {
                        expandItemView(true, item);
                        item.setExpanded(true);
                    }

                    rotationAngle = item.isExpanded() ? 180 : 0;  //toggle
                    expandIcon.animate().rotation(rotationAngle).setDuration(200).start();
                }
            });

        }

        private SpannableStringBuilder dateStringBuilder(String entity, String value){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString txtSpannable= new SpannableString(entity);
            if (value == null) value = "";
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            txtSpannable.setSpan(boldSpan, 0, entity.length()-2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(txtSpannable);
            builder.append(value);
            return builder;
        }


        void expandItemView(boolean expand, final DecisionDocument item) {

            if (expand){
                expand(body, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fullBet.setVisibility(View.VISIBLE);
                        if(item.hasVotes()) searchVote.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                RikdagskollenApp.getInstance().getRiksdagenAPIManager().searchVotesForDecision(item, new VoteCallback() {
                    @Override
                    public void onVotesFetched(final List<Vote> votes) {
                        if(!votes.isEmpty()){
                            item.setHasVotes(true);
                            searchVote.setVisibility(View.VISIBLE);
                            searchVote.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, SearchedVoteAcitivity.class);
                                    ArrayList<Vote> list = new ArrayList<>(votes);
                                    intent.putParcelableArrayListExtra("votes", list);
                                    intent.putExtra("document",item);
                                    context.startActivity(intent);
                                }
                            });
                        } else {
                            item.setHasVotes(false);
                        }
                    }

                    @Override
                    public void onFail(VolleyError error) {
                        searchVote.setVisibility(View.GONE);
                        item.setHasVotes(false);
                    }
                });
            }
            else {
                collapse(body, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fullBet.setVisibility(View.GONE);
                        if(item.hasVotes()) searchVote.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        }


    }


    private void expand(final View v, Animation.AnimationListener listener) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }

    private void collapse(final View v, Animation.AnimationListener listener) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }
}