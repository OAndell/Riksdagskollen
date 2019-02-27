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

import oscar.riksdagskollen.Activity.ProtocolReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.ProtocolAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;


/**
 * Created by oscar on 2018-03-29.
 */

public class ProtocolListFragment extends RiksdagenAutoLoadingListFragment {

    private final List<Protocol> protocolList = new ArrayList<>();
    private ProtocolAdapter adapter;

    public static ProtocolListFragment newInstance(){
        ProtocolListFragment newInstance = new ProtocolListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.prot);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProtocolAdapter(protocolList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                /*Protocol protocol = (Protocol) document;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + protocol.getDokument_url_html().substring(2)));
                startActivity(browserIntent);*/

                Intent intent = new Intent(getContext(), ProtocolReaderActivity.class);
                intent.putExtra("url", ((Protocol) document).getDokument_url_html());
                intent.putExtra("title",((Protocol) document).getTitel());
                intent.putExtra("id", ((Protocol) document).getId());
                startActivity(intent);
            }
        });

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage(){
        setLoadingMoreItems(true);
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getProtocols(new ProtocolCallback() {

            @Override
            public void onProtocolsFetched(List<Protocol> protocols) {
                setShowLoadingView(false);
                protocolList.addAll(protocols);
                getAdapter().addAll(protocols);
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                onLoadFail();
            }
        },getPageToLoad());

        incrementPage();
    }

    @Override
    protected void clearItems() {
        protocolList.clear();
        adapter.clear();
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
