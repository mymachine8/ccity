package com.cargoexchange.cargocity.cargocity;

/**
 * Created by root on 18/1/16.
 */

import android.app.Application;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cargoexchange.cargocity.cargocity.constants.CargoSharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CargoCity extends Application
{
    private static CargoCity mInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mRequestQueue= Volley.newRequestQueue(this);
        mInstance=this;

    }

    public static synchronized CargoCity getmInstance()
    {
        return mInstance;
    }
    public RequestQueue getRequestQueue()
    {
        return mRequestQueue;
    }

    public JsonObjectRequest getRequest(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener,String url)
    {
        JsonObjectRequest request = new JsonObjectRequest(url,null,responseListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = "Bearer " + getSharedPreferences(CargoSharedPreferences.MY_PREFS, MODE_PRIVATE)
                        .getString(CargoSharedPreferences.PREFERENCE_ACCESSTOKEN, null);
                params.put("Authorization", auth);
                return params;
            }
        };
        return request;
    }

    public JsonObjectRequest getGeneralRequest(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener,String url)
    {
        JsonObjectRequest request = new JsonObjectRequest(url,null,responseListener,errorListener);
        return request;
    }

    public JsonObjectRequest postLoginRequest(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener,String url, JSONObject requestData)
    {
        JsonObjectRequest request = new JsonObjectRequest(url,requestData,responseListener,errorListener);
        return request;
    }

    public JsonObjectRequest postRequest(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener,String url,JSONObject requestData)
    {
        JsonObjectRequest request = new JsonObjectRequest(url,requestData,responseListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = "Bearer " + getSharedPreferences(CargoSharedPreferences.MY_PREFS, MODE_PRIVATE)
                                                            .getString(CargoSharedPreferences.PREFERENCE_ACCESSTOKEN, null);
                params.put("Authorization", auth);
                return params;
            }
        };
        return request;
    }
}
