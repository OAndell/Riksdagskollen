package oscar.riksdagskollen.RepresentativeList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.View.CircularImageView;

/**
 * Created by oscar on 2018-09-24.
 */

public class RepresentativeAdapter extends RiksdagenViewHolderAdapter {

    public enum SortingMode {
        NAME,
        SURNAME,
        AGE,
        DISTRICT
    }


    public static Comparator<Representative> NAME_COMPARATOR = (a, b) -> a.getTilltalsnamn().compareTo(b.getTilltalsnamn());

    public static Comparator<Representative> SURNAME_COMPARATOR = (a, b) -> a.getEfternamn().compareTo(b.getEfternamn());

    public static Comparator<Representative> AGE_COMPARATOR = (a, b) -> a.getAge().compareTo(b.getAge());

    public static Comparator<Representative> DISTRICT_COMPARATOR = (a, b) -> a.getValkrets().compareTo(b.getValkrets());

    //Sort alphabetically after first name

    private Comparator<Representative> mComparator;
    private Fragment fragment;
    private SortingMode sortingMode;
    private boolean ascending;

    private final SortedList<Representative> representativeList = new SortedList<>(Representative.class, new SortedList.Callback<Representative>() {
        @Override
        public int compare(Representative o1, Representative o2) {
            return mComparator.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Representative oldItem, Representative newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Representative item1, Representative item2) {
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

    @Override
    public long getItemId(int position) {
        return Long.parseLong(representativeList.get(position).getIntressent_id());
    }

    public RepresentativeAdapter(List<Representative> items, SortingMode sortingMode, boolean ascending, Fragment fragment, final RiksdagenViewHolderAdapter.OnItemClickListener listener) {
        super(listener);
        this.sortingMode = sortingMode;
        switch (sortingMode) {
            case NAME:
                mComparator = NAME_COMPARATOR;
                break;
            case SURNAME:
                mComparator = SURNAME_COMPARATOR;
                break;
            case AGE:
                mComparator = AGE_COMPARATOR;
                break;
            case DISTRICT:
                mComparator = DISTRICT_COMPARATOR;
                break;
        }
        if (!ascending) mComparator = new ReverseOrder<>(mComparator);

        this.fragment = fragment;
        setSortedList(representativeList);
        addAll(items);
        this.clickListener = listener;
    }

    public String getIndicatorStringAtIndex(int index) {
        if (sortingMode.equals(SortingMode.NAME)) {
            return representativeList.get(index).getTilltalsnamn().substring(0, 1);
        } else if (sortingMode.equals(SortingMode.AGE)) {
            return representativeList.get(index).getAge();
        } else if (sortingMode.equals(SortingMode.DISTRICT)) {
            return representativeList.get(index).getValkrets().substring(0, 1);
        } else if (sortingMode.equals(SortingMode.SURNAME)) {
            return representativeList.get(index).getEfternamn().substring(0, 1);
        }
        return "";
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.representative_list_row, parent, false);
            return new RepresentativeAdapter.RepresentativeViewHolder(itemView, fragment);
        } else {

            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < headers.size()) {
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else if (position >= headers.size() + representativeList.size()) {
            View v = footers.get(position - representativeList.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            Representative item = representativeList.get(position);
            ((RepresentativeAdapter.RepresentativeViewHolder) holder).bind(item, this.clickListener);
        }
    }

    @Override
    public void replaceAll(List<?> items) {
        representativeList.beginBatchedUpdates();
        representativeList.clear();
        representativeList.addAll((Collection<Representative>) items);
        representativeList.endBatchedUpdates();
    }


    @Override
    public void addAll(List<?> items) {
        representativeList.addAll((Collection<Representative>) items);
    }

    @Override
    public void removeAll(List<?> items) {
        representativeList.replaceAll((Collection<Representative>) items);
    }

    /**
     * Class for displaying individual items in the list.
     */
    static class RepresentativeViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView born;
        private final TextView valkrets;
        private final TextView title;
        private final TextView titleLabel;
        private final CircularImageView portrait;
        private final ImageView partyLogo;
        private Fragment fragment;

        public RepresentativeViewHolder(View itemView, Fragment fragment) {
            super(itemView);
            this.fragment = fragment;
            name = itemView.findViewById(R.id.rep_card_name);
            born = itemView.findViewById(R.id.rep_card_born);
            valkrets = itemView.findViewById(R.id.rep_card_valkrets);
            titleLabel = itemView.findViewById(R.id.rep_card_title_label);
            title = itemView.findViewById(R.id.rep_card_title);
            portrait = itemView.findViewById(R.id.rep_card_portrait);
            partyLogo = itemView.findViewById(R.id.rep_card_party_logo);

        }

        public void bind(final Representative item, final RiksdagenViewHolderAdapter.OnItemClickListener listener) {
            name.setText(item.getTilltalsnamn() + " " + item.getEfternamn());
            valkrets.setText(item.getValkrets());
            born.setText(item.getAge() + " år");
            setTitle(item);

            if (fragment.getActivity() != null) {
                Glide
                        .with(fragment)
                        .load(item.getBild_url_80())
                        .into(portrait);
            }

            try {
                partyLogo.setImageResource(CurrentParties.getParty(item.getParti().toLowerCase()).getDrawableLogo());
            } catch (Exception e) {//No party found, Does not belong to a party
                partyLogo.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        private void setTitle(Representative representative) {
            String titleStr = representative.getTitle();
            if (titleStr.equals("")) {
                title.setVisibility(View.GONE);
                titleLabel.setVisibility(View.GONE);
            } else {
                title.setText(titleStr);

            }
        }
    }

    private class ReverseOrder<T> implements Comparator<T> {
        private Comparator<T> delegate;

        ReverseOrder(Comparator<T> delegate) {
            this.delegate = delegate;
        }

        public int compare(T a, T b) {
            //reverse order of a and b!!!
            return this.delegate.compare(b, a);
        }
    }


}
