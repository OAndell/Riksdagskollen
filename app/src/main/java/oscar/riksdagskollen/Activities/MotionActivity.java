package oscar.riksdagskollen.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    ProgressBar progress;
    Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        document = getIntent().getParcelableExtra("document");

        TextView titleTV = findViewById(R.id.act_doc_reader_title);
        TextView authorTV = findViewById(R.id.act_doc_reader_author);
        loadingView = findViewById(R.id.loading_view);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Motion");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titleTV.setText(document.getTitel());
        authorTV.setText(document.getUndertitel());

        WebViewClient webViewClient = new CustomWebViewClient();

        final WebView webView = findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                if(newProgress == 100){
                    loadingView.setVisibility(View.GONE);
                    }
            }
        });
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setInitialScale(1);

        //Disable text-select to make consistent with rest of app
        webView.setLongClickable(false);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });


        final RikdagskollenApp app = RikdagskollenApp.getInstance();
        app.getRiksdagenAPIManager().getDocumentBody( document, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                //Remove title
                doc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=2.0,text/html, charset='utf-8'>\n");
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
                System.out.println(result);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });    


        LinearLayout portaitContainer = findViewById(R.id.act_doc_reader_portrait_container);


        for (Intressent i : document.getDokintressent().getIntressenter()){
            final View portraitView;
            TextView nameTv;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.motion_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.open_in_web:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http:"+document.getDokument_url_html()));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomWebViewClient extends WebViewClient{

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Kunde inte hämta motionen");
            builder.setMessage("Motionen kunde inte hämtas. Vill du öppna i webbläsaren istället?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http:"+document.getDokument_url_html()));
                    startActivity(i);
                }
            });
            builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MotionActivity) context).finish();
                }
            });
            builder.show();
        }
    }

}
