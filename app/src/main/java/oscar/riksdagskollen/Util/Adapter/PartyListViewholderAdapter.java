package oscar.riksdagskollen.Util.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.DokIntressent;
import oscar.riksdagskollen.Util.JSONModel.Intressent;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeCallback;
import oscar.riksdagskollen.Util.View.CircularImageView;

/**
 * Created by shelbot on 2018-03-27.
 */

public class PartyListViewholderAdapter extends RiksdagenViewHolderAdapter {
    private static final Comparator<PartyDocument> DEFAULT_COMPARATOR = new Comparator<PartyDocument>() {
        @Override
        public int compare(PartyDocument a, PartyDocument b) {
            return 0;
        }
    };
    private final RiksdagskollenApp app = RiksdagskollenApp.getInstance();
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
    Fragment fragment;


    public PartyListViewholderAdapter(List<PartyDocument> items, OnItemClickListener clickListener, Fragment fragment) {
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

        /*
        // Give loading view id 1
        if (position == documentList.size()) return 1;
        // Header and loading view visible
        if (position > documentList.size()) return documentList.get(position-1).uniqueDocId();
        return documentList.get(position).uniqueDocId();*/
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.party_list_row, parent, false);
            return new PartyListViewHolder(itemView, fragment);
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
            ((PartyListViewHolder) holder).bind(document, clickListener);
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
        if (holder instanceof PartyListViewHolder) {
            PartyListViewHolder viewHolder = ((PartyListViewHolder) holder);
            if (viewHolder.imageUrlRequest != null) viewHolder.imageUrlRequest.cancel();
        }
    }

    static class PartyListViewHolder extends RecyclerView.ViewHolder {
        final TextView documentTitle;
        final TextView published;
        final TextView author;
        final TextView dokName;
        final CircularImageView authorView;
        Request imageUrlRequest;
        final Fragment fragment;

        public PartyListViewHolder(View partyView, Fragment fragment) {
            super(partyView);
            this.fragment = fragment;
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

            ArrayList<Intressent> i = new ArrayList<>();
            DokIntressent dokIntressent = item.getDokintressent();
            if (dokIntressent != null) i = item.getDokintressent().getIntressenter();

            int senderCount = 0;
            for (Intressent intressent : i) {
                if (intressent.getRoll().equals("undertecknare")) senderCount++;
                if (senderCount > 1) break;
            }

            if (senderCount != 1 || RiksdagskollenApp.getInstance().isDataSaveModeActive()) {
                authorView.setVisibility(View.GONE);
            }

            else {
                authorView.setVisibility(View.VISIBLE);
                imageUrlRequest = RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getRepresentative(i.get(0).getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(Representative representative) {
                        if (fragment.getActivity() != null) {
                            Glide
                                    .with(fragment)
                                    .load(representative.getBild_url_80())
                                    .into(authorView);
                        }
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
            }

        }
    }
}
