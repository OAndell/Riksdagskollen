package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oscar.riksdagskollen.R;

/**
 * Created by oscar on 2018-09-23.
 */

public class PartyRepresentativeFragment extends Fragment {

    public static PartyRepresentativeFragment newInstance(){
        PartyRepresentativeFragment newInstance = new PartyRepresentativeFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_list,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }
}