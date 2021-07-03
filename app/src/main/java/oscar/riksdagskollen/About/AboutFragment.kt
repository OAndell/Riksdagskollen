package oscar.riksdagskollen.About;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.Library;
import com.franmontiel.attributionpresenter.entities.License;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CustomTabs;

/**
 * Created by oscar on 2018-06-26.
 */

public class AboutFragment extends Fragment {

    public static final String SECTION_NAME_ABOUT = "about";


    private Button licenceButton;
    public static AboutFragment newInstance(){
        AboutFragment newInstance = new AboutFragment();
        return newInstance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_title);
        return inflater.inflate(R.layout.fragment_about,null);
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
                CustomTabs.openTab(getContext(), getResources().getString(R.string.github));
            }
        });

        TextView twitterLink = view.findViewById(R.id.twitter_link);
        content = new SpannableString(twitterLink.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        twitterLink.setText(content);
        twitterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabs.openTab(getContext(), "https://twitter.com/Riksdagskollen");
            }
        });
        
        final AttributionPresenter attributionPresenter = new AttributionPresenter.Builder(getContext())
                .addAttributions(
                        Library.GSON)
                .addAttributions(
                        new Attribution.Builder("jsoup: Java HTML Parser")
                                .addCopyrightNotice("Copyright Jonathan Hedley 2009 - 2017")
                                .addLicense(License.MIT)
                                .setWebsite("https://jsoup.org/")
                                .build(),
                        new Attribution.Builder("Volley")
                                .addLicense(License.APACHE)
                                .addCopyrightNotice("Copyright 2011 Google")
                                .setWebsite("https://github.com/google/volley")
                                .build(),
                        new Attribution.Builder("FlexboxLayout")
                                .addCopyrightNotice("Copyright 2016 Google")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/google/flexbox-layout")
                                .build(),
                        new Attribution.Builder("MPAndroidChart")
                                .addCopyrightNotice("Copyright 2018 Philipp Jahoda")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/PhilJay/MPAndroidChart")
                                .build(),
                        new Attribution.Builder("AttributionPresenter")
                                .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                                .build(),
                        new Attribution.Builder("Android-Job")
                                .addCopyrightNotice("Copyright (c) 2007-2017 by Evernote Corporation, All rights reserved.")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/evernote/android-job")
                                .build()
                )
                .build();


        licenceButton = view.findViewById(R.id.show_licenses_button);
        licenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attributionPresenter.showDialog("Programvara som hjälpt till att skapa Riksdagskollen");
            }
        });
        try {
            Context context = RiksdagskollenApp.getInstance();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            TextView versionView = view.findViewById(R.id.version);
            versionView.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
