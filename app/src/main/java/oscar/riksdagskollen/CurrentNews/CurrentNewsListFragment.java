package oscar.riksdagskollen.CurrentNews;

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

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels.CurrentNews;
import oscar.riksdagskollen.Fragment.RiksdagenAutoLoadingListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;
import oscar.riksdagskollen.Util.RiksdagenCallback.RateResultListener;
import oscar.riksdagskollen.Util.View.PlayRatingQuestionView;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends RiksdagenAutoLoadingListFragment implements CurrentNewsContract.View {
    public static final String SECTION_NAME_NEWS = "news";
    private final List<CurrentNews> newsList = new ArrayList<>();
    private CurrentNewsListAdapter adapter;
    private MenuItem notificationItem;
    private CurrentNewsContract.Presenter presenter = new CurrentNewsPresenter(this);

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
        adapter = new CurrentNewsListAdapter(newsList, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {
                presenter.handleItemClick((CurrentNews) document, getContext());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification_menu_item:
                if (newsList.size() > 0) {
                    presenter.handleNotificationItemClick(newsList.get(0).getId());
                } else {
                    presenter.handleNotificationItemClick("");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_menu, menu);
        notificationItem = menu.findItem(R.id.notification_menu_item);
        if (presenter.isNotificationsEnabled()) {
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
        presenter.loadMoreItems();
    }

    @Override
    protected void clearItems() {
        newsList.clear();
        adapter.clear();
    }

    @Override
    protected RiksdagenViewHolderAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void addItemsToView(List<CurrentNews> news) {
        newsList.addAll(news);
        getAdapter().addAll(news);
    }

    @Override
    public void showLoadingView(boolean loading) {
        setShowLoadingView(loading);
    }

    @Override
    public void showLoadingItemsView(boolean loading) {
        setLoadingMoreItems(loading);
    }

    @Override
    public void showPlayRatingView() {
        if (this.getContext() == null) return;
        final PlayRatingQuestionView view = new PlayRatingQuestionView(this.getContext());
        view.setRateResultListener(new RateResultListener() {
            @Override
            public void onResult() {
                adapter.removeHeader(view);
            }
        });
        adapter.addHeader(view);
    }

    @Override
    public void showLoadFailView() {
        onLoadFail();
    }

    @Override
    public void fillNotificationItem() {
        notificationItem.setIcon(R.drawable.notifications_border_to_filled_animated);
        Toast.makeText(getContext(), "Du kommer nu få en notis när en ny nyhet publiceras", Toast.LENGTH_LONG).show();
        animateMenuItem();
    }

    @Override
    public void unFillNotificationItem() {
        notificationItem.setIcon(R.drawable.notifications_filled_to_border_animated);
        animateMenuItem();
    }

    private void animateMenuItem() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Animatable) notificationItem.getIcon()).start();
            }
        });
    }
}
