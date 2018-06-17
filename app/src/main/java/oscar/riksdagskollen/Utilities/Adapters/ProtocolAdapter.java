package oscar.riksdagskollen.Utilities.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.Protocol;

/**
 * Created by oscar on 2018-03-29.
 */

public class ProtocolAdapter  extends RiksdagenViewHolderAdapter{
    private List<Protocol> protocolsList;

    public ProtocolAdapter(List<Protocol> items, final OnItemClickListener listener) {
        super(items, listener);
        this.clickListener = listener;
        protocolsList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.protocol_list_item, parent, false);
            return new ProtocolAdapter.ProtocolViewHolder(itemView);
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
        }else if(position >= headers.size() + protocolsList.size()){
            View v = footers.get(position-protocolsList.size()-headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        }else {
            Protocol document = protocolsList.get(position);
            ((ProtocolAdapter.ProtocolViewHolder) holder).bind(document, this.clickListener);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class ProtocolViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView summary;
        private TextView date;

        public ProtocolViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            summary = itemView.findViewById(R.id.summary);
        }

        public void bind(final Protocol item ,final OnItemClickListener listener) {
            title.setText(item.getTitel());
            date.setText(item.getDatum());
            summary.setText(String.format("%s%s",item.getSummary(),"..."));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }



}
