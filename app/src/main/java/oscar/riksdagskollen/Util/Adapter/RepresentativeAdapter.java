package oscar.riksdagskollen.Util.Adapter;

import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.View.CircularImageView;

/**
 * Created by oscar on 2018-09-24.
 */

public class RepresentativeAdapter extends RiksdagenViewHolderAdapter {


    public static Comparator<Representative> NAME_COMPARATOR = new Comparator<Representative>() {
        @Override
        public int compare(Representative a, Representative b) {
            return a.getTilltalsnamn().compareTo(b.getTilltalsnamn());
        }
    };

    public static Comparator<Representative> SURNAME_COMPARATOR = new Comparator<Representative>() {
        @Override
        public int compare(Representative a, Representative b) {
            return a.getEfternamn().compareTo(b.getEfternamn());
        }
    };

    public static Comparator<Representative> AGE_COMPARATOR = new Comparator<Representative>() {
        @Override
        public int compare(Representative a, Representative b) {
            return a.getAge().compareTo(b.getAge());
        }
    };

    public static Comparator<Representative> DISTRICT_COMPARATOR = new Comparator<Representative>() {
        @Override
        public int compare(Representative a, Representative b) {
            return a.getValkrets().compareTo(b.getValkrets());
        }
    };

    //Sort alphabetically after first name

    private Comparator<Representative> mComparator;
    private Fragment fragment;

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


    public RepresentativeAdapter(List<Representative> items, Comparator<Representative> comparator, Fragment fragment, final RiksdagenViewHolderAdapter.OnItemClickListener listener) {
        super(listener);
        this.fragment = fragment;
        this.mComparator = comparator;
        setSortedList(representativeList);
        addAll(items);
        this.clickListener = listener;
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


}
