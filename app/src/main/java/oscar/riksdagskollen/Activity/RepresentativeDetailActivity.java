package oscar.riksdagskollen.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.RepresentativeDocumentCallback;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Representative;
import oscar.riksdagskollen.Util.View.CircularNetworkImageView;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class RepresentativeDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout headerContainer;
    private Representative representative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RikdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_intressent_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View repHeaderLayout = findViewById(R.id.rep_header_layout);
        repHeaderLayout.setVisibility(View.VISIBLE);
        representative = getIntent().getParcelableExtra("representative");

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
        CircularNetworkImageView portrait = findViewById(R.id.representative_portrait);
        ImageView partyLogo = findViewById(R.id.representative_portrait_party_logo);

        partyLogo.setImageResource(MainActivity.getParty(representative.getParti().toLowerCase()).getDrawableLogo());
        portrait.setImageUrl(representative.getBild_url_192(), RikdagskollenApp.getInstance().getRequestManager().getmImageLoader());

        age.setText(representative.getAge());
        status.setText(representative.getDescriptiveRole());

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForRepresentative(
                representative.getIntressent_id(),
                1,
                new RepresentativeDocumentCallback() {

                    @Override
                    public void onDocumentsFetched(List<PartyDocument> documents, String numberOfHits) {
                        publishedDocuments.setText(numberOfHits);
                    }

                    @Override
                    public void onFail(VolleyError error) {
                        publishedDocuments.setText("0");
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
