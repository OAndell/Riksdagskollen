package oscar.riksdagskollen.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListAdapter  extends RecyclerView.Adapter<CurrentNewsListAdapter.NewsViewHolder>{
    private List<CurrentNews> newsList;

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        public TextView document;
        public NewsViewHolder(View textView) {
            super(textView);
            document = (TextView) textView.findViewById(R.id.document);

        }

        public void bind(final CurrentNews item) {
            document.setText(item.getTitel());
        }

    }

    public CurrentNewsListAdapter(List<CurrentNews> newsList) {
        this.newsList = newsList;
    }


    @Override
    public CurrentNewsListAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.party_list_row, parent, false);
        return new CurrentNewsListAdapter.NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        CurrentNews currentNews = newsList.get(position);
        holder.bind(currentNews);
    }

    public void setDocumentList(List<CurrentNews> newsList) {
        this.newsList = newsList;
    }


    @Override
    public int getItemCount() {
        return newsList.size();
    }


}
