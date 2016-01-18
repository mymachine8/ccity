package com.cargoexchange.cargocity.cargocity.utils;

/**
 * Created by root on 18/1/16.
 */

import android.text.Html;
import android.text.Spanned;

import com.cargoexchange.cargocity.cargocity.models.NavigationInstructionModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseDirections
{
    private JSONObject jMapData;
    private ArrayList<NavigationInstructionModel> navigationList=new ArrayList<>();
    private ArrayList distanceDetails=new ArrayList();
    private String polyline;
    private List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    private List path = new ArrayList<HashMap<String, String>>();

    public ParseDirections(JSONObject MapData)
    {
        jMapData=MapData;
        calculateDistance();
    }
    public ArrayList<NavigationInstructionModel> getNavigationList()
    {
        return navigationList;
    }
    public ArrayList getDistanceDetails()
    {
        return distanceDetails;
    }

    public List<List<HashMap<String, String>>> getRoutes()
    {
        calculateDistance();
        decodePoly(polyline);
        return routes;
    }

    private void calculateDistance()
    {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        String mDistance=new String();
        String mDuration=new String();
        String html_instruction=new String();
        Spanned instruction;
        String maneuver=new String("");
        double start_lat,start_lng;

        try
        {
            jRoutes = jMapData.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++)
            {
                polyline = (String) ((JSONObject) ((JSONObject) jRoutes.get(i)).get("overview_polyline")).get("points");
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                mDistance=(String)((JSONObject)((JSONObject) jLegs.get(i)).get("distance")).get("text");
                mDuration=(String)((JSONObject)((JSONObject) jLegs.get(i)).get("duration_in_traffic")).get("text");
                for (int j=0;j<jLegs.length();j++)
                {
                    jSteps=((JSONObject)jLegs.get(j)).getJSONArray("steps");
                    for(int k=0;k<jSteps.length();k++)
                    {
                        html_instruction = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
                        instruction= Html.fromHtml(html_instruction);
                        /*polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);*/
                        if(((JSONObject) jSteps.get(k)).has("maneuver"))
                        {
                            maneuver=(String) ((JSONObject) jSteps.get(k)).get("maneuver");
                        }
                        else
                        {
                            maneuver=new String("");
                        }
                        navigationList.add(new NavigationInstructionModel(instruction,maneuver));
                    }
                }
            }
            distanceDetails.add(mDistance);
            distanceDetails.add(mDuration);
        }

        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void decodePoly(String encoded)
    {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        //return poly;
        for (int l = 0; l < poly.size(); l++)
        {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("lat", Double.toString(((LatLng) poly.get(l)).latitude));
            hm.put("lng", Double.toString(((LatLng) poly.get(l)).longitude));
            path.add(hm);
        }
        routes.add(path);
    }

}

