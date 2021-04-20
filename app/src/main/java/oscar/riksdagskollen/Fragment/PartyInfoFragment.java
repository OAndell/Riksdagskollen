package oscar.riksdagskollen.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import oscar.riksdagskollen.Activity.RepresentativeDetailActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.Helper.WikiPartyInfoExtractor;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels.PartyData;
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels.PollingData;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks;
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
    private WikiPartyInfoExtractor wikiResponse = null;
    private ArrayList<Representative> leaders = new ArrayList<>();
    private FlexboxLayout leadersLayout;
    private final Fragment fragment = this;
    private ViewGroup loadingView;
    private final RiksdagskollenApp app = RiksdagskollenApp.getInstance();

    private PublishSubject<PollingData> pollingData$ = PublishSubject.create();
    private PublishSubject<PartyData> partyData$ = PublishSubject.create();


    public static PartyInfoFragment newInstance(Party party) {
        Bundle args = new Bundle();
        args.putParcelable("party", party);
        PartyInfoFragment newInstance = new PartyInfoFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.party = getArguments().getParcelable("party");
        return inflater.inflate(R.layout.fragment_party_info, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingView = view.findViewById(R.id.loading_view);

        //Set party logo
        ImageView partyLogoView = view.findViewById(R.id.party_logo);
        partyLogoView.setImageResource(party.getDrawableLogo());

        final TextView partyWikiInfo = view.findViewById(R.id.about_party_wiki);
        final TextView source = view.findViewById(R.id.source_tv);
        final TextView ideologyView = view.findViewById(R.id.ideology);
        final TextView pollingNumber = view.findViewById(R.id.polling_number);
        final TextView pollingDelta = view.findViewById(R.id.polling_delta);
        final TextView electionResults = view.findViewById(R.id.election_result);
        final ImageView indicatorArrow = view.findViewById(R.id.indicator_arrow);

        leadersLayout = view.findViewById(R.id.leadersLayout);

        partyWikiInfo.setText(getCachedWikiInfo(getSummaryKey()));
        ideologyView.setText(getCachedWikiInfo(getIdeologyKey()));

        if (wikiResponse == null) {
            app.getRequestManager().getDownloadString(party.getWikiUrl(), new StringRequestCallback() {
                @Override
                public void onResponse(String response) {
                    wikiResponse = new WikiPartyInfoExtractor(response);
                    partyWikiInfo.setText(wikiResponse.getPartySummary());
                    source.setText("Källa: Wikipedia\n" + wikiResponse.getLastUpdated());
                    ideologyView.setText(wikiResponse.getPartyIdeology());
                    saveWikiResults(wikiResponse);
                }

                @Override
                public void onFail(VolleyError error) {

                }
            });
        } else {
            partyWikiInfo.setText(wikiResponse.getPartySummary());
            source.setText("Källa: Wikipedia\n" + wikiResponse.getLastUpdated());
            ideologyView.setText(wikiResponse.getPartyIdeology());
        }


        // Hack to fill the view, since flexboxlayput does not support min-height

        for (int i = 0; i < 5; i++) {
            View portraitView = LayoutInflater.from(getActivity()).inflate(R.layout.intressent_layout_big, leadersLayout, false);
            leadersLayout.addView(portraitView);
        }

        if (this.leaders.isEmpty()) {
            app.getRiksdagenAPIManager().getPartyLeaders(party.getName(), new PartyLeadersCallback() {
                @Override
                public void onPersonFetched(final ArrayList<Representative> downloadedLeaders) {
                    leaders.clear();
                    leaders.addAll(downloadedLeaders);
                    setupLeaderView();
                }

                @Override
                public void onFail(VolleyError error) {

                }
            });
        } else {
            setupLeaderView();
        }


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

        app.getRiksdagskollenAPIManager().getPartyData(party.getID(), new RKAPICallbacks.PartyDataCallback() {
            @Override
            public void onFetched(PartyData data) {
                partyData$.onNext(data);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        //Riksdagskollen API POC
        app.getRiksdagskollenAPIManager().getPollingDataForParty(party.getID(), new RKAPICallbacks.PollingDataCallback() {
            @Override
            public void onFetched(PollingData data) {
                pollingData$.onNext(data);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        Observable.combineLatest(pollingData$, partyData$, Pair::new).subscribe((pair) -> {
            PollingData pollData = pair.first;
            PartyData partyData = pair.second;
            String lastData = pollData.getData().get(0).getPercent();
            String previousData = pollData.getData().get(1).getPercent();
            pollingNumber.setText(lastData);
            try {
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                double last = format.parse(lastData.replace(",", ".")).doubleValue();
                double prev = format.parse(previousData.replace(",", ".")).doubleValue();
                double delta = last - prev;
                DecimalFormat f = new DecimalFormat("0.00");

                if (delta > 0) {
                    pollingDelta.setText("+" + f.format(delta));
                    pollingDelta.setTextColor(getResources().getColor(R.color.yesVoteColor));

                } else {
                    pollingDelta.setText(f.format(delta));
                    pollingDelta.setTextColor(getResources().getColor(R.color.noVoteColor));
                }
                electionResults.setText(partyData.getElectionResult());
                double electionResult = format.parse(partyData.getElectionResult().replace(",", ".")).doubleValue();
                if (electionResult > last) {
                    indicatorArrow.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    indicatorArrow.setColorFilter(getResources().getColor(R.color.noVoteColor));
                } else {
                    indicatorArrow.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    indicatorArrow.setColorFilter(getResources().getColor(R.color.yesVoteColor));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


    }

    private void setupLeaderView() {
        leadersLayout.removeAllViews();
        for (int i = 0; i < leaders.size(); i++) {
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

                    portrait.setOnClickListener(view -> {
                        Intent repDetailsIntent = new Intent(getContext(), RepresentativeDetailActivity.class);
                        repDetailsIntent.putExtra("representative", representative);
                        startActivity(repDetailsIntent);
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

    private String getCachedWikiInfo(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PARTY_INFO_PREFERENCES, 0);
        String info = preferences.getString(key, "");

        // Default to predefined ideology
        if (key.equals(getIdeologyKey()) && info.isEmpty()) {
            info = party.getIdeology();
        }
        return info;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("dummy", 1);
        super.onSaveInstanceState(outState);
    }

    private void saveWikiResults(WikiPartyInfoExtractor infoExtractor) {

        Context context = this.getActivity();

        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(PARTY_INFO_PREFERENCES, 0);
            // Update stored information if needed
            if (!preferences.getString(getSummaryKey(), "").equals(infoExtractor.getPartySummary())) {
                preferences.edit().putString(getSummaryKey(), infoExtractor.getPartySummary()).apply();
            }

            if (!preferences.getString(getIdeologyKey(), "").equals(infoExtractor.getPartyIdeology())) {
                preferences.edit().putString(getIdeologyKey(), infoExtractor.getPartyIdeology()).apply();
            }
        }


    }

    private String getSummaryKey() {
        return party.getID() + SUMMARY_SUFFIX;
    }

    private String getIdeologyKey() {
        return party.getID() + IDEOLOGY_SUFFIX;
    }


}
