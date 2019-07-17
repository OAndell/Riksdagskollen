package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oscar.riksdagskollen.DebateView.Data.DebateStatement;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;

public class DebateWebTvView extends WebView {

    private DebateStatement debateStatement;
    private Context context;
    private OnDocumentHtmlViewLoadedCallback loadedCallack;


    public DebateWebTvView(Context context, DebateStatement debateStatement) {
        super(context);
        this.context = context;
        this.debateStatement = debateStatement;
        setupWebView();
    }

    public DebateWebTvView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupWebView();
    }

    public void setDebateStatement(DebateStatement debateStatement) {
        this.debateStatement = debateStatement;
        setupWebView();
    }


    public void setLoadedCallack(OnDocumentHtmlViewLoadedCallback loadedCallack) {
        this.loadedCallack = loadedCallack;
    }

    private void setupWebView() {
        WebViewClient webViewClient = new CustomWebViewClient();

        setWebViewClient(webViewClient);
        getSettings().setDomStorageEnabled(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setPluginState(WebSettings.PluginState.ON);
        setWebChromeClient(new WebChromeClient());

        //Disable text-select to make consistent with rest of app
        setLongClickable(false);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

    }


    class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            String meta = "<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html, charset='utf-8'>";
            System.out.println("document.getElementsByTagName('head')[0].appendChild(" + meta + ");");
            view.loadUrl("javascript:(function() { " +
                    "var meta = document.createElement('meta');" +
                    "meta.innerHTML = \"<meta name=\\\"viewport\\\" content='width=device-width, initial-scale=1.0,text/html, charset='utf-8'>\";" +
                    "document.getElementsByTagName('head')[0].appendChild(meta);" +
                    "document.getElementsByClassName('vlp-embed-meta ng-scope')[0].style.display='none';" +
                    "document.getElementsByClassName('vlp-embed-logo ng-scope')[0].style.display='none';" +
                    " })()");
        }
    }


}
