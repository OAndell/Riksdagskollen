package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Committee;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by shelbot on 2018-03-27.
 */

public class PartyListViewholderAdapter extends RiksdagenViewHolderAdapter {
    private final List<PartyDocument> documentList;

    class MyViewHolder extends RecyclerView.ViewHolder{
        final TextView documentTitle;
        final TextView published;
        final TextView author;
        final TextView dokName;
        public MyViewHolder(View partyView) {
            super(partyView);
            documentTitle = partyView.findViewById(R.id.document);
            published = partyView.findViewById(R.id.publicerad);
            author = partyView.findViewById(R.id.författare);
            dokName = partyView.findViewById(R.id.dok_typ);
            partyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        void bind(final PartyDocument item, final OnItemClickListener listener) {
            documentTitle.setText(item.getTitel());
            published.setText("Publicerad " + item.getPublicerad());
            author.setText(item.getUndertitel());
            dokName.setText(item.getDokumentnamn());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }



    public PartyListViewholderAdapter(List<PartyDocument> documentList, OnItemClickListener clickListener) {
        super(documentList,clickListener);
        this.documentList = documentList;
        this.clickListener = clickListener;

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
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
        if(position < headers.size()){
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        }else if(position >= headers.size() + documentList.size()){
            View v = footers.get(position-documentList.size()-headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        }else {
            PartyDocument document = documentList.get(position);
            ((MyViewHolder) holder).bind(document,clickListener);
        }
    }




}
