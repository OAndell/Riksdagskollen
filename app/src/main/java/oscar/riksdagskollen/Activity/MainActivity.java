package oscar.riksdagskollen.Activity;

import android.arch.core.BuildConfig;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import oscar.riksdagskollen.About.AboutFragment;
import oscar.riksdagskollen.CurrentNews.CurrentNewsListFragment;
import oscar.riksdagskollen.DebateList.DebateListFragment;
import oscar.riksdagskollen.Fragment.DecisionsListFragment;
import oscar.riksdagskollen.Fragment.ProtocolListFragment;
import oscar.riksdagskollen.Fragment.RepresentativeListFragment;
import oscar.riksdagskollen.Fragment.SavedDocumentsFragment;
import oscar.riksdagskollen.Fragment.SearchListFragment;
import oscar.riksdagskollen.Fragment.TwitterListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Manager.AnalyticsManager;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.Helper.AppBarStateChangeListener;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.Helper.NotificationHelper;
import oscar.riksdagskollen.Util.Helper.RiksdagskollenFragmentFactory;

import static oscar.riksdagskollen.Util.Helper.NotificationHelper.NEWS_ITEM_URL_KEY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private boolean emptyToolbar = false;
    private ImageView collapsingLogo;
    private AnalyticsManager analyticsManager;
    private RiksdagskollenFragmentFactory fragmentFactory = new RiksdagskollenFragmentFactory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingLogo = findViewById(R.id.riksdagskollen_logo_collapsing);

        analyticsManager = AnalyticsManager.getInstance();

        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        // Fresh start
        if (savedInstanceState == null) {
            startLauncherTransition();
            // Mark News-fragment as selected at startup
            onNavigationItemSelected(navigationView.getMenu().getItem(0).getSubMenu().getItem(0));
            navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);
        } else {
            // Apply theme
            emptyToolbar = false;
            toggle.setDrawerIndicatorEnabled(true);
            invalidateOptionsMenu();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleOpenWithNotification(intent);
    }

    private void handleOpenWithNotification(@Nullable Intent intent) {
        Intent incoming;
        if (intent == null) {
            incoming = getIntent();
        } else {
            incoming = intent;
        }

        if (incoming.hasExtra(NotificationHelper.DOCUMENT_KEY)) {
            Intent docIntent = new Intent(this, DocumentReaderActivity.class);
            docIntent.putExtra("document", incoming.getParcelableExtra("document"));
            startActivity(docIntent);
        } else if (incoming.hasExtra(NotificationHelper.SECTION_NAME_KEY)) {
            switch (incoming.getStringExtra(NotificationHelper.SECTION_NAME_KEY)) {
                case "m":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.m_nav));
                    break;
                case "s":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.s_nav));
                    break;
                case "c":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.c_nav));
                    break;
                case "l":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.l_nav));
                    break;
                case "mp":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.mp_nav));
                    break;
                case "kd":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.kd_nav));
                    break;
                case "sd":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.sd_nav));
                    break;
                case "v":
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.v_nav));
                    break;
                case VoteListFragment.SECTION_NAME_VOTE:
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.votes_nav));
                    break;
                case CurrentNewsListFragment.SECTION_NAME_NEWS:
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.news_nav));
                    String url = incoming.getStringExtra(NEWS_ITEM_URL_KEY);
                    try {
                        CustomTabs.openTab(this, url);
                    } catch (ActivityNotFoundException e) { //Some news might not work
                        Toast.makeText(this, "Kunde inte öppna nyhet", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (emptyToolbar) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_theme:
                final ThemeManager themeManager = RiksdagskollenApp.getInstance().getThemeManager();
                final ThemeManager.Theme[] themes = ThemeManager.Theme.values();

                final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                        .setTitle("Välj utseende")
                        .setSingleChoiceItems(
                                ThemeManager.Theme.getDisplayNames(),
                                themeManager.getCurrentThemeIndex(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        themeManager.setTheme(themes[i]);
                                        applyTheme();
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                dialog.show();
                break;
            case R.id.menu_preferences:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLauncherTransition() {
        emptyToolbar = true;
        toggle.setDrawerIndicatorEnabled(false);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingLogo.setVisibility(View.VISIBLE);
        Display display = getWindowManager().getDefaultDisplay();

        int height;
        int width;
        int magicNumber = 130;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            display.getRealMetrics(realDisplayMetrics);
            height = realDisplayMetrics.heightPixels - magicNumber;
            width = realDisplayMetrics.widthPixels;
        } else {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        }
        collapsingLogo.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(width, height));
        collapsingToolbarLayout.setTitle(" ");

        Handler handler = new Handler();

        final AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, final State state) {
                appBarLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (state.equals(State.COLLAPSED)) {
                            collapsingToolbarLayout.setTitleEnabled(false);

                            try {
                                collapsingToolbarLayout.removeView(collapsingLogo);
                            } catch (Exception e) {
                                //TODO FIGURE out why collapsingLogo.getParent() is null sometimes
                            }

                            emptyToolbar = false;
                            toggle.setDrawerIndicatorEnabled(true);
                            invalidateOptionsMenu();
                        }
                    }
                });

            }

            @Override
            public void onOffsetChange(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                float alpha = 255 - (255 * percentage);
                collapsingLogo.setAlpha((int) alpha);
            }
        };
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(false, true);
                handleOpenWithNotification(null);

            }
        }, 200);
    }

    private void applyTheme() {
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        recreate();
    }

    public boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Ugly hack to prevent News menu item to be checked forever
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);

        switch (id) {
            case R.id.news_nav:
                analyticsManager.setCurrentScreen(this, CurrentNewsListFragment.SECTION_NAME_NEWS);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentNewsListFragment.SECTION_NAME_NEWS)).commit();
                break;
            case R.id.votes_nav:
                analyticsManager.setCurrentScreen(this, VoteListFragment.SECTION_NAME_VOTE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(VoteListFragment.SECTION_NAME_VOTE)).commit();
                break;
            case R.id.dec_nav:
                analyticsManager.setCurrentScreen(this, DecisionsListFragment.SECTION_NAME_DECISIONS);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(DecisionsListFragment.SECTION_NAME_DECISIONS)).commit();
                break;
            case R.id.rep_nav:
                analyticsManager.setCurrentScreen(this, RepresentativeListFragment.SECTION_NAME_REPS);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(RepresentativeListFragment.SECTION_NAME_REPS)).commit();
                break;
            case R.id.prot_nav:
                analyticsManager.setCurrentScreen(this, ProtocolListFragment.SECTION_NAME_protocol);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(ProtocolListFragment.SECTION_NAME_protocol)).commit();
                break;
            case R.id.debate_nav:
                analyticsManager.setCurrentScreen(this, DebateListFragment.SECTION_NAME_DEBATE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(DebateListFragment.SECTION_NAME_DEBATE)).commit();
                break;
            case R.id.s_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getS().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getS().getID())).commit();
                break;
            case R.id.m_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getM().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getM().getID())).commit();
                break;
            case R.id.sd_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getSD().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getSD().getID())).commit();
                break;
            case R.id.mp_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getMP().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getMP().getID())).commit();
                break;
            case R.id.c_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getC().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getC().getID())).commit();
                break;
            case R.id.v_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getV().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getV().getID())).commit();
                break;
            case R.id.l_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getL().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getL().getID())).commit();
                break;
            case R.id.kd_nav:
                analyticsManager.setCurrentScreen(this, CurrentParties.getKD().getID());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(CurrentParties.getKD().getID())).commit();
                break;
            case R.id.search_nav:
                analyticsManager.setCurrentScreen(this, SearchListFragment.SECTION_NAME_SEARCH);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(SearchListFragment.SECTION_NAME_SEARCH)).commit();
                break;
            case R.id.about_nav:
                analyticsManager.setCurrentScreen(this, AboutFragment.SECTION_NAME_ABOUT);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(AboutFragment.SECTION_NAME_ABOUT)).commit();
                break;
            case R.id.feedback_nav:
                sendFeedback();
                break;
            case R.id.saved_docs:
                analyticsManager.setCurrentScreen(this, SavedDocumentsFragment.SECTION_NAME_SAVED);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(SavedDocumentsFragment.SECTION_NAME_SAVED)).commit();
                break;
            case R.id.twitter_nav:
                analyticsManager.setCurrentScreen(this, TwitterListFragment.SECTION_NAME_TWITTER);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragmentFactory.getFragment(TwitterListFragment.SECTION_NAME_TWITTER)).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "oscar@andell.eu", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tankar kring Riksdagskollen");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "\n\nSysteminformation:" +
                        "\nApp-version: " + BuildConfig.VERSION_NAME +
                        "\nSdk-version: " + Build.VERSION.SDK_INT);
        startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"));
    }




}
