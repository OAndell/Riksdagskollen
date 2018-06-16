package oscar.riksdagskollen.Utilities.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.DecisionDocument;

/**
 * Created by oscar on 2018-03-29.
 */

public class DecisionListAdapter extends RiksdagenViewHolderAdapter {
    private List<DecisionDocument> decisionDocuments;
    private Context context;

    public DecisionListAdapter(List<DecisionDocument> items, final OnItemClickListener listener) {
        super(items, listener);
        this.clickListener = listener;
        decisionDocuments = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.decision_list_item, parent, false);
            return new DecisionView(itemView);
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
        } else if (position >= headers.size() + decisionDocuments.size()) {
            View v = footers.get(position - decisionDocuments.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            DecisionDocument document = decisionDocuments.get(position);
            ((DecisionView) holder).bind(document, this.clickListener);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class DecisionView extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView body;
        private TextView date;
        private TextView imageText;
        private NetworkImageView image;

        private View itemView;


        public DecisionView(final View view) {
            super(view);

            this.itemView = view;
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body_text);
            date = view.findViewById(R.id.publicerad);
            imageText = view.findViewById(R.id.image_text);
            image = view.findViewById(R.id.image);
        }

        public void bind(final DecisionDocument item, final OnItemClickListener listener) {
            title.setText(item.getNotisrubrik());
            date.setText(item.getPublicerad());
            if (item.isExpanded()){
                body.setVisibility(View.VISIBLE);
            } else {
                body.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                    if (item.isExpanded()) {
                        collapse(true);
                        item.setExpanded(false);
                    } else {
                        collapse(false);
                        item.setExpanded(true);
                    }
                }
            });
        }


        void collapse(boolean collapse) {
            TransitionManager.beginDelayedTransition((ViewGroup) itemView.getParent());
            if (collapse) body.setVisibility(View.GONE);
            else body.setVisibility(View.VISIBLE);
        }


    }
}