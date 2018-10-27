package oscar.riksdagskollen.Fragment;

/**
 * Created by oscar on 2018-09-27.
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;

/**
 * Created by oscar on 2018-08-28.
 */

public class RepresentativeTabFragment extends Fragment {

    private ArrayList<Fragment> fragmentsTabs = new ArrayList<>();
    private ArrayList<String> tabNames = new ArrayList<>();
    private Menu menu;
    private int currentPage;

    public static RepresentativeTabFragment newInstance() {
        Bundle args = new Bundle();
        RepresentativeTabFragment newInstance = new RepresentativeTabFragment();
        newInstance.setArguments(args);
        newInstance.setRetainInstance(true);
        return newInstance;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = getActivity().findViewById(R.id.result_tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.setupWithViewPager(viewPager);
        //init tab lists
        return view;

    }

    public void addTab(Fragment tabFragment, String tabName) {
        fragmentsTabs.add(tabFragment);
        tabNames.add(tabName);
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        for (int i = 0; i < fragmentsTabs.size(); i++) {
            adapter.addFragment(fragmentsTabs.get(i), tabNames.get(i));
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                updateMenuItemAlpha(positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(adapter);
    }

    private void updateMenuItemAlpha(float positionOffset) {
        if (menu == null) return;
        MenuItem filter = menu.findItem(R.id.menu_filter);

        if (filter != null) {
            int alpha = (int) (255 - positionOffset * 255) - 255 * currentPage;
            filter.getIcon().setAlpha(alpha);
            Log.d("Pary", "updateMenuItemAlpha: " + alpha);
            if (alpha <= 0 && filter.isVisible()) {
                filter.setVisible(false);
            } else if (alpha > 0 && !filter.isVisible()) {
                filter.setVisible(true);
            }

        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
