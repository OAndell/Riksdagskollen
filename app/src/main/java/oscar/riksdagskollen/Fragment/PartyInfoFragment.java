package oscar.riksdagskollen.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.Helper.WikiPartyInfoExtractor;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;

/**
 * Created by oscar on 2018-08-27.
 */

public class PartyInfoFragment extends Fragment {
    private Party party;
    private static final String PARTY_INFO_PREFERENCES = "party_info";
    private static final String SUMMARY_SUFFIX = "_summary";
    private static final String IDEOLOGY_SUFFIX = "_ideology";


    public static PartyInfoFragment newInstance(Party party){
        Bundle args = new Bundle();
        args.putParcelable("party",party);
        PartyInfoFragment newInstance = new PartyInfoFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.party = getArguments().getParcelable("party");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
        return inflater.inflate(R.layout.fragment_party_info,null);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.party = getArguments().getParcelable("party");

        final ViewGroup loadingView = view.findViewById(R.id.loading_view);

        ((ProgressBar) loadingView.findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, getContext()),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        //Set party logo
        ImageView partyLogoView = view.findViewById(R.id.party_logo);
        partyLogoView.setImageResource(party.getDrawableLogo());

        //Fill view with party leaders
        final FlexboxLayout leadersLayout = view.findViewById(R.id.leadersLayout);
        final RiksdagskollenApp app = RiksdagskollenApp.getInstance();
        final Fragment fragment = this;
        final TextView partyWikiInfo = view.findViewById(R.id.about_party_wiki);
        final TextView source = view.findViewById(R.id.source_tv);
        final TextView ideologyView = view.findViewById(R.id.ideology);

        partyWikiInfo.setText(getCachedWikiInfo(getSummaryKey()));
        ideologyView.setText(getCachedWikiInfo(getIdeologyKey()));


        app.getRequestManager().getDownloadString(party.getWikiUrl(), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                WikiPartyInfoExtractor partyInfoExtractor = new WikiPartyInfoExtractor(response);
                partyWikiInfo.setText(partyInfoExtractor.getPartySummary());
                source.setText("Källa: Wikipedia\n" + partyInfoExtractor.getLastUpdated());
                ideologyView.setText(partyInfoExtractor.getPartyIdeology());
                saveWikiResults(partyInfoExtractor);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        // Hack to fill the view, since flexboxlayput does not support min-height
        for (int i = 0; i < 4; i++) {
            View portraitView = LayoutInflater.from(getActivity()).inflate(R.layout.intressent_layout_big, null);
            leadersLayout.addView(portraitView);
        }


        app.getRiksdagenAPIManager().getPartyLeaders(party.getName(), new PartyLeadersCallback() {
            @Override
            public void onPersonFetched(final ArrayList<Representative> leaders) {
                leadersLayout.removeAllViews();
                for (int i =0; i < leaders.size(); i++) {
                    final Representative tmpRep = leaders.get(i);
                    if (getActivity() == null) break;

                    final View portraitView = LayoutInflater.from(getActivity()).inflate(R.layout.intressent_layout_big, null);
                    final ImageView portrait = portraitView.findViewById(R.id.intressent_portait);

                    leadersLayout.addView(portraitView);

                    app.getRiksdagenAPIManager().getRepresentative(tmpRep.getTilltalsnamn(), tmpRep.getEfternamn(), party.getID(), tmpRep.getSourceid(), new RepresentativeCallback() {
                        @Override
                        public void onPersonFetched(final Representative representative) {

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                portrait.setImageResource(R.drawable.ic_person);
                            }
                            if (fragment.getActivity() != null) {
                                Glide
                                        .with(fragment)
                                        .load(representative.getBild_url_192())
                                        .into(portrait);
                            }

                            portrait.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent repDetailsIntent = new Intent(getContext(), RepresentativeDetailActivity.class);
                                    repDetailsIntent.putExtra("representative", representative);
                                    startActivity(repDetailsIntent);
                                }
                            });
                            TextView nameTv = portraitView.findViewById(R.id.intressent_name);
                            nameTv.setText(representative.getTilltalsnamn() + " " + representative.getEfternamn() + "\n" + representative.getDescriptiveRole());
                        }

                        @Override
                        public void onFail(VolleyError error) {
                            portraitView.setVisibility(View.GONE);
                        }
                    });

                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        //ideology

        //Set party website
        TextView website = view.findViewById(R.id.website);
        SpannableString content = new SpannableString(party.getWebsite());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        website.setText(content);
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabs.openTab(getContext(), "https://" + party.getWebsite());
            }
        });


    }

    private String getCachedWikiInfo(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PARTY_INFO_PREFERENCES, 0);
        String info = preferences.getString(key, "");

        // Default to predefined ideology
        if (key.equals(getIdeologyKey()) && info.isEmpty()) {
            info = party.getIdeology();
        }
        return info;
    }

    private void saveWikiResults(WikiPartyInfoExtractor infoExtractor) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PARTY_INFO_PREFERENCES, 0);

        // Update stored information if needed
        if (!preferences.getString(getSummaryKey(), "").equals(infoExtractor.getPartySummary())) {
            preferences.edit().putString(getSummaryKey(), infoExtractor.getPartySummary()).apply();
        }

        if (!preferences.getString(getIdeologyKey(), "").equals(infoExtractor.getPartyIdeology())) {
            preferences.edit().putString(getIdeologyKey(), infoExtractor.getPartyIdeology()).apply();
        }


    }

    private String getSummaryKey() {
        return party.getID() + SUMMARY_SUFFIX;
    }

    private String getIdeologyKey() {
        return party.getID() + IDEOLOGY_SUFFIX;
    }


}
