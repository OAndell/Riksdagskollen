package oscar.riksdagskollen.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;


import com.android.volley.VolleyError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.JSONModels.Protocol;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;


/**
 * Created by oscar on 2018-06-07.
 *
 */

public class ProtocolReaderActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        Protocol document = (Protocol) getIntent().getParcelableExtra("document");

        final ViewGroup loadingView = findViewById(R.id.loading_view);

        final WebView webView = findViewById(R.id.news_reader_webview);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                if(newProgress == 100){
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);


        final RikdagskollenApp app = RikdagskollenApp.getInstance();
        app.getRequestManager().downloadHtmlPage("http:"+ document.getDokument_url_html(), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);

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

            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }


}
