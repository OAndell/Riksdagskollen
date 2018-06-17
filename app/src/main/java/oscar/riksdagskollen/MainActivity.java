package oscar.riksdagskollen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import oscar.riksdagskollen.Activities.DocumentReaderActivity;
import oscar.riksdagskollen.Fragments.CurrentNewsListFragment;
import oscar.riksdagskollen.Activities.MotionActivity;
import oscar.riksdagskollen.Fragments.DecisionsListFragment;
import oscar.riksdagskollen.Fragments.PartyListFragment;
import oscar.riksdagskollen.Fragments.ProtocolListFragment;
import oscar.riksdagskollen.Utilities.JSONModels.Party;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CurrentNewsListFragment currentNewsListFragment;
    ProtocolListFragment protFragment;
    DecisionsListFragment decisionsFragment;
    PartyListFragment sPartyFragment;
    PartyListFragment mPartyFragment;
    PartyListFragment sdPartyFragment;
    PartyListFragment mpPartyFragment;
    PartyListFragment cPartyFragment;
    PartyListFragment vPartyFragment;
    PartyListFragment lPartyFragment;
    PartyListFragment kdPartyFragment;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        initPartyFragments();
        initMenuOptions();

        // Mark News-fragment as selected at startup
        onNavigationItemSelected(navigationView.getMenu().getItem(0).getSubMenu().getItem(0));
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
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
                break;
            case R.id.dec_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,decisionsFragment).commit();
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

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initMenuOptions(){
        currentNewsListFragment = CurrentNewsListFragment.newInstance();
        protFragment = ProtocolListFragment.newInstance();

        decisionsFragment = DecisionsListFragment.newInstance();
    }

    // Create all of the PartyFragments with new Party objects
    private void initPartyFragments(){
        mPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_m),"m"));
        sPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_s),"s"));
        sdPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_sd),"sd"));
        kdPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_kd),"kd"));
        vPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_v),"v"));
        cPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_c),"c"));
        mpPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_mp),"mp"));
        lPartyFragment = PartyListFragment.newIntance(new Party(getString(R.string.party_l),"l"));
    }
}
