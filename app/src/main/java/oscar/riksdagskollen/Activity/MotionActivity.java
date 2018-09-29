package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Callback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.Callback.RepresentativeCallback;
import oscar.riksdagskollen.Util.Callback.StringRequestCallback;
import oscar.riksdagskollen.Util.Helper.CustomTabs;
import oscar.riksdagskollen.Util.JSONModel.Intressent;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by gustavaaro on 2018-03-29.
 */

public class MotionActivity extends AppCompatActivity {

    ProgressBar progress;
    private PartyDocument document;
    private ViewGroup loadingView;
    private Context context;
    private LinearLayout portaitContainer;
    private MenuItem notificationItem;
    RiksdagskollenApp app;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_motion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        document = getIntent().getParcelableExtra("document");
        String intresentId = getIntent().getStringExtra("intressent");

        TextView titleTV = findViewById(R.id.act_doc_reader_title);
        TextView authorTV = findViewById(R.id.act_doc_reader_author);
        loadingView = findViewById(R.id.loading_view);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(document.getDokumentnamn());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!document.isMotion()){
            titleTV.setVisibility(View.GONE);
            authorTV.setVisibility(View.GONE);
            findViewById(R.id.divider).setVisibility(View.GONE);
        } else {
            titleTV.setText(document.getTitel());
            authorTV.setText(document.getUndertitel());
        }

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


        app = RiksdagskollenApp.getInstance();
        app.getRiksdagenAPIManager().getDocumentBody( document, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                //Remove title
                doc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=2.0,text/html, charset='utf-8'>\n");
                doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", app.getThemeManager().getCurrentTheme().getCss());
                doc.select("div>span.sidhuvud_publikation").remove();
                doc.select("div>span.sidhuvud_beteckning").remove();
                doc.select("div>span.MotionarLista").remove();
                doc.select("div.pconf>h1").remove();
                doc.select("div>hr.sidhuvud_linje").remove();
                doc.select("head>style").remove();
                doc.select("body>div>br").remove();


                if(document.getDoktyp().equals("frs")){
                    try {
                        // Add title styling
                        doc.select("body>div>div>p").get(0).addClass("DokumentRubrik");
                        doc.select("body>div>div>table.webbtabell").remove();

                        // Only show body of response
                        doc.body().replaceWith(doc.select("body>div>div").get(0));
                    } catch (IndexOutOfBoundsException e){
                        // Failed to get response body
                        e.printStackTrace();
                    }
                }

                //String result = doc.toString().replaceAll("class=\\\"[A-Öa-ö0-9]+\\\"","");
                //Clear default styling
                String result = doc.toString().replaceAll("style=\"[A-Öa-ö-_:;\\s0-9.%']+\"","");
                webView.loadDataWithBaseURL("file:///android_asset/", result, "text/html", "UTF-8", null);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        portaitContainer = findViewById(R.id.act_doc_reader_portrait_container);


        if (document.getDokintressent() == null && intresentId != null) addSenderView(intresentId);
        else if (document.getDokintressent() != null) showSenders();

    }

    private void searchForReplyOrQuestion() {
        //Tries to find a response to the document if the doc has type = "fr"
        if (document.getTyp().equals("fr")) {
            app.getRiksdagenAPIManager().searchForReply(document, new PartyDocumentCallback() {
                @Override
                public void onDocumentsFetched(List<PartyDocument> documents) {
                    final PartyDocument reply = documents.get(0);
                    Button replyButton = findViewById(R.id.reply_button);
                    replyButton.setVisibility(View.VISIBLE);
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MotionActivity.class);
                            intent.putExtra("document", (reply));
                            startActivity(intent);
                        }
                    });
                    app.getAlertManager().setAlertEnabledForDoc(document, false);
                }

                @Override
                public void onFail(VolleyError error) {
                    notificationItem.setVisible(true);
                    findViewById(R.id.notification_tip).setVisibility(View.VISIBLE);
                }
            });
        } else if (document.getTyp().equals("frs")) {
            app.getRiksdagenAPIManager().searchForQuestion(document, new PartyDocumentCallback() {
                @Override
                public void onDocumentsFetched(List<PartyDocument> documents) {
                    final PartyDocument reply = documents.get(0);
                    Button replyButton = findViewById(R.id.reply_button);
                    replyButton.setText("Läs fråga");
                    replyButton.setVisibility(View.VISIBLE);
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MotionActivity.class);
                            intent.putExtra("document", (reply));
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onFail(VolleyError error) {
                }
            });
        }
    }

    private void showSenders() {
        for (Intressent i : document.getDokintressent().getIntressenter()){
            if(i.getRoll().equals("undertecknare") || (document.getDoktyp().equals("frs") && i.getRoll().equals("besvaradav"))){
                addSenderView(i.getIntressent_id());
            }
        }
    }

    private void addSenderView(String iid) {
        final View portraitView = LayoutInflater.from(this).inflate(R.layout.intressent_layout, null);
        final NetworkImageView portrait = portraitView.findViewById(R.id.intressent_portait);
        portrait.setDefaultImageResId(R.drawable.ic_person);
        final TextView nameTv = portraitView.findViewById(R.id.intressent_name);

        final AppCompatActivity activity = this;
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getRepresentative(iid, new RepresentativeCallback() {
            @Override
            public void onPersonFetched(final Representative representative) {
                portrait.setImageUrl(representative.getBild_url_192(), RiksdagskollenApp.getInstance().getRequestManager().getmImageLoader());
                portraitView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent repDetailsIntent = new Intent(activity, RepresentativeDetailActivity.class);
                        repDetailsIntent.putExtra("representative", representative);
                        startActivity(repDetailsIntent);
                    }
                });
                nameTv.setText(representative.getTilltalsnamn() + " " + representative.getEfternamn() + " (" + representative.getParti() + ")");

            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
        portaitContainer.addView(portraitView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchForReplyOrQuestion();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.motion_activity_menu,menu);
        notificationItem = menu.findItem(R.id.notification_menu_item);
        if (RiksdagskollenApp.getInstance().getAlertManager().isAlertEnabledForDoc(document)) {
            notificationItem.setIcon(R.drawable.ic_notification_enabled);
        }
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
                CustomTabs.openTab(this, Uri.parse("http:" + document.getDokument_url_html()).toString());
                break;
            case R.id.notification_menu_item:
                boolean enabled = RiksdagskollenApp.getInstance().getAlertManager().toggleEnabledForDoc(document);
                if (enabled) {
                    item.setIcon(R.drawable.ic_notification_enabled);
                    Toast.makeText(getApplicationContext(), "Du kommer nu få en notis när ett svar publiceras på denna fråga", Toast.LENGTH_LONG).show();
                }
                else item.setIcon(R.drawable.ic_notifications_disabled);
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
