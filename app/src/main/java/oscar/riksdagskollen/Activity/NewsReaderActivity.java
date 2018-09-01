package oscar.riksdagskollen.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;


import oscar.riksdagskollen.R;



/**
 * Created by oscar on 2018-04-07.
 */

public class NewsReaderActivity extends AppCompatActivity {

    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        url =  getIntent().getStringExtra("url");
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
        if(url.startsWith("http")){
            webView.loadUrl(url);
        }
        else{
            webView.loadUrl("http://riksdagen.se"+url);
        }
        loadingView.setVisibility(View.GONE);
    }




}
