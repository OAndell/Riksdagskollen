package oscar.riksdagskollen.Utilities;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.io.UnsupportedEncodingException;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListAdapter  extends RiksdagenViewHolderAdapter{
    private List<CurrentNews> newsList;

    public CurrentNewsListAdapter(List<CurrentNews> items) {
        super(items, new OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {

            }
        });
        newsList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_list_row, parent, false);
            return new CurrentNewsListAdapter.NewsViewHolder(itemView);
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
        }else if(position >= headers.size() + newsList.size()){
            View v = footers.get(position-newsList.size()-headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        }else {
            CurrentNews document = newsList.get(position);
            ((CurrentNewsListAdapter.NewsViewHolder) holder).bind(document);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class NewsViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView body;
        private TextView date;
        private TextView imageText;
        private NetworkImageView image;

        public NewsViewHolder(View textView) {
            super(textView);
            title = textView.findViewById(R.id.title);
            body = textView.findViewById(R.id.body_text);
            date = textView.findViewById(R.id.publicerad);
            imageText = textView.findViewById(R.id.image_text);
            image =  textView.findViewById(R.id.image);
        }

        public void bind(final CurrentNews item) {
            title.setText(item.getTitel());
            body.setText(Html.fromHtml(item.getSummary()));
            date.setText(item.getPublicerad());
            imageText.setText(item.getImg_text());
            if(item.getImg_url() != null){
                image.setVisibility(View.VISIBLE);
                System.out.println("BILD FÖR :  " + item.getTitel());
                image.setImageUrl("http://riksdagen.se" + item.getImg_url(),
                        RikdagskollenApp.getInstance().getRequestManager().getmImageLoader());
            }
            else {
                image.setVisibility(View.GONE);
            }
        }
    }



}
