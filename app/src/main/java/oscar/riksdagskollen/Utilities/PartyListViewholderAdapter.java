package oscar.riksdagskollen.Utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by shelbot on 2018-03-27.
 */

public class PartyListViewholderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PartyDocument> documentList;
    List<View> headers = new ArrayList<>();
    List<View> footers = new ArrayList<>();

    public static final int TYPE_HEADER = 111;
    public static final int TYPE_FOOTER = 222;
    public static final int TYPE_ITEM = 333;

    private OnPartyDocumentClickListener clickListener;

    public interface OnPartyDocumentClickListener {

        void onPartyDocumentClickListener(PartyDocument document);
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView documentTitle;
        TextView published;
        TextView author;
        TextView dokName;
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

        void bind(final PartyDocument item, final OnPartyDocumentClickListener listener) {
            documentTitle.setText(item.getTitel());
            published.setText("Publicerad " + item.getPublicerad());
            author.setText(item.getUndertitel());
            dokName.setText(item.getDokumentnamn());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onPartyDocumentClickListener(item);
                }
            });
        }
    }

    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder{
        FrameLayout base;
        HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }

    public PartyListViewholderAdapter(List<PartyDocument> documentList, OnPartyDocumentClickListener clickListener) {
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

    public void setDocumentList(List<PartyDocument> documentList) {
        this.documentList = documentList;
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

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if(position < headers.size()){
            return TYPE_HEADER;
        }else if(position >= headers.size() + documentList.size()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return documentList.size() + headers.size() + footers.size();
    }

    public int getDocumentCount(){
        return documentList.size();
    }

    private void prepareHeaderFooter(HeaderFooterViewHolder vh, View view){
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();
        vh.base.addView(view);
    }

    //add a header to the adapter
    public void addHeader(View header){
        if(!headers.contains(header)){
            headers.add(header);
            //animate
            notifyItemInserted(headers.size() - 1);
        }
    }

    //remove a header from the adapter
    public void removeHeader(View header){
        if(headers.contains(header)){
            //animate
            notifyItemRemoved(headers.indexOf(header));
            headers.remove(header);
            if(header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
        }
    }

    //add a footer to the adapter
    public void addFooter(View footer){
        if(!footers.contains(footer)){
            footers.add(footer);
            //animate
            notifyItemInserted(headers.size()+documentList.size()+footers.size()-1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer){
        if(footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size()+documentList.size()+footers.indexOf(footer));
            footers.remove(footer);
            if(footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    }


}
