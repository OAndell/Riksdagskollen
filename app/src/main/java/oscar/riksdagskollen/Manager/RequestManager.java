package oscar.riksdagskollen.Manager;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
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

import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Callback.JSONRequestCallback;
import oscar.riksdagskollen.Util.JSONModel.StringRequestCallback;


/**
 * Created by gustavaaro on 2018-02-15.
 */

public class RequestManager {

    private final static String baseUrl = "http://data.riksdagen.se";

    private final static int POST = 1;
    private final static int GET = 0;
    private final static int PUT = 2;
    private final static int PATCH = 7;
    private final static int DELETE = 3;

    RequestQueue requestQueue;
    Cache cache;
    Network network = new BasicNetwork(new HurlStack());
    ImageLoader mImageLoader;

    public RequestManager(){
        cache = new DiskBasedCache(RikdagskollenApp.getInstance().getCacheDir(), 1024 * 1024);
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

    public void doGetRequest(String subURL, JSONRequestCallback callback){
        doJsonRequest(GET,null,subURL,callback);
    }

    public void doStringGetRequest(String subURL, StringRequestCallback callback){
        doStringRequest(GET,subURL,callback);
    }

    public void doPostRequest(JSONObject jsonRequest, String subURL, JSONRequestCallback callback){
        doJsonRequest(POST,jsonRequest,subURL,callback);
    }

    public void doPutRequest(JSONObject jsonRequest, String subURL, JSONRequestCallback callback){
        doJsonRequest(PUT,jsonRequest,subURL,callback);
    }

    public void doDeleteRequest(String subURL, JSONRequestCallback callback){
        doJsonRequest(DELETE,null,subURL,callback);
    }

    public void doPatchRequest(JSONObject jsonRequest, String subURL, JSONRequestCallback callback){
        doJsonRequest(PATCH,jsonRequest,subURL,callback);
    }

    private void doJsonRequest(int method, JSONObject jsonRequest, String subURL, JSONRequestCallback callback){
        String url = baseUrl + subURL;
        queueJSONRequest(jsonRequest,url,method,callback);
    }

    private void doStringRequest(int method, String subURL, StringRequestCallback callback){
        queueStringRequest(subURL,method,callback);
    }

    private void queueJSONRequest(final JSONObject jsonRequest, final String url, final int method, final JSONRequestCallback callback){
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
    }

    private void queueStringRequest(final String url, final int method, final StringRequestCallback callback ){
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

        requestQueue.add(request);
    }

    public void downloadHtmlPage(String url, StringRequestCallback callback){
        HtmlDownloader task = new HtmlDownloader(url,callback);
        task.execute();
    }

    class HtmlDownloader extends AsyncTask<String, String, String> {

        private String url;

        private StringRequestCallback callback;

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
