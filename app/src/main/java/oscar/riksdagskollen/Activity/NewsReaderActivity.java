package oscar.riksdagskollen.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.android.volley.VolleyError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;
import oscar.riksdagskollen.Util.Callback.StringRequestCallback;


/**
 * Created by oscar on 2018-04-07.
 * TODO This class is currently unused but should be used to display an in app news readers
 */

public class NewsReaderActivity extends AppCompatActivity {

    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        url =  getIntent().getStringExtra("url");
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
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i("WebView", "Attempting to load URL: " + url);

                view.loadUrl(url);
                return true;
            }
        });

        final RikdagskollenApp app = RikdagskollenApp.getInstance();
        app.getRiksdagenAPIManager().getNewsHTML(url, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    Document doc = Jsoup.parse(response);
                    Elements elements = doc.getElementsByClass("page-contents");

                    if(elements.isEmpty()){
                        createErrorDialog();
                    }
                    doc = Jsoup.parse(elements.toString());
                    doc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html, charset='utf-8'>\n");
                    doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "news_style.css");


                    String result = doc.toString().replaceAll("style=\"[A-Öa-ö-_:;\\s0-9.%']+\"", "");
                    webView.loadDataWithBaseURL("file:///android_asset/", result, "text/html", "UTF-8", null);
                    loadingView.setVisibility(View.GONE);
                }catch (Exception e){
                    System.out.println("Something went wrong getting page");
                    System.out.println(e);
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.motion_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.open_in_web:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://riksdagen.se"+ url));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewsReaderActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Kunde inte visa objektet.");
        builder.setMessage("Öppna objekt i webbläsaren?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://riksdagen.se"+ url));
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();

    }



}
