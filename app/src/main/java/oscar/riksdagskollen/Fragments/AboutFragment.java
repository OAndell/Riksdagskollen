package oscar.riksdagskollen.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oscar.riksdagskollen.R;

/**
 * Created by oscar on 2018-06-26.
 */

public class AboutFragment extends Fragment {
    public static AboutFragment newInstance(){
        AboutFragment newInstance = new AboutFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_title);
        return inflater.inflate(R.layout.activity_about,null);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView gitHubLink = view.findViewById(R.id.github_link);
        SpannableString content = new SpannableString(getResources().getString(R.string.github));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        gitHubLink.setText(content);
        gitHubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.github)));
                startActivity(browserIntent);
            }
        });

    }
}
