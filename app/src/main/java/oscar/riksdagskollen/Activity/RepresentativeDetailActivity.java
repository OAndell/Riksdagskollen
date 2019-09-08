package oscar.riksdagskollen.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Fragment.RepresentativeBiographyFragment;
import oscar.riksdagskollen.Fragment.RepresentativeFeedFragment;
import oscar.riksdagskollen.Fragment.RepresentativeTabFragment;
import oscar.riksdagskollen.Manager.AnalyticsManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.RepresentativeVoteStatistics;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteStatisticsCallback;
import oscar.riksdagskollen.Util.View.CircularImageView;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class RepresentativeDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout headerContainer;
    private Representative representative;
    private RepresentativeFeedFragment feedFragment;

    private RepresentativeTabFragment tabFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_intressent_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final View repHeaderLayout = findViewById(R.id.rep_header_layout);
        repHeaderLayout.setVisibility(View.VISIBLE);
        representative = getIntent().getParcelableExtra("representative");
        // Old representative
        if (representative.getParti() == null || representative.getIntressent_id() == null) {
            Toast.makeText(this, "Kunde inte hämta ledamot", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        AnalyticsManager.getInstance().setCurrentScreen(this, "Representative: " + representative.getIntressent_id());

        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setTitle(representative.getTilltalsnamn() + " " + representative.getEfternamn());

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                float alpha = 1 - percentage;
                repHeaderLayout.setAlpha(alpha);
            }
        });

        TextView status = findViewById(R.id.representative_detail_status);
        TextView age = findViewById(R.id.representative_age);
        final TextView publishedDocuments = findViewById(R.id.representative_published_documents);
        CircularImageView portrait = findViewById(R.id.representative_portrait);
        ImageView partyLogo = findViewById(R.id.representative_portrait_party_logo);


        partyLogo.setImageResource(CurrentParties.getParty(representative.getParti().toLowerCase()).getDrawableLogo());


        Glide
                .with(this)
                .load(representative.getBild_url_192())
                .into(portrait);
        age.setText(representative.getAge());
        status.setText(representative.getDescriptiveRole());
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForRepresentative(
                representative.getIntressent_id(),
                1,
                new RepresentativeDocumentCallback() {

                    @Override
                    public void onDocumentsFetched(List<PartyDocument> documents, String numberOfHits) {
                        ArrayList<PartyDocument> firstPage = new ArrayList<>(documents);
                        publishedDocuments.setText(numberOfHits);
                        tabFragment = RepresentativeTabFragment.newInstance();
                        feedFragment = RepresentativeFeedFragment.newInstance(representative.getIntressent_id(), firstPage);
                        tabFragment.addTab(feedFragment, getString(R.string.rep_feed_tab_name));
                        tabFragment.addTab(RepresentativeBiographyFragment.newInstance(representative), "Om " + representative.getTilltalsnamn());
                        getSupportFragmentManager().beginTransaction().replace(R.id.rep_fragment_container, tabFragment).commitAllowingStateLoss();
                    }

                    @Override
                    public void onFail(VolleyError error) {
                        publishedDocuments.setText("0");
                        getSupportFragmentManager().beginTransaction().replace(R.id.rep_fragment_container, RepresentativeBiographyFragment.newInstance(representative)).commitAllowingStateLoss();
                    }
                });
        getVoteAbsence();
    }


    private void getVoteAbsence() {
        final TextView attendance = findViewById(R.id.representative_attendance);
        final TextView attendance_desc = findViewById(R.id.representative_attendance_subtext);
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getVoteStatisticsForRepresentative(representative.getIntressent_id(), new VoteStatisticsCallback() {
            @Override
            public void onStatisticsFetched(RepresentativeVoteStatistics stats) {
                attendance.setVisibility(View.VISIBLE);
                attendance_desc.setVisibility(View.VISIBLE);
                attendance.setText(stats.getAttendancePercent() + "%");
            }

            @Override
            public void onFail(VolleyError error) {


            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
