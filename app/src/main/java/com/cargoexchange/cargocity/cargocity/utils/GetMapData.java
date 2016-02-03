package com.cargoexchange.cargocity.cargocity.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 3/2/16.
 */
public class GetMapData
{
    private Location location;
    private String url;
    private String mDestination;
    private List<List<HashMap<String, String>>> routes;
    public void fetchMapData(Location location)
    {/*
        if (location != null)
        {
            url = Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                    + "key=" + Constants.GOOGLE_MAP_SERVER_KEY
                    + "&origin=" + location.getLatitude() + "," + location.getLongitude()
                    + "&destination=" + mDestination
                    + "&departure_time=" + Constants.MAP_DEPARTURETIME
                    + "&traffic_model=" + Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                    + "&mode=" + Constants.MAP_TRANSTMODE;

            JsonObjectRequest request = CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("test", response.toString());
                    //Use the routes to draw polyline on map
                    routes = new ParseDirections(response).getRoutes();
                    //pass this bundle to extract the navigation instructions
                    sendToNavigationFragmentBundle = new Bundle();
                    sendToNavigationFragmentBundle.putString("mapData", response.toString());
                    mapFragment.getMapAsync(MapActivity.this);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mapDataProgress.dismiss();
                    Toast.makeText(MapActivity.this, "Error!Please try again", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }, url);
            CargoCity.getmInstance().getRequestQueue().add(request);

            mNavigationFloatingActionButton = (FloatingActionButton)
                    findViewById(R.id.navigationFloatingActionButton);
            mNavigationFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationIntent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + mDestination + "&mode=d"))
                            .setPackage("com.google.android.apps.maps");

                    if (navigationIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(navigationIntent, 1);
                    }
                }
            });
        } else
            Toast.makeText(this, "Error fethcing location", Toast.LENGTH_SHORT);*/
    }
}
