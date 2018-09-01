package oscar.riksdagskollen.Util.Adapter;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.JSONModel.Protocol;

/**
 * Created by oscar on 2018-03-29.
 */

public class ProtocolAdapter  extends RiksdagenViewHolderAdapter{
    private final SortedList<Protocol> protocolsList = new SortedList<Protocol>(Protocol.class, new SortedList.Callback<Protocol>() {
        @Override
        public int compare(Protocol o1, Protocol o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Protocol oldItem, Protocol newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Protocol item1, Protocol item2) {
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

    private static final Comparator<Protocol> DEFAULT_COMPARATOR = new Comparator<Protocol>() {
        @Override
        public int compare(Protocol a, Protocol b) {
            return 0;
        }
    };
    private Comparator<Protocol> mComparator = DEFAULT_COMPARATOR;


    public ProtocolAdapter(List<Protocol> items, final OnItemClickListener listener) {
        super(listener);
        setSortedList(protocolsList);
        addAll(items);

        this.clickListener = listener;
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

    public void add(Protocol model) {
        protocolsList.add(model);
    }

    public void remove(Protocol model) {
        protocolsList.remove(model);
    }

    @Override
    public void addAll(List<?> items) {
        protocolsList.addAll((Collection<Protocol>) items);
    }

    @Override
    public void removeAll(List<?> items) {
        protocolsList.beginBatchedUpdates();
        for (Object item : items) {
            protocolsList.remove((Protocol) item);
        }
        protocolsList.endBatchedUpdates();
    }

    @Override
    public void replaceAll(List<?> items) {
        protocolsList.beginBatchedUpdates();
        protocolsList.clear();
        ;
        protocolsList.addAll((Collection<Protocol>) items);
        protocolsList.endBatchedUpdates();
    }


    /**
     * Class for displaying individual items in the list.
     */
    public class ProtocolViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView date;

        public ProtocolViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
        }

        public void bind(final Protocol item ,final OnItemClickListener listener) {
            title.setText(trimTitle(item.getTitel()));
            date.setText("Publicerad " + item.getDatum());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        //Removes text "Protokoll 2017/18:XYZ" from title
        private String trimTitle(String title){
            return title.split(":\\d+")[1].trim();
        }

    }



}
