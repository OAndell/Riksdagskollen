package oscar.riksdagskollen.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.ref.SoftReference;

import oscar.riksdagskollen.BuildConfig;
import oscar.riksdagskollen.Fragment.AboutFragment;
import oscar.riksdagskollen.Fragment.CurrentNewsListFragment;
import oscar.riksdagskollen.Fragment.DecisionsListFragment;
import oscar.riksdagskollen.Fragment.PartyFragment;
import oscar.riksdagskollen.Fragment.PartyListFragment;
import oscar.riksdagskollen.Fragment.ProtocolListFragment;
import oscar.riksdagskollen.Fragment.RepresentativeListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Manager.ThemeManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.Helper.AppBarStateChangeListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SoftReference<CurrentNewsListFragment> currentNewsListFragment;
    private SoftReference<ProtocolListFragment> protFragment;
    private SoftReference<DecisionsListFragment> decisionsFragment;
    private SoftReference<RepresentativeListFragment> repFragment;
    private SoftReference<VoteListFragment> voteListFragment;
    private SoftReference<AboutFragment> aboutFragment;
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
    private FirebaseAnalytics fireBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingLogo = findViewById(R.id.riksdagen_logo_collapsing);
        fireBase = FirebaseAnalytics.getInstance(this);

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
            // Apply theme
        } else {
            emptyToolbar = false;
            toggle.setDrawerIndicatorEnabled(true);
            invalidateOptionsMenu();
        }

    }


    private void handleOpenWithNotification() {
        Intent incoming = getIntent();
        if (incoming.hasExtra("document")) {
            Intent intent = new Intent(this, MotionActivity.class);
            intent.putExtra("document", incoming.getParcelableExtra("document"));
            startActivity(intent);
        } else if (incoming.hasExtra("section")) {
            switch (incoming.getStringExtra("section")) {
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
                case VoteListFragment.sectionName:
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.votes_nav));
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLauncherTransition() {
        emptyToolbar = true;
        toggle.setDrawerIndicatorEnabled(false);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingLogo.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setTitle(" ");

        Handler handler = new Handler();
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.equals(State.COLLAPSED)) {
                    collapsingToolbarLayout.setTitleEnabled(false);

                    try {
                        ((ViewGroup) collapsingLogo.getParent()).removeView(collapsingLogo);
                    } catch (NullPointerException e) {
                        //TODO FIGURE out why collapsingLogo.getParent() is null sometimes
                    }


                    emptyToolbar = false;
                    toggle.setDrawerIndicatorEnabled(true);
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onOffsetChange(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                float alpha = 255 - (255 * percentage);
                collapsingLogo.setImageAlpha((int) alpha);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(false, true);
                handleOpenWithNotification();
            }
        }, 800);
    }

    private void applyTheme() {
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        recreate();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Ugly hack to prevent News menu item to be checked forever
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);

        switch (id){
            case R.id.news_nav:
                fireBase.setCurrentScreen(this, "news", null);
                if (currentNewsListFragment == null || currentNewsListFragment.get() == null)
                    currentNewsListFragment = new SoftReference<>(CurrentNewsListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentNewsListFragment.get()).commit();
                break;
            case R.id.votes_nav:
                fireBase.setCurrentScreen(this, "votes", null);
                if (voteListFragment == null || voteListFragment.get() == null)
                    voteListFragment = new SoftReference<>(VoteListFragment.newInstance(null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, voteListFragment.get()).commit();
                break;
            case R.id.dec_nav:
                fireBase.setCurrentScreen(this, "decisions", null);
                if (decisionsFragment == null || decisionsFragment.get() == null)
                    decisionsFragment = new SoftReference<>(DecisionsListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, decisionsFragment.get()).commit();
                break;
            case R.id.rep_nav:
                fireBase.setCurrentScreen(this, "reps", null);
                if (repFragment == null || repFragment.get() == null)
                    repFragment = new SoftReference<>(RepresentativeListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, repFragment.get()).commit();
                break;
            case R.id.prot_nav:
                fireBase.setCurrentScreen(this, "protocol", null);
                if (protFragment == null || protFragment.get() == null)
                    protFragment = new SoftReference<>(ProtocolListFragment.newInstance());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, protFragment.get()).commit();
                break;
            case R.id.s_nav:
                fireBase.setCurrentScreen(this, "s", null);
                if (sPartyFragment == null || sPartyFragment.get() == null) {
                    sPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getS()));
                    PartyListFragment sPartyListFragment = PartyListFragment.newInstance(CurrentParties.getS());
                    sPartyFragment.get().setListFragment(sPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sPartyFragment.get()).commit();
                break;
            case R.id.m_nav:
                fireBase.setCurrentScreen(this, "m", null);
                if (mPartyFragment == null || mPartyFragment.get() == null) {
                    mPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getM()));
                    PartyListFragment mPartyListFragment = PartyListFragment.newInstance(CurrentParties.getM());
                    mPartyFragment.get().setListFragment(mPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPartyFragment.get()).commit();
                break;
            case R.id.sd_nav:
                fireBase.setCurrentScreen(this, "sd", null);
                if (sdPartyFragment == null || sdPartyFragment.get() == null) {
                    sdPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getSD()));
                    PartyListFragment sdPartyListFragment = PartyListFragment.newInstance(CurrentParties.getSD());
                    sdPartyFragment.get().setListFragment(sdPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sdPartyFragment.get()).commit();
                break;
            case R.id.mp_nav:
                fireBase.setCurrentScreen(this, "mp", null);
                if (mpPartyFragment == null || mpPartyFragment.get() == null) {
                    mpPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getMP()));
                    PartyListFragment mpPartyListFragment = PartyListFragment.newInstance(CurrentParties.getMP());
                    mpPartyFragment.get().setListFragment(mpPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mpPartyFragment.get()).commit();
                break;
            case R.id.c_nav:
                fireBase.setCurrentScreen(this, "c", null);
                if (cPartyFragment == null || cPartyFragment.get() == null) {
                    cPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getC()));
                    PartyListFragment cPartyListFragment = PartyListFragment.newInstance(CurrentParties.getC());
                    cPartyFragment.get().setListFragment(cPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cPartyFragment.get()).commit();
                break;
            case R.id.v_nav:
                fireBase.setCurrentScreen(this, "v", null);
                if (vPartyFragment == null || vPartyFragment.get() == null) {
                    vPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getV()));
                    PartyListFragment vPartyListFragment = PartyListFragment.newInstance(CurrentParties.getV());
                    vPartyFragment.get().setListFragment(vPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vPartyFragment.get()).commit();
                break;
            case R.id.l_nav:
                fireBase.setCurrentScreen(this, "l", null);
                if (lPartyFragment == null || lPartyFragment.get() == null) {
                    lPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getL()));
                    PartyListFragment lPartyListFragment = PartyListFragment.newInstance(CurrentParties.getL());
                    lPartyFragment.get().setListFragment(lPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lPartyFragment.get()).commit();
                break;
            case R.id.kd_nav:
                fireBase.setCurrentScreen(this, "kd", null);
                if (kdPartyFragment == null || kdPartyFragment.get() == null) {
                    kdPartyFragment = new SoftReference<>(PartyFragment.newInstance(CurrentParties.getKD()));
                    PartyListFragment kdPartyListFragment = PartyListFragment.newInstance(CurrentParties.getKD());
                    kdPartyFragment.get().setListFragment(kdPartyListFragment);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, kdPartyFragment.get()).commit();
                break;
            case R.id.about_nav:
                fireBase.setCurrentScreen(this, "about", null);
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
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
