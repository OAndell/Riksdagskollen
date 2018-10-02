package oscar.riksdagskollen.Manager;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.AuthenticatedJsonObjectRequest;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;


/**
 * Created by gustavaaro on 2018-02-15.
 */

public class RequestManager {

    private final static int POST = 1;
    private final static int GET = 0;
    private final static int PUT = 2;
    private final static int PATCH = 7;
    private final static int DELETE = 3;

    private final RequestQueue requestQueue;
    private final Cache cache;
    private final Network network = new BasicNetwork(new HurlStack());
    private final ImageLoader mImageLoader;

    public RequestManager(){
        cache = new DiskBasedCache(RiksdagskollenApp.getInstance().getCacheDir(), 1024 * 1024);
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        mImageLoader = new ImageLoader(this.requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

    public Request doAuthenticatedGetRequest(String subURL, String host, String accessToken, JSONRequestCallback callback) {
        return doAuthenticatedJsonRequest(GET, null, subURL, accessToken, host, callback);
    }

    public Request doAuthenticatedPostRequest(JSONObject jsonRequest, String subURL, String host, String accessToken, JSONRequestCallback callback) {
        return doAuthenticatedJsonRequest(POST, jsonRequest, subURL, accessToken, host, callback);
    }

    public Request doGetRequest(String subURL, String host, JSONRequestCallback callback) {
        return doJsonRequest(GET, null, subURL, host, callback);
    }

    public void doStringGetRequest(String subURL, String host, StringRequestCallback callback) {
        doStringRequest(GET, subURL, host, callback);
    }

    public void doPostRequest(JSONObject jsonRequest, String subURL, String host, JSONRequestCallback callback) {
        doJsonRequest(POST, jsonRequest, subURL, host, callback);
    }
    public void doPutRequest(JSONObject jsonRequest, String subURL, String host, JSONRequestCallback callback) {
        doJsonRequest(PUT, jsonRequest, subURL, host, callback);
    }

    public void doDeleteRequest(String subURL, String host, JSONRequestCallback callback) {
        doJsonRequest(DELETE, null, subURL, host, callback);
    }

    public void doPatchRequest(JSONObject jsonRequest, String subURL, String host, JSONRequestCallback callback) {
        doJsonRequest(PATCH, jsonRequest, subURL, host, callback);
    }

    private Request doAuthenticatedJsonRequest(int method, JSONObject jsonRequest, String subURL, String accessToken, String host, JSONRequestCallback callback) {
        String url = host + subURL;
        return queueAuthenticatedJSONRequest(jsonRequest, url, accessToken, method, callback);
    }

    private Request doJsonRequest(int method, JSONObject jsonRequest, String subURL, String host, JSONRequestCallback callback) {
        String url = host + subURL;
        return queueJSONRequest(jsonRequest, url, method, callback);
    }

    private void doStringRequest(int method, String subURL, String host, StringRequestCallback callback) {
        queueStringRequest(subURL,method,callback);
    }

    private Request queueJSONRequest(final JSONObject jsonRequest, final String url, final int method, final JSONRequestCallback callback) {
        System.out.println("Making request to: " + url);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Requests", "onErrorResponse: " + (error == null ? "" : error.getMessage()));
                callback.onRequestFail(error);
            }
        });
        requestQueue.add(jsonObjectRequest);
        return jsonObjectRequest;
    }

    private Request queueAuthenticatedJSONRequest(final JSONObject jsonRequest, final String url, String accessToken, final int method, final JSONRequestCallback callback) {
        System.out.println("Making authenticated request to: " + url);
        final JsonObjectRequest jsonObjectRequest = new AuthenticatedJsonObjectRequest(method, url, accessToken, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Requests", "onErrorResponse: " + (error == null ? "" : error.getMessage()));
                callback.onRequestFail(error);
            }
        });
        requestQueue.add(jsonObjectRequest);
        return jsonObjectRequest;
    }


    private Request queueStringRequest(final String url, final int method, final StringRequestCallback callback) {
        System.out.println("Making string-request to: " + url);
        final StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error);
            }
        });

        return requestQueue.add(request);
    }

    public Request getDownloadString(String url, StringRequestCallback callback) {
        return queueStringRequest(url, GET, callback);
    }

    public AsyncTask downloadHtmlPage(String url, StringRequestCallback callback){
        HtmlDownloader task = new HtmlDownloader(url,callback);
        return task.execute();
    }

    static class HtmlDownloader extends AsyncTask<String, String, String> {

        private final String url;

        private final StringRequestCallback callback;

        HtmlDownloader(String url, StringRequestCallback callback){
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.connect();

                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);

                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result){
            callback.onResponse(result);
        }


    }

}
