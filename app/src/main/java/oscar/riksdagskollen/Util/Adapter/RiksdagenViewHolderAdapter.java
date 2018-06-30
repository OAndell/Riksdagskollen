package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gustavaaro on 2018-04-04.
 */

public abstract class RiksdagenViewHolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<?> items;
    protected List<View> headers = new ArrayList<>();
    protected List<View> footers = new ArrayList<>();

    protected static final int TYPE_HEADER = 111;
    protected static final int TYPE_FOOTER = 222;
    protected static final int TYPE_ITEM = 333;
    protected OnItemClickListener clickListener;

    public interface OnItemClickListener {

        void onItemClick(Object document);
    }


    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder{
        FrameLayout base;
        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }

    public RiksdagenViewHolderAdapter(List<?> items, OnItemClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if(position < headers.size()){
            return TYPE_HEADER;
        }else if(position >= headers.size() + items.size()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount(){
        return headers.size() + footers.size() + items.size();
    };

    public int getObjectCount(){
        return items.size();
    }

    protected void prepareHeaderFooter(HeaderFooterViewHolder vh, View view){
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
            notifyItemInserted(headers.size()+ items.size()+footers.size()-1);
        }
    }

    //remove a footer from the adapter
    public void removeFooter(View footer){
        if(footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size()+ items.size()+footers.indexOf(footer));
            footers.remove(footer);
            if(footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    }



}
