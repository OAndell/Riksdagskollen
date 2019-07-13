package oscar.riksdagskollen.Fragment;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.CurrentNewsListAdapter;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.JSONModel.CurrentNewsModels.CurrentNews;
import oscar.riksdagskollen.Util.RiksdagenCallback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RateResultListener;
import oscar.riksdagskollen.Util.View.PlayRatingQuestionView;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends RiksdagenAutoLoadingListFragment {
    private final List<CurrentNews> newsList = new ArrayList<>();
    private CurrentNewsListAdapter adapter;
    public static final String SECTION_NAME_NEWS = "news";
    private MenuItem notificationItem;


    public static CurrentNewsListFragment newInstance() {
        CurrentNewsListFragment newInstance = new CurrentNewsListFragment();
        return newInstance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        adapter = new CurrentNewsListAdapter(newsList, this, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                CurrentNews newsDoc = (CurrentNews) document;
                if (newsDoc.getUrl() != null)
                    CustomTabs.openTab(getContext(), newsDoc.getNewsUrl());
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification_menu_item:
                boolean enabled;
                try {
                    enabled = RiksdagskollenApp.getInstance().getAlertManager().toggleEnabledForPage(SECTION_NAME_NEWS, newsList.get(0).getId());

                } catch (IndexOutOfBoundsException e) {
                    // if no items has been loaded, fall back on an empty string. Doc id will be updated once some items has been loaded
                    enabled = RiksdagskollenApp.getInstance().getAlertManager().toggleEnabledForPage(SECTION_NAME_NEWS, "");
                }
                if (enabled) {
                    item.setIcon(R.drawable.notifications_border_to_filled_animated);
                    Toast.makeText(getContext(), "Du kommer nu få en notis när en ny nyhet publiceras", Toast.LENGTH_LONG).show();
                } else {
                    item.setIcon(R.drawable.notifications_filled_to_border_animated);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Animatable) item.getIcon()).start();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_menu, menu);
        notificationItem = menu.findItem(R.id.notification_menu_item);
        if (RiksdagskollenApp.getInstance().getAlertManager().isAlertEnabledForSection(SECTION_NAME_NEWS)) {
            notificationItem.setIcon(R.drawable.ic_notification_enabled);
        }
        if (newsList.size() > 0) {
            notificationItem.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.s
     */
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        final Context context = this.getContext();

        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(new CurrentNewsCallback() {
            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {

                if (getPageToLoad() <= 2) {
                    if (RiksdagskollenApp.getInstance().shouldAskForRating()) {
                        final PlayRatingQuestionView view = new PlayRatingQuestionView(context);
                        adapter.removeTopHeader();
                        view.setRateResultListener(new RateResultListener() {
                            @Override
                            public void onResult() {
                                adapter.removeTopHeader();
                            }
                        });
                        adapter.addHeader(view);
                    }

                }

                setShowLoadingView(false);
                newsList.addAll(currentNews);
                getAdapter().addAll(currentNews);
                setLoadingMoreItems(false);

                if (getPageToLoad() <= 2) {
                    updateAlertsLatestDocument();
                }
            }

            @Override
            public void onFail(VolleyError error) {
                decrementPage();
                onLoadFail();
            }
        }, getPageToLoad());
        incrementPage();
    }

    @Override
    protected void clearItems() {
        adapter.removeTopHeader();
        adapter.clear();
        newsList.clear();
    }

    private void updateAlertsLatestDocument() {
        if (AlertManager.getInstance().isAlertEnabledForSection(SECTION_NAME_NEWS)) {
            AlertManager.getInstance().setAlertEnabledForSection(SECTION_NAME_NEWS, newsList.get(0).getId(), true);
        }
    }

    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }
}
