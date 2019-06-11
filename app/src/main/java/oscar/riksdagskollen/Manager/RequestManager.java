package oscar.riksdagskollen.Manager;

import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONObject;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.DesktopStringRequest;
import oscar.riksdagskollen.Util.Helper.TwitterAuthRequest;
import oscar.riksdagskollen.Util.Helper.TwitterGetRequest;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONArrayCallback;
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

    public Request doGetRequest(String subURL, String host, JSONRequestCallback callback) {
        return doJsonRequest(GET, null, subURL, host, callback);
    }

    public void doStringGetRequest(String subURL, String host, StringRequestCallback callback) {
        doStringRequest(GET, subURL, host, callback);
    }

    public void doPostRequest(JSONObject jsonRequest, String subURL, String host, JSONRequestCallback callback) {
        doJsonRequest(POST, jsonRequest, subURL, host, callback);
    }

    public void doTwitterAuthRequest(JSONObject jsonRequest, String subURL, String host, String apiKey, final JSONRequestCallback callback) {
        System.out.println("Making request to: " + host + subURL);
        TwitterAuthRequest request = new TwitterAuthRequest(POST, host + subURL, apiKey, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onRequestFail(error);
            }
        });
        requestQueue.add(request);
    }

    public void doTwitterGetRequest(String subURL, String host, String token, final JSONArrayCallback callback) {
        System.out.println("Making request to: " + host + subURL);
        TwitterGetRequest request = new TwitterGetRequest(GET, host + subURL, token, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onRequestFail(error);
            }
        });
        requestQueue.add(request);
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


    private Request queueStringRequest(final String url, final int method, final StringRequestCallback callback) {
        System.out.println("Making string-request to: " + url);
        final StringRequest request = new DesktopStringRequest(method, url, new Response.Listener<String>() {
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
        //remove swedish chars
        url = url.replaceAll("ö", "%C3%B6");
        url = url.replaceAll("å", "%C3%A5");
        url = url.replaceAll("ä", "%C3%A4");
        return queueStringRequest(url, GET, callback);
    }
}
