package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.JSONModel.Vote;

/**
 * Created by oscar on 2018-03-29.
 */

public class VoteAdapter  extends RiksdagenViewHolderAdapter{
    private List<Vote> voteList;

    public VoteAdapter(List<Vote> items, final OnItemClickListener listener) {
        super(items, listener);
        this.clickListener = listener;
        voteList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.protocol_list_item, parent, false);
            return new VoteAdapter.VoteViewHolder(itemView);
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
        }else if(position >= headers.size() + voteList.size()){
            View v = footers.get(position-voteList.size()-headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        }else {
            Vote document = voteList.get(position);
            ((VoteAdapter.VoteViewHolder) holder).bind(document, this.clickListener);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class VoteViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView docType;
        private TextView date;


        public VoteViewHolder(View textView) {
            super(textView);
            title = textView.findViewById(R.id.title);
            docType = textView.findViewById(R.id.dok_typ);
            date = textView.findViewById(R.id.date);
        }

        public void bind(final Vote item ,final OnItemClickListener listener) {
            title.setText(item.getTitel());
            docType.setText(R.string.vote);
            date.setText(item.getDatum());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }



}

