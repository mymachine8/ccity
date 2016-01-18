package com.cargoexchange.cargocity.cargocity;

/**
 * Created by root on 18/1/16.
 */

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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

    public JsonObjectRequest getProduct(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener,String url)
    {
        JsonObjectRequest request = new JsonObjectRequest(url,null,responseListener,errorListener);
        return request;
    }
}
