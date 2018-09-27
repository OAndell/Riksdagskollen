package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.RepresentativeAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Callback.RepresentativeListCallback;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by oscar on 2018-09-27.
 */

public class RepresentativeListFragment extends RiksdagenAutoLoadingListFragment {

    private final List<Representative> representativeList = new ArrayList<>();
    private RepresentativeAdapter adapter;

    public static RepresentativeListFragment newInstance() {
        return new RepresentativeListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.representatives);
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getAllCurrentRepresentatives(new RepresentativeListCallback() {
            @Override
            public void onPersonListFetched(List<Representative> representatives) {
                setShowLoadingView(false);
                representativeList.addAll(representatives);
                getAdapter().addAll(representatives);
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementPage();
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RepresentativeAdapter(representativeList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                Intent repDetailsIntent = new Intent(getContext(), RepresentativeDetailActivity.class);
                repDetailsIntent.putExtra("representative", (Representative) document);
                startActivity(repDetailsIntent);
            }
        });

    }

    //not used for this fragment
    @Override
    protected void loadNextPage() {

    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
