package oscar.riksdagskollen.Util.WebTV;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.RiksdagenCallback.OnDocumentHtmlViewLoadedCallback;

public class DebateWebTvView extends WebView {

    private PartyDocument debate;
    private Context context;
    private OnDocumentHtmlViewLoadedCallback loadedCallack;
    private String videoSelector = "document.querySelector(\"body > div.row.ng-scope > div > div > div > div > div.video-include.ng-scope > div:nth-child(4) > video\")";

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_SEEK_FORWARD = "ACTION_SEEK_FORWARD";
    public static final String ACTION_SEEK_BACKWARD = "ACTION_SEEK_BACKWARD";




    public DebateWebTvView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupWebView();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setDebate(PartyDocument debate) {
        this.debate = debate;
        setupWebView();
    }


    public void setLoadedCallack(OnDocumentHtmlViewLoadedCallback loadedCallack) {
        this.loadedCallack = loadedCallack;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void loadDebate() {
        loadUrl(getDebateURL());
    }

    public String getDebateURL() {
        return String.format("http://www.riksdagen.se/views/pages/embedpage.aspx" +
                        "?did=%s",
                debate.getId());
    }

    public void play() {
        this.loadUrl("javascript:mediaElement.play();");
    }

    public void pause() {
        this.loadUrl("javascript:mediaElement.pause();");
    }

    public void setCurrentTime(int seconds) {

        this.loadUrl("javascript:mediaElement.currentTime = " + seconds + ";");
    }

    public void seekForward() {
        this.loadUrl("javascript:mediaElement.currentTime = mediaElement.currentTime + " + 30 + ";");

    }

    public void seekBackward() {
        this.loadUrl("javascript:mediaElement.currentTime = mediaElement.currentTime - " + 30 + ";");

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

        if (debate != null) {
            addJavascriptInterface(new JSInterface(getContext(), debate.getTitel(), debate.getUndertitel()), "JSOUT");
        }



        //Disable text-select to make consistent with rest of app
        setLongClickable(false);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility != View.GONE) super.onWindowVisibilityChanged(View.VISIBLE);
    }

    class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }


        @Override
        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:(function() { " +
                    "var meta = document.createElement('meta');" +
                    "meta.innerHTML = \"<meta name=\\\"viewport\\\" content='width=device-width, initial-scale=1.0,text/html, charset='utf-8'>\";" +
                    "document.getElementsByTagName('head')[0].appendChild(meta);" +
                    "document.getElementsByClassName('vlp-embed-meta ng-scope')[0].style.display='none';" +
                    "document.getElementsByClassName('vlp-embed-logo ng-scope')[0].style.display='none';" +
                    " })()");


            String mediaPlaybackCode = "var mediaElement;" +
                    "mediaCheck();" +
                    "document.onclick = function(){" +
                    "    mediaCheck();" +
                    "};" +
                    "function mediaCheck(){" +
                    "    for(var i = 0; i < document.getElementsByTagName('video').length; i++){" +
                    "        var media = document.getElementsByTagName('video')[i];" +
                    "        mediaElement = media;" +
                    "        media.onplay = function(){" +
                    "            mediaElement = media;" +
                    "            JSOUT.mediaAction('true');" +
                    "        };" +
                    "        media.onpause = function(){" +
                    "            mediaElement = media;" +
                    "            JSOUT.mediaAction('false');" +
                    "        };" +
                    "    } " +
                    "}";
            view.loadUrl("javascript:" + mediaPlaybackCode);

        }


    }


}
