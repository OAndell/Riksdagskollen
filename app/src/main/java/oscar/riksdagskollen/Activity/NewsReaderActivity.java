package oscar.riksdagskollen.Activity;

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
import org.jsoup.select.Elements;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;
import oscar.riksdagskollen.Util.JSONModel.StringRequestCallback;


/**
 * Created by oscar on 2018-04-07.
 * TODO This class is currently unused but should be used to display an in app news readers
 */

public class NewsReaderActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        CurrentNews document = (CurrentNews) getIntent().getParcelableExtra("document");

        final ViewGroup loadingView = findViewById(R.id.loading_view);

        //final TextView body = findViewById(R.id.news_body);
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
        app.getRiksdagenAPIManager().getNewsHTML(document.getSummary(), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Elements elements = doc.getElementsByClass("main-content");
                Document newdoc = Jsoup.parse(elements.toString());
                newdoc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=2.0,text/html, charset='utf-8'>\n");

                webView.loadData(newdoc.toString(), "text/html", "UTF-8" );
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }


}
