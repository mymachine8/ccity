package com.cargoexchange.cargocity.cargocity.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 21/1/16.
 */
public class GenerateRequest
{
    public JSONObject getRequest(String email,String password)
    {
        JSONObject request=new JSONObject();
        try {
            request.put("username", email);
            request.put("password",password);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        Log.d("JSON",request.toString());
        return request;
    }
}
