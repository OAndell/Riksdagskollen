package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.Representative;

/**
 * Created by oscar on 2018-08-27.
 */

public class PartyInfoFragment extends Fragment {
    private Party party;

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
        final FlexboxLayout leadersLayout = view.findViewById(R.id.leadersLayout);

        ImageView partyLogoView = view.findViewById(R.id.party_logo);
        partyLogoView.setImageResource(party.getDrawableLogo());
        final RikdagskollenApp app = RikdagskollenApp.getInstance();
        app.getRiksdagenAPIManager().getPartyLeaders(party.getName(), new PartyLeadersCallback() {
            @Override
            public void onPersonFetched(ArrayList<Representative> leaders) {
                for (int i =0; i < leaders.size(); i++) {
                    View portraitView = LayoutInflater.from(getContext()).inflate(R.layout.intressent_layout_big,null);

                    NetworkImageView portrait = portraitView.findViewById(R.id.intressent_portait);
                    portrait.setDefaultImageResId(R.mipmap.ic_default_person);
                    portrait.setImageUrl(leaders.get(i).getBild_url_192(),app.getRequestManager().getmImageLoader());
                    TextView nameTv = portraitView.findViewById(R.id.intressent_name);
                    nameTv.setText(leaders.get(i).getTilltalsnamn()+" "+leaders.get(i).getEfternamn() +"\n" + leaders.get(i).getRoll_kod());
                    leadersLayout.addView(portraitView);
                    System.out.println(leaders.get(i).getTilltalsnamn()+ " " + leaders.get(i).getEfternamn());
                    System.out.println("   " +leaders.get(i).getRoll_kod());
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }
}
