package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.VolleyError;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import oscar.riksdagskollen.Activity.DocumentReaderActivity;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;

public class DocumentHtmlView extends WebView {

    private PartyDocument document;
    private Context context;
    private OnDocumentHtmlViewLoadedCallback loadedCallack;


    public DocumentHtmlView(Context context, PartyDocument document) {
        super(context);
        this.context = context;
        this.document = document;
        setupWebView();
    }

    public DocumentHtmlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDocument(PartyDocument document) {
        this.document = document;
        setupWebView();
    }


    public void setLoadedCallack(OnDocumentHtmlViewLoadedCallback loadedCallack) {
        this.loadedCallack = loadedCallack;
    }

    private void setupWebView() {
        WebViewClient webViewClient = new CustomWebViewClient();

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                if (newProgress == 100) {
                    if (loadedCallack != null) loadedCallack.onDocumentLoaded();
                }
            }
        });
        setWebViewClient(webViewClient);
        getSettings().setDomStorageEnabled(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setJavaScriptEnabled(true);

        setInitialScale(1);

        //Disable text-select to make consistent with rest of app
        setLongClickable(false);
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });


        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentBody(document, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                //Remove title
                doc.head().append("<meta name=\"viewport\" content='width=device-width, initial-scale=2.0,text/html, charset='utf-8'>\n");
                doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme().getCss());
                doc.select("div>span.sidhuvud_publikation").remove();
                doc.select("div>span.sidhuvud_beteckning").remove();
                doc.select("div>span.MotionarLista").remove();
                doc.select("div.pconf>h1").remove();
                doc.select("div>hr.sidhuvud_linje").remove();
                doc.select("head>style").remove();
                doc.select("body>div>br").remove();

                if (document.getDoktyp().equals("frs")) {
                    try {
                        // Add title styling
                        doc.select("body>div>div>p").get(0).addClass("DokumentRubrik");
                        doc.select("body>div>div>table.webbtabell").remove();

                        // Only show body of response
                        doc.body().replaceWith(doc.select("body>div>div").get(0));
                    } catch (IndexOutOfBoundsException e) {
                        // Failed to get response body
                        e.printStackTrace();
                    }
                }

                //String result = doc.toString().replaceAll("class=\\\"[A-Öa-ö0-9]+\\\"","");
                //Clear default styling
                String result = doc.toString().replaceAll("style=\"[A-Öa-ö-_:;\\s0-9.%']+\"", "");
                loadDataWithBaseURL("file:///android_asset/", result, "text/html", "UTF-8", null);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }


    class CustomWebViewClient extends WebViewClient {

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
                    i.setData(Uri.parse("http:" + document.getDokument_url_html()));
                    context.startActivity(i);
                }
            });
            builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DocumentReaderActivity) context).finish();
                }
            });
            builder.show();
        }
    }


}
