package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.RepresentativeCallback;
import oscar.riksdagskollen.Util.Committee;
import oscar.riksdagskollen.Util.JSONModel.Intressent;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Representative;

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
        final NetworkImageView authorView;

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
            if(item.getPublicerad() != null && !item.getPublicerad().equals("null")) published.setText("Publicerad " + item.getPublicerad());
            else published.setText("");
            author.setText(item.getUndertitel());
            dokName.setText(item.getDokumentnamn());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            ArrayList<Intressent> i = item.getDokintressent().getIntressenter();

            authorView.setDefaultImageResId(R.mipmap.ic_default_person);
            authorView.invalidate();

            int senderCount = 0;
            for (Intressent intressent: i) {
                if (intressent.getRoll().equals("undertecknare")) senderCount++;
                if (senderCount > 1) break;
            }
            if (senderCount > 1 ) authorView.setVisibility(View.GONE);
            else {
                authorView.setVisibility(View.VISIBLE);
                RikdagskollenApp.getInstance().getRiksdagenAPIManager().getRepresentative(i.get(0).getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(Representative representative) {
                        authorView.setActivated(true);
                        authorView.setImageUrl(representative.getBild_url_192(),RikdagskollenApp.getInstance().getRequestManager().getmImageLoader());
                    }
                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
            }


        }
    }

    @Override
    public long getItemId(int position) {
        // Give loading view id 1
        if (position == documentList.size()) return 1;
        return documentList.get(position).uniqueDocId();
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
