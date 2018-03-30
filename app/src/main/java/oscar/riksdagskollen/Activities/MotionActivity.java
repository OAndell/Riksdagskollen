package oscar.riksdagskollen.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import oscar.riksdagskollen.Managers.RiksdagenAPIManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.RepresentativeCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Intressent;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;

/**
 * Created by gustavaaro on 2018-03-29.
 */

public class MotionActivity extends AppCompatActivity {

    PartyDocument document;
    ViewGroup loadingView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        document = getIntent().getParcelableExtra("document");

        TextView titleTV = findViewById(R.id.act_doc_reader_title);
        TextView authorTV = findViewById(R.id.act_doc_reader_author);
        loadingView = findViewById(R.id.loading_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Motion");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titleTV.setText(document.getTitel());
        authorTV.setText(document.getUndertitel());

        WebChromeClient webChromeClient = new WebChromeClient();
        final WebView webView = findViewById(R.id.webview);
        webView.setWebChromeClient(webChromeClient);

        final RikdagskollenApp app = RikdagskollenApp.getInstance();

        app.getRiksdagenAPIManager().getDocumentBody(document, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                //Remove title
                doc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html, charset='utf-8'>\n");
                doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "motion_style.css");
                doc.select("div>span.sidhuvud_publikation").remove();
                doc.select("div>span.sidhuvud_beteckning").remove();
                doc.select("div>span.MotionarLista").remove();
                doc.select("div.pconf>h1").remove();
                doc.select("div>hr.sidhuvud_linje").remove();
                doc.select("head>style").remove();
                doc.select("body>div>br").remove();

                //Clear default styling
                String result  = doc.toString().replaceAll("class=\\\"[A-Öa-ö0-9]+\\\"","");
                result = result.replaceAll("style=\"[A-Öa-ö-_:;\\s0-9.%']+\"","");
                webView.loadDataWithBaseURL("file:///android_asset/", result, "text/html", "UTF-8", null);
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);

        //Disable text-select to make consistent with rest of app
        webView.setLongClickable(false);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        LinearLayout portaitContainer = findViewById(R.id.act_doc_reader_portrait_container);


        for (Intressent i : document.getDokintressent().getIntressenter()){
            final View portraitView;
            TextView nameTv;
            System.out.println(i.getNamn());
            if(i.getRoll().equals("undertecknare")){
                portraitView = LayoutInflater.from(this).inflate(R.layout.intressent_layout,null);
                final NetworkImageView portrait = portraitView.findViewById(R.id.intressent_portait);
                portrait.setDefaultImageResId(R.mipmap.ic_default_person);
                nameTv = portraitView.findViewById(R.id.intressent_name);
                nameTv.setText(i.getNamn() + " (" + i.getPartibet() + ")");


                app.getRiksdagenAPIManager().getRepresentative(i.getIntressent_id(), new RepresentativeCallback() {
                    @Override
                    public void onPersonFetched(Representative representative) {
                        portrait.setImageUrl(representative.getBild_url_192(),app.getRequestManager().getmImageLoader());
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });
                portaitContainer.addView(portraitView);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
