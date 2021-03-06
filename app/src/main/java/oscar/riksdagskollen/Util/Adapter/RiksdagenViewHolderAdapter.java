package oscar.riksdagskollen.Util.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gustavaaro on 2018-04-04.
 */

public abstract class RiksdagenViewHolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private SortedList<?> sortedList;
    protected final List<View> headers = new ArrayList<>();
    protected final List<View> footers = new ArrayList<>();

    protected static final int TYPE_HEADER = 111;
    protected static final int TYPE_FOOTER = 222;
    protected static final int TYPE_ITEM = 333;
    protected OnItemClickListener clickListener;

    public interface OnItemClickListener {

        void onItemClick(Object document);
    }


    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder{
        final FrameLayout base;
        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }

    protected RiksdagenViewHolderAdapter(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setSortedList(SortedList<?> sortedList) {
        this.sortedList = sortedList;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > sortedList > footers
        if(position < headers.size()){
            return TYPE_HEADER;
        } else if (position >= headers.size() + sortedList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount(){
        return headers.size() + footers.size() + sortedList.size();
    };

    public int getObjectCount(){
        return sortedList.size();
    }

    protected void prepareHeaderFooter(HeaderFooterViewHolder vh, View view) {
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews();
        try {
            vh.base.addView(view);
        } catch (IllegalStateException e) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            if (parentViewGroup != null) parentViewGroup.removeView(view);
        }
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
    public void removeTopHeader() {
        if (!headers.isEmpty()) {
            //animate
            View header = headers.get(0);
            notifyItemRemoved(headers.indexOf(header));
            headers.remove(header);
            if(header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
        }
    }

    public abstract void replaceAll(List<?> items);

    public abstract void addAll(List<?> items);

    public abstract void removeAll(List<?> items);

    public void clear() {
        sortedList.clear();
    }

    //add a footer to the adapter
    public void addFooter(View footer){
        if(!footers.contains(footer)){
            footers.add(footer);
            //animate
            notifyItemInserted(headers.size() + sortedList.size() + footers.size() - 1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer){
        if(footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size() + sortedList.size() + footers.indexOf(footer));
            footers.remove(footer);
            if(footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    }


}
