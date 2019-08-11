package oscar.riksdagskollen.Fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.Util.Helper.CustomTabs;

/**
 * Created by oscar on 2018-09-28.
 */

public class RepresentativeBiographyFragment extends Fragment {

    Representative representative;

    public static RepresentativeBiographyFragment newInstance(Representative representative) {
        Bundle args = new Bundle();
        args.putParcelable("representative", representative);
        RepresentativeBiographyFragment newInstance = new RepresentativeBiographyFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.representative = getArguments().getParcelable("representative");
        return inflater.inflate(R.layout.fragment_representatvive_bio, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        createBioViews(view);
    }

    private void createBioViews(View view) {
        LinearLayout bioHolder = view.findViewById(R.id.bio_itemholder);
        ArrayList<String[]> bioList = representative.getBiography();
        for (int i = 0; i < bioList.size(); i++) {
            View bioItem = LayoutInflater.from(getContext()).inflate(R.layout.biography_item, null);
            TextView bioItemTitle = bioItem.findViewById(R.id.bioitem_title);
            bioItemTitle.setText(bioList.get(i)[0]);
            TextView bioItemText = bioItem.findViewById(R.id.bioitem_text);
            bioItemText.setText(bioList.get(i)[1].replace(". ", ".\n"));
            bioHolder.addView(bioItem);
        }

        //Create personal website link if exist
        final String website = representative.getWebsite();
        if (!website.equals("")) {
            View bioItem = LayoutInflater.from(getContext()).inflate(R.layout.biography_item, null);
            TextView bioItemTitle = bioItem.findViewById(R.id.bioitem_title);
            bioItemTitle.setText("Webbsida");
            TextView url = bioItem.findViewById(R.id.bioitem_text);
            SpannableString content = new SpannableString(website);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            url.setText(content);
            bioHolder.addView(bioItem);
            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabs.openTab(getContext(), website);
                }
            });
        }

        if (website.equals("") && bioList.isEmpty()) {
            View bioItem = LayoutInflater.from(getContext()).inflate(R.layout.biography_item, null);
            TextView bioItemTitle = bioItem.findViewById(R.id.bioitem_title);
            bioItemTitle.setText(R.string.no_content_string);
            TextView bioItemText = bioItem.findViewById(R.id.bioitem_text);
            bioItemText.setText("");
            bioHolder.addView(bioItem);
        }


        //hack for fixing weird problem with bottom text being cut off.
        TextView paddingView = new TextView(getContext());
        paddingView.setText("\n");
        bioHolder.addView(paddingView);
    }

}
