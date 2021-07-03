package oscar.riksdagskollen.About

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.franmontiel.attributionpresenter.AttributionPresenter
import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.Library
import com.franmontiel.attributionpresenter.entities.License
import kotlinx.android.synthetic.main.fragment_about.*
import oscar.riksdagskollen.R
import oscar.riksdagskollen.RiksdagskollenApp
import oscar.riksdagskollen.Util.Helper.CustomTabs
import oscar.riksdagskollen.Util.Helper.createDeveloperEmailIntent

/**
 * Created by oscar on 2018-06-26.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle(R.string.about_title)
        return inflater.inflate(R.layout.fragment_about, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gitHubLink = view.findViewById<TextView>(R.id.github_link)
        var content = SpannableString(resources.getString(R.string.github))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        gitHubLink.text = content
        gitHubLink.setOnClickListener {
            CustomTabs.openTab(
                context,
                resources.getString(R.string.github)
            )
        }
        val twitterLink = view.findViewById<TextView>(R.id.twitter_link)
        content = SpannableString(twitterLink.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        twitterLink.text = content
        twitterLink.setOnClickListener {
            CustomTabs.openTab(
                context,
                "https://twitter.com/Riksdagskollen"
            )
        }

        val devEmail1Link = view.findViewById<TextView>(R.id.email_dev_1)
        content = SpannableString(devEmail1Link.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        devEmail1Link.text = content
        devEmail1Link.setOnClickListener {
            val emailIntent = createDeveloperEmailIntent(resources.getString(R.string.dev_email_1))
            context?.startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"))
        }

        val devEmail2Link = view.findViewById<TextView>(R.id.email_dev_2)
        content = SpannableString(devEmail2Link.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        devEmail2Link.text = content
        devEmail2Link.setOnClickListener {
            val emailIntent = createDeveloperEmailIntent(resources.getString(R.string.dev_email_2))
            context?.startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"))
        }

        val contactLink = view.findViewById<TextView>(R.id.riksdagen_website)
        content = SpannableString(contactLink.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        contactLink.text = content
        contactLink.setOnClickListener {
            CustomTabs.openTab(
                context,
                getString(R.string.riksdagen_contact)
            )
        }

        val attributionPresenter = AttributionPresenter.Builder(context)
            .addAttributions(
                Library.GSON
            )
            .addAttributions(
                Attribution.Builder("jsoup: Java HTML Parser")
                    .addCopyrightNotice("Copyright Jonathan Hedley 2009 - 2017")
                    .addLicense(License.MIT)
                    .setWebsite("https://jsoup.org/")
                    .build(),
                Attribution.Builder("Volley")
                    .addLicense(License.APACHE)
                    .addCopyrightNotice("Copyright 2011 Google")
                    .setWebsite("https://github.com/google/volley")
                    .build(),
                Attribution.Builder("FlexboxLayout")
                    .addCopyrightNotice("Copyright 2016 Google")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/google/flexbox-layout")
                    .build(),
                Attribution.Builder("MPAndroidChart")
                    .addCopyrightNotice("Copyright 2018 Philipp Jahoda")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/PhilJay/MPAndroidChart")
                    .build(),
                Attribution.Builder("AttributionPresenter")
                    .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                    .build(),
                Attribution.Builder("Android-Job")
                    .addCopyrightNotice("Copyright (c) 2007-2017 by Evernote Corporation, All rights reserved.")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/evernote/android-job")
                    .build()
            )
            .build()
        show_licenses_button.setOnClickListener { attributionPresenter.showDialog("Programvara som hjälpt till att skapa Riksdagskollen") }
        try {
            val context: Context = RiksdagskollenApp.getInstance()
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            val versionView = view.findViewById<TextView>(R.id.version)
            versionView.text = "Version $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SECTION_NAME_ABOUT = "about"

        @JvmStatic
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}