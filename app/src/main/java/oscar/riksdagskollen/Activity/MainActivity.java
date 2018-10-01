package oscar.riksdagskollen.Activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Window;
import android.widget.ImageView;

import java.util.Collection;
import java.util.HashMap;

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
import oscar.riksdagskollen.Util.Helper.AppBarStateChangeListener;
import oscar.riksdagskollen.Util.Helper.DeviceUuidFactory;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static HashMap<String, Party> parties;
    private CurrentNewsListFragment currentNewsListFragment;
    private ProtocolListFragment protFragment;
    private DecisionsListFragment decisionsFragment;
    private RepresentativeListFragment repFragment;
    private VoteListFragment voteListFragment;
    private AboutFragment aboutFragment;
    private PartyFragment sPartyFragment;
    private PartyFragment mPartyFragment;
    private PartyFragment sdPartyFragment;
    private PartyFragment mpPartyFragment;
    private PartyFragment cPartyFragment;
    private PartyFragment vPartyFragment;
    private PartyFragment lPartyFragment;
    private PartyFragment kdPartyFragment;
    private PartyListFragment sPartyListFragment;
    private PartyListFragment mPartyListFragment;
    private PartyListFragment sdPartyListFragment;
    private PartyListFragment mpPartyListFragment;
    private PartyListFragment cPartyListFragment;
    private PartyListFragment vPartyListFragment;
    private PartyListFragment lPartyListFragment;
    private PartyListFragment kdPartyListFragment;
    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private boolean emptyToolbar = false;
    private ImageView collapsingLogo;

    public static Party getParty(String id) {
        return parties.get(id);
    }

    public static Collection<Party> getParties() {
        return parties.values();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_main);

        DeviceUuidFactory.getDeviceUuid(this);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingLogo = findViewById(R.id.riksdagen_logo_collapsing);

        setSupportActionBar(toolbar);

        parties = new HashMap<>();

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
        initPartyFragments();
        initMenuOptions();

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

        //Open app with notification
        if (getIntent().hasExtra("document")) {
            handleOpenWithNotification((PartyDocument) getIntent().getParcelableExtra("document"));
        }
    }


    private void handleOpenWithNotification(PartyDocument document) {
        Intent intent = new Intent(this, MotionActivity.class);
        intent.putExtra("document", document);
        startActivity(intent);
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
                    collapsingLogo.setVisibility(View.GONE);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,currentNewsListFragment).commit();
                break;
            case R.id.votes_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,voteListFragment).commit();
                break;
            case R.id.dec_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,decisionsFragment).commit();
                break;
            case R.id.rep_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, repFragment).commit();
                break;
            case R.id.prot_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,protFragment).commit();
                break;
            case R.id.s_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,sPartyFragment).commit();
                break;
            case R.id.m_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mPartyFragment).commit();
                break;
            case R.id.sd_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,sdPartyFragment).commit();
                break;
            case R.id.mp_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mpPartyFragment).commit();
                break;
            case R.id.c_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,cPartyFragment).commit();
                break;
            case R.id.v_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,vPartyFragment).commit();
                break;
            case R.id.l_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,lPartyFragment).commit();
                break;
            case R.id.kd_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,kdPartyFragment).commit();
                break;
            case R.id.about_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,aboutFragment).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initMenuOptions(){
        currentNewsListFragment = CurrentNewsListFragment.newInstance();
        protFragment = ProtocolListFragment.newInstance();
        voteListFragment = VoteListFragment.newInstance(null);
        decisionsFragment = DecisionsListFragment.newInstance();
        repFragment = RepresentativeListFragment.newInstance();
        aboutFragment = AboutFragment.newInstance();
    }

    // Create all of the PartyFragments with new Party objects
    private void initPartyFragments(){
        Party mParty = new Party(
                getString(R.string.party_m),
                "m",R.drawable.mlogo,
                getString(R.string.m_website),
                getString(R.string.m_ideology));
        mPartyListFragment = PartyListFragment.newInstance(mParty);
        mPartyFragment = PartyFragment.newInstance(mParty);
        mPartyFragment.setListFragment(mPartyListFragment);


        Party sParty = new Party(
                getString(R.string.party_s),
                "s",R.drawable.slogo,
                getString(R.string.s_website),
                getString(R.string.s_ideology));
        sPartyListFragment = PartyListFragment.newInstance(sParty);
        sPartyFragment = PartyFragment.newInstance(sParty);
        sPartyFragment.setListFragment(sPartyListFragment);

        Party sdParty = new Party(
                getString(R.string.party_sd),
                "sd",R.drawable.sdlogo,
                getString(R.string.sd_website),
                getString(R.string.sd_ideology));
        sdPartyListFragment = PartyListFragment.newInstance(sdParty);
        sdPartyFragment = PartyFragment.newInstance(sdParty);
        sdPartyFragment.setListFragment(sdPartyListFragment);

        Party kdParty = new Party(
                getString(R.string.party_kd),
                "kd",R.drawable.kdlogo,
                getString(R.string.kd_website),
                getString(R.string.kd_ideology));
        kdPartyListFragment = PartyListFragment.newInstance(kdParty);
        kdPartyFragment = PartyFragment.newInstance(kdParty);
        kdPartyFragment.setListFragment(kdPartyListFragment);

        Party vParty = new Party(
                getString(R.string.party_v),
                "v",R.drawable.vlogo,
                getString(R.string.v_website),
                getString(R.string.v_ideology));
        vPartyListFragment = PartyListFragment.newInstance(vParty);
        vPartyFragment = PartyFragment.newInstance(vParty);
        vPartyFragment.setListFragment(vPartyListFragment);

        Party cParty = new Party(
                getString(R.string.party_c),
                "c",R.drawable.clogo,
                getString(R.string.c_website),
                getString(R.string.c_ideology));
        cPartyListFragment = PartyListFragment.newInstance(cParty);
        cPartyFragment = PartyFragment.newInstance(cParty);
        cPartyFragment.setListFragment(cPartyListFragment);

        Party mpParty = new Party(
                getString(R.string.party_mp),
                "mp",R.drawable.mplogo,
                getString(R.string.mp_website),
                getString(R.string.mp_ideology));
        mpPartyListFragment = PartyListFragment.newInstance(mpParty);
        mpPartyFragment = PartyFragment.newInstance(mpParty);
        mpPartyFragment.setListFragment(mpPartyListFragment);

        Party lParty = new Party(
                getString(R.string.party_l),
                "l",R.drawable.llogo,
                getString(R.string.l_website),
                getString(R.string.l_ideology));
        lPartyListFragment = PartyListFragment.newInstance(lParty);
        lPartyFragment = PartyFragment.newInstance(lParty);
        lPartyFragment.setListFragment(lPartyListFragment);

        parties.put(mParty.getID(), mParty);
        parties.put(sParty.getID(), sParty);
        parties.put(sdParty.getID(), sdParty);
        parties.put(kdParty.getID(), kdParty);
        parties.put(vParty.getID(), vParty);
        parties.put(cParty.getID(), cParty);
        parties.put(mpParty.getID(), mpParty);
        parties.put(lParty.getID(), lParty);
    }
}
