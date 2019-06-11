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

import com.android.volley.VolleyError;

import java.lang.ref.SoftReference;
import java.util.List;

import oscar.riksdagskollen.Fragment.AboutFragment;
import oscar.riksdagskollen.Fragment.CurrentNewsListFragment;
import oscar.riksdagskollen.Fragment.DebateListFragment;
import oscar.riksdagskollen.Fragment.DecisionsListFragment;
import oscar.riksdagskollen.Fragment.PartyFragment;
import oscar.riksdagskollen.Fragment.PartyListFragment;
import oscar.riksdagskollen.Fragment.ProtocolListFragment;
import oscar.riksdagskollen.Fragment.RepresentativeListFragment;
import oscar.riksdagskollen.Fragment.SavedDocumentsFragment;
import oscar.riksdagskollen.Fragment.SearchListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Manager.AnalyticsManager;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.Manager.TwitterAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.Helper.AppBarStateChangeListener;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.Helper.NotificationHelper;
import oscar.riksdagskollen.Util.Helper.TwitterUserFactory;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.JSONModel.Twitter.TwitterUser;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

import static oscar.riksdagskollen.Util.Helper.NotificationHelper.NEWS_ITEM_URL_KEY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SoftReference<CurrentNewsListFragment> currentNewsListFragment;
    private SoftReference<ProtocolListFragment> protFragment;
    private SoftReference<DecisionsListFragment> decisionsFragment;
    private SoftReference<RepresentativeListFragment> repFragment;
    private SoftReference<VoteListFragment> voteListFragment;
    private SoftReference<DebateListFragment> debateFragment;
    private SoftReference<AboutFragment> aboutFragment;
    private SoftReference<SavedDocumentsFragment> savedDocumentsFragment;
    private SoftReference<SearchListFragment> searchFragment;
    private SoftReference<PartyFragment> sPartyFragment;
    private SoftReference<PartyFragment> mPartyFragment;
    private SoftReference<PartyFragment> sdPartyFragment;
    private SoftReference<PartyFragment> mpPartyFragment;
    private SoftReference<PartyFragment> cPartyFragment;
    private SoftReference<PartyFragment> vPartyFragment;
    private SoftReference<PartyFragment> lPartyFragment;
    private SoftReference<PartyFragment> kdPartyFragment;

    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private boolean emptyToolbar = false;
    private ImageView collapsingLogo;
    private AnalyticsManager analyticsManager;


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

        //TODO TWITTER DEMO
        System.out.println("TWITTER DEMO");
        TwitterAPIManager twitterAPIManager = RiksdagskollenApp.getInstance().getTwitterAPIManager();
        TwitterUser vTwitter = TwitterUserFactory.getUser(CurrentParties.getParty("v"));
        twitterAPIManager.getTweets(vTwitter.getTwitterScreenName(), new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                for (int i = 0; i < tweets.size(); i++) {
                    System.out.println(tweets.get(i).getText());
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
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
                analyticsManager.setCurrentScreen(this, "news");
                if (currentNewsListFragment == null || currentNewsListFragment.get() == null)
                    currentNewsListFragment = new SoftReference<>(CurrentNewsListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentNewsListFragment.get()).commit();
                break;
            case R.id.votes_nav:
                analyticsManager.setCurrentScreen(this, "votes");
                if (voteListFragment == null || voteListFragment.get() == null)
                    voteListFragment = new SoftReference<>(VoteListFragment.newInstance(null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, voteListFragment.get()).commit();
                break;
            case R.id.dec_nav:
                analyticsManager.setCurrentScreen(this, "decisions");
                if (decisionsFragment == null || decisionsFragment.get() == null)
                    decisionsFragment = new SoftReference<>(DecisionsListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, decisionsFragment.get()).commit();
                break;
            case R.id.rep_nav:
                analyticsManager.setCurrentScreen(this, "reps");
                if (repFragment == null || repFragment.get() == null)
                    repFragment = new SoftReference<>(RepresentativeListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, repFragment.get()).commit();
                break;
            case R.id.prot_nav:
                analyticsManager.setCurrentScreen(this, "protocol");
                if (protFragment == null || protFragment.get() == null)
                    protFragment = new SoftReference<>(ProtocolListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, protFragment.get()).commit();
                break;
            case R.id.debate_nav:
                analyticsManager.setCurrentScreen(this, "debateList");
                if (debateFragment == null || debateFragment.get() == null)
                    debateFragment = new SoftReference<>(DebateListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, debateFragment.get()).commit();
                break;
            case R.id.s_nav:
                analyticsManager.setCurrentScreen(this, "s");
                if (sPartyFragment == null || sPartyFragment.get() == null) {
                    sPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getS()));
                    PartyListFragment sPartyListFragment = PartyListFragment.newInstance(CurrentParties.getS());
                    sPartyFragment.get().setListFragment(sPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sPartyFragment.get()).commit();
                break;
            case R.id.m_nav:
                analyticsManager.setCurrentScreen(this, "m");
                if (mPartyFragment == null || mPartyFragment.get() == null) {
                    mPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getM()));
                    PartyListFragment mPartyListFragment = PartyListFragment.newInstance(CurrentParties.getM());
                    mPartyFragment.get().setListFragment(mPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPartyFragment.get()).commit();
                break;
            case R.id.sd_nav:
                analyticsManager.setCurrentScreen(this, "sd");
                if (sdPartyFragment == null || sdPartyFragment.get() == null) {
                    sdPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getSD()));
                    PartyListFragment sdPartyListFragment = PartyListFragment.newInstance(CurrentParties.getSD());
                    sdPartyFragment.get().setListFragment(sdPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sdPartyFragment.get()).commit();
                break;
            case R.id.mp_nav:
                analyticsManager.setCurrentScreen(this, "mp");
                if (mpPartyFragment == null || mpPartyFragment.get() == null) {
                    mpPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getMP()));
                    PartyListFragment mpPartyListFragment = PartyListFragment.newInstance(CurrentParties.getMP());
                    mpPartyFragment.get().setListFragment(mpPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mpPartyFragment.get()).commit();
                break;
            case R.id.c_nav:
                analyticsManager.setCurrentScreen(this, "c");
                if (cPartyFragment == null || cPartyFragment.get() == null) {
                    cPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getC()));
                    PartyListFragment cPartyListFragment = PartyListFragment.newInstance(CurrentParties.getC());
                    cPartyFragment.get().setListFragment(cPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cPartyFragment.get()).commit();
                break;
            case R.id.v_nav:
                analyticsManager.setCurrentScreen(this, "v");
                if (vPartyFragment == null || vPartyFragment.get() == null) {
                    vPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getV()));
                    PartyListFragment vPartyListFragment = PartyListFragment.newInstance(CurrentParties.getV());
                    vPartyFragment.get().setListFragment(vPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vPartyFragment.get()).commit();
                break;
            case R.id.l_nav:
                analyticsManager.setCurrentScreen(this, "l");
                if (lPartyFragment == null || lPartyFragment.get() == null) {
                    lPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getL()));
                    PartyListFragment lPartyListFragment = PartyListFragment.newInstance(CurrentParties.getL());
                    lPartyFragment.get().setListFragment(lPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lPartyFragment.get()).commit();
                break;
            case R.id.kd_nav:
                analyticsManager.setCurrentScreen(this, "kd");
                if (kdPartyFragment == null || kdPartyFragment.get() == null) {
                    kdPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getKD()));
                    PartyListFragment kdPartyListFragment = PartyListFragment.newInstance(CurrentParties.getKD());
                    kdPartyFragment.get().setListFragment(kdPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, kdPartyFragment.get()).commit();
                break;
            case R.id.search_nav:
                analyticsManager.setCurrentScreen(this, "search");
                if (searchFragment == null || searchFragment.get() == null)
                    searchFragment = new SoftReference<>(SearchListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment.get()).commit();
                break;
            case R.id.about_nav:
                analyticsManager.setCurrentScreen(this, "about");
                if (aboutFragment == null || aboutFragment.get() == null)
                    aboutFragment = new SoftReference<>(AboutFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, aboutFragment.get()).commit();
                break;
            case R.id.feedback_nav:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "oscar@andell.eu", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tankar kring Riksdagskollen");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "\n\nSysteminformation:" +
                                "\nApp-version: " + BuildConfig.VERSION_NAME +
                                "\nSdk-version: " + Build.VERSION.SDK_INT);
                startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"));
                break;
            case R.id.saved_docs:
                analyticsManager.setCurrentScreen(this, "saved");

                if (savedDocumentsFragment == null || savedDocumentsFragment.get() == null) {
                    savedDocumentsFragment = new SoftReference<>(SavedDocumentsFragment.newInstance());
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, savedDocumentsFragment.get()).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
