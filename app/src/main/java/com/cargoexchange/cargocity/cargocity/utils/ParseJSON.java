package com.cargoexchange.cargocity.cargocity.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thespidy on 27/01/16.
 */
public class ParseJSON {
    public static String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }
}
