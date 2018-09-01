package oscar.riksdagskollen.Util.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.RepresentativeCallback;
import oscar.riksdagskollen.Util.JSONModel.Intressent;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Representative;
import oscar.riksdagskollen.Util.View.CircularNetworkImageView;

/**
 * Created by shelbot on 2018-03-27.
 */

public class PartyListViewholderAdapter extends RiksdagenViewHolderAdapter {
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

    private static final Comparator<PartyDocument> DEFAULT_COMPARATOR = new Comparator<PartyDocument>() {
        @Override
        public int compare(PartyDocument a, PartyDocument b) {
            return 0;
        }
    };

    private Comparator<PartyDocument> mComparator = DEFAULT_COMPARATOR;


    private final RikdagskollenApp app = RikdagskollenApp.getInstance();


    public PartyListViewholderAdapter(List<PartyDocument> items, OnItemClickListener clickListener) {
        super(clickListener);
        setSortedList(documentList);
        addAll(items);

        this.clickListener = clickListener;
    }

    public void setComparator(Comparator<PartyDocument> mComparator) {
        this.mComparator = mComparator;
    }

    @Override
    public long getItemId(int position) {
        // Give loading view id 1
        if (position == documentList.size()) return 1;
        return documentList.get(position).uniqueDocId();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.party_list_row, parent, false);
            return new MyViewHolder(itemView);
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
            PartyDocument document = documentList.get(position);
            ((MyViewHolder) holder).bind(document, clickListener);
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
        ;
        documentList.addAll((Collection<PartyDocument>) items);
        documentList.endBatchedUpdates();
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = ((MyViewHolder) holder);
            if (viewHolder.imageUrlRequest != null) viewHolder.imageUrlRequest.cancel();
            viewHolder.authorView.setImageResource(R.mipmap.ic_default_person);

        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView documentTitle;
        final TextView published;
        final TextView author;
        final TextView dokName;
        final CircularNetworkImageView authorView;
        Request imageUrlRequest;

        public MyViewHolder(View partyView) {
            super(partyView);
            documentTitle = partyView.findViewById(R.id.document);
            published = partyView.findViewById(R.id.publicerad);
            author = partyView.findViewById(R.id.författare);
            dokName = partyView.findViewById(R.id.dok_typ);
            authorView = partyView.findViewById(R.id.author_img);

            partyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        void bind(final PartyDocument item, final OnItemClickListener listener) {
            documentTitle.setText(item.getTitel());

            // Handle documents without publication date
            if (item.getPublicerad() != null && !item.getPublicerad().equals("null"))
                published.setText("Publicerad " + item.getPublicerad());
            else if (item.getDatum() != null && !item.getDatum().equals("null"))
                published.setText("Publicerad " + item.getDatum());
            else published.setText("");
            author.setText(item.getUndertitel());
            dokName.setText(item.getDokumentnamn());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });


            final ArrayList<Intressent> i = item.getDokintressent().getIntressenter();


            int senderCount = 0;
            for (Intressent intressent : i) {
                if (intressent.getRoll().equals("undertecknare")) senderCount++;
                if (senderCount > 1) break;
            }
            if (senderCount > 1) authorView.setVisibility(View.GONE);
            else {
                authorView.setVisibility(View.VISIBLE);
                imageUrlRequest = app.getRiksdagenAPIManager().getRepresentative(i.get(0).getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(Representative representative) {
                        authorView.setImageUrl(representative.getBild_url_192(), app.getRequestManager().getmImageLoader());
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
            }

        }
    }
}
