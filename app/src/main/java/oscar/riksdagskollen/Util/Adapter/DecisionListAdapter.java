package oscar.riksdagskollen.Util.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.util.SortedList;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.Activity.ProtocolReaderActivity;
import oscar.riksdagskollen.Activity.SearchedVoteAcitivity;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.DecicionCategory;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.DecisionsCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;

import static oscar.riksdagskollen.Util.Helper.AnimUtil.collapse;
import static oscar.riksdagskollen.Util.Helper.AnimUtil.expand;

/**
 * Created by oscar on 2018-03-29.
 */

public class DecisionListAdapter extends RiksdagenViewHolderAdapter {
    private final SortedList<DecisionDocument> decisionDocuments = new SortedList<>(DecisionDocument.class, new SortedList.Callback<DecisionDocument>() {
        @Override
        public int compare(DecisionDocument o1, DecisionDocument o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(DecisionDocument oldItem, DecisionDocument newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(DecisionDocument item1, DecisionDocument item2) {
            return item1.equals(item2);
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
    private Context context;

    private static final Comparator<DecisionDocument> DEFAULT_COMPARATOR = new Comparator<DecisionDocument>() {
        @Override
        public int compare(DecisionDocument a, DecisionDocument b) {
            return 0;
        }
    };
    private Comparator<DecisionDocument> mComparator = DEFAULT_COMPARATOR;


    public DecisionListAdapter(List<DecisionDocument> items, final OnItemClickListener listener, RecyclerView recyclerView) {
        super(listener);
        setSortedList(decisionDocuments);
        addAll(items);

        this.clickListener = listener;
    }

    public void setComparator(Comparator<DecisionDocument> comparator) {
        this.mComparator = comparator;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.decision_list_item, parent, false);
            return new DecisionView(itemView, context);
        } else {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }

    public void add(DecisionDocument model) {
        decisionDocuments.add(model);
    }

    public void remove(DecisionDocument model) {
        decisionDocuments.remove(model);
    }

    @Override
    public void addAll(List<?> items) {
        decisionDocuments.addAll((Collection<DecisionDocument>) items);
    }

    @Override
    public void removeAll(List<?> items) {
        decisionDocuments.beginBatchedUpdates();
        for (Object item : items) {
            decisionDocuments.remove((DecisionDocument) item);
        }
        decisionDocuments.endBatchedUpdates();
    }

    @Override
    public void replaceAll(List<?> items) {
        decisionDocuments.beginBatchedUpdates();
        decisionDocuments.clear();
        ;
        decisionDocuments.addAll((Collection<DecisionDocument>) items);
        decisionDocuments.endBatchedUpdates();
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
    public static class DecisionView extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView body;
        private final TextView justDate;
        private final TextView debateDate;
        private final TextView decisionDate;
        private final TextView betName;
        private final TextView catName;
        private final ImageView expandIcon;
        private final View catColor;
        private boolean isAnimating = false;
        private Context context;

        private final Button fullBet;
        private final Button searchVote;

        private final View itemView;

        int rotationAngle = 0;


        public DecisionView(final View view, Context context) {
            super(view);
            this.itemView = view;
            this.context = context;
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body_text);
            expandIcon = itemView.findViewById(R.id.attended_documents_expand_icon);
            justDate = itemView.findViewById(R.id.justering_date);
            debateDate = itemView.findViewById(R.id.debatt_date);
            decisionDate = itemView.findViewById(R.id.beslut_date);
            fullBet = itemView.findViewById(R.id.full_bet_link);
            betName = itemView.findViewById(R.id.bet_name);
            searchVote = itemView.findViewById(R.id.search_vote);
            catColor = itemView.findViewById(R.id.category_border);
            catName = itemView.findViewById(R.id.category_name);
        }

        public void bind(final DecisionDocument item, final OnItemClickListener listener) {
            title.setText(item.getTitel());
            betName.setText("Betänkande:  " + item.getRm() + ":" + item.getBeteckning());
            if (item.getNotis() != null) body.setText(Html.fromHtml(item.getNotis()));
            else body.setText("");
            justDate.setText(dateStringBuilder("Justering: ",item.getJusteringsdag()));
            debateDate.setText(dateStringBuilder("Debatt: ", item.getDebattdag()));
            decisionDate.setText(dateStringBuilder("Beslut: ", item.getBeslutsdag()));
            fullBet.setText("Läs fullständigt betänkande");
            searchVote.setText("Visa voteringar");

            DecicionCategory decicionCategory = DecicionCategory.getCategoryFromBet(item.getBeteckning());
            if (RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme() != ThemeManager.Theme.BLACK) {
                catColor.setBackgroundColor(context.getResources().getColor(decicionCategory.getCategoryColor()));
            } else {
                // Dont use colors for black theme
                catColor.setBackgroundColor(context.getResources().getColor(R.color.black));
            }
            catName.setText(decicionCategory.getCategoryName());
            if(!item.hasVotes() || !item.isExpanded()) searchVote.setVisibility(View.GONE);


            fullBet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisionWithId(
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
            builder.append("\n");
            builder.append(value);
            return builder;
        }


        void expandItemView(boolean expand, final DecisionDocument item) {

            if (expand){
                expand(body, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fullBet.setVisibility(View.VISIBLE);
                        if(item.hasVotes()) searchVote.setVisibility(View.VISIBLE);
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                RiksdagskollenApp.getInstance().getRiksdagenAPIManager().searchVotesForDecision(item, new VoteCallback() {
                    @Override
                    public void onVotesFetched(final List<Vote> votes) {
                        if(!votes.isEmpty()){
                            item.setHasVotes(true);
                            if(!isAnimating) searchVote.setVisibility(View.VISIBLE);
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
                        isAnimating = true;
                        fullBet.setVisibility(View.GONE);
                        if(item.hasVotes()) searchVote.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isAnimating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        }


    }

}