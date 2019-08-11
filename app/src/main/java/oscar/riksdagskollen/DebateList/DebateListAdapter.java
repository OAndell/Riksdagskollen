package oscar.riksdagskollen.DebateList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.Enum.DocumentType;
import oscar.riksdagskollen.Util.JSONModel.Intressent;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeCallback;
import oscar.riksdagskollen.Util.View.CircularImageView;

public class DebateListAdapter extends RiksdagenViewHolderAdapter {

    private static final int TYPE_PORTRAIT = 111;
    private static final int TYPE_NO_PORTRAIT = 222;

    private static final Comparator<PartyDocument> DEFAULT_COMPARATOR = new Comparator<PartyDocument>() {
        @Override
        public int compare(PartyDocument a, PartyDocument b) {
            return 0;
        }
    };
    Fragment fragment;
    private Comparator<PartyDocument> mComparator = DEFAULT_COMPARATOR;
    private final SortedList<PartyDocument> documentList = new SortedList<>(PartyDocument.class, new SortedList.Callback<PartyDocument>() {
        @Override
        public int compare(PartyDocument o1, PartyDocument o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(PartyDocument oldItem, PartyDocument newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(PartyDocument item1, PartyDocument item2) {
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


    public DebateListAdapter(List<PartyDocument> items, OnItemClickListener clickListener, Fragment fragment) {
        super(clickListener);
        this.fragment = fragment;
        setSortedList(documentList);
        addAll(items);

        this.clickListener = clickListener;
    }

    public void setComparator(Comparator<PartyDocument> mComparator) {
        this.mComparator = mComparator;
    }

    @Override
    public long getItemId(int position) {

        if (position < headers.size()) {
            return 1;
        } else if (position >= headers.size() + getObjectCount()) {
            return 2;
        }
        return documentList.get(position - headers.size()).uniqueDocId();
    }

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > sortedList > footers
        if (position < headers.size()) {
            return TYPE_HEADER;
        } else if (position >= headers.size() + documentList.size()) {
            return TYPE_FOOTER;
        }
        if (documentList.get(position).getDoktyp().equals(DocumentType.Interpellation.getDocType())) {
            return TYPE_PORTRAIT;
        }
        return TYPE_ITEM;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PORTRAIT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.debate_ip_list_item, parent, false);
            return new IPDebateListViewHolder(itemView, fragment);
        }
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.debate_other_list_item, parent, false);
            return new BetDebateListViewHolder(itemView, fragment);
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
        } else if (position >= headers.size() + documentList.size()) {
            View v = footers.get(position - documentList.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            PartyDocument document = documentList.get(position - headers.size());
            if (documentList.get(position).getDoktyp().equals(DocumentType.Interpellation.getDocType())) {
                ((IPDebateListViewHolder) holder).bind(document, clickListener);
            } else {
                ((BetDebateListViewHolder) holder).bind(document, clickListener);
            }

        }
    }


    public void add(PartyDocument model) {
        documentList.add(model);
    }

    public void remove(PartyDocument model) {
        documentList.remove(model);
    }

    @Override
    public void addAll(List<?> items) {
        documentList.addAll((Collection<PartyDocument>) items);
    }

    @Override
    public void removeAll(List<?> items) {
        documentList.beginBatchedUpdates();
        for (Object item : items) {
            documentList.remove((PartyDocument) item);
        }
        documentList.endBatchedUpdates();
    }

    @Override
    public void replaceAll(List<?> items) {
        documentList.beginBatchedUpdates();
        documentList.clear();
        documentList.addAll((Collection<PartyDocument>) items);
        documentList.endBatchedUpdates();
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    static class IPDebateListViewHolder extends RecyclerView.ViewHolder {
        final TextView documentTitle;
        final TextView debateDate;
        final TextView author;
        final CircularImageView authorView;
        final ImageView partySymbol;
        final FlexboxLayout partyContainer;
        final Fragment fragment;
        Request imageUrlRequest;


        public IPDebateListViewHolder(View cardView, Fragment fragment) {
            super(cardView);
            this.fragment = fragment;
            documentTitle = cardView.findViewById(R.id.debate_card_title);
            debateDate = cardView.findViewById(R.id.debate_card_date);
            author = cardView.findViewById(R.id.debate_card_author);
            authorView = cardView.findViewById(R.id.debate_card_portrait);
            partySymbol = cardView.findViewById(R.id.debate_card_party_logo);
            partyContainer = cardView.findViewById(R.id.debate_card_parties_holder);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        void bind(final PartyDocument item, final OnItemClickListener listener) {
            partyContainer.removeAllViews();

            //This is for preventing the old pictures to be shown when recycling the view.
            //(Problem with slow internet)
            authorView.setImageResource(0);
            partySymbol.setImageResource(0);

            documentTitle.setText(item.getTitel());
            debateDate.setText("Debattdag " + item.getDebattdag());
            author.setText(item.getUndertitel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

            ArrayList<Party> partiesInDebate = item.getDebatt().getPartiesInDebate();
            for (Party party : partiesInDebate) {
                ImageView partyIcon = new ImageView(RiksdagskollenApp.getInstance().getApplicationContext());
                partyIcon.setImageResource(party.getDrawableLogo());
                partyIcon.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
                partyContainer.addView(partyIcon);
            }

            ArrayList<Intressent> i = item.getDokintressent().getIntressenter();
            imageUrlRequest = RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getRepresentative(i.get(0).getIntressent_id(), new RepresentativeCallback() {
                @Override
                public void onPersonFetched(Representative representative) {
                    if (fragment.getActivity() != null) {
                        Glide
                                .with(fragment)
                                .load(representative.getBild_url_80())
                                .into(authorView);
                    }
                    partySymbol.setImageResource(CurrentParties.getParty(representative.getParti()).getDrawableLogo());
                }

                @Override
                public void onFail(VolleyError error) {

                }
            });


        }
    }


    static class BetDebateListViewHolder extends RecyclerView.ViewHolder {
        final TextView documentType;
        final TextView documentTitle;
        final TextView debateDate;
        final TextView author;
        final CircularImageView authorView;
        final ImageView partySymbol;
        final FlexboxLayout partyContainer;
        final Fragment fragment;
        Request imageUrlRequest;


        public BetDebateListViewHolder(View cardView, Fragment fragment) {
            super(cardView);
            this.fragment = fragment;
            documentTitle = cardView.findViewById(R.id.debate_card_title);
            debateDate = cardView.findViewById(R.id.debate_card_date);
            author = cardView.findViewById(R.id.debate_card_author);
            authorView = cardView.findViewById(R.id.debate_card_portrait);
            partySymbol = cardView.findViewById(R.id.debate_card_party_logo);
            partyContainer = cardView.findViewById(R.id.debate_card_parties_holder);
            documentType = cardView.findViewById(R.id.debate_card_doctype);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        void bind(final PartyDocument item, final OnItemClickListener listener) {
            partyContainer.removeAllViews();

            documentType.setText(DocumentType.getDocTypeForDocument(item).getDisplayName());

            documentTitle.setText(item.getTitel());
            debateDate.setText("Debattdag " + item.getDebattdag());
            author.setText(item.getUndertitel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

            ArrayList<Party> partiesInDebate = item.getDebatt().getPartiesInDebate();
            for (Party party : partiesInDebate) {
                ImageView partyIcon = new ImageView(RiksdagskollenApp.getInstance().getApplicationContext());
                partyIcon.setImageResource(party.getDrawableLogo());
                partyIcon.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
                partyContainer.addView(partyIcon);
            }


        }
    }

}
