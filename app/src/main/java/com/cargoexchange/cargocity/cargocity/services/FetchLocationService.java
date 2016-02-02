package com.cargoexchange.cargocity.cargocity.services;

import android.Manifest;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.utils.ParseDirections;

import org.json.JSONObject;

/**
 * Created by root on 2/2/16.
 */
public class FetchLocationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchLocationService(String name) {
        super(name);
    }

    public FetchLocationService() {
        super("com.cargoexchange.cargocity.cargocity.FetchLocationService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        fetchRouteData();

    }

    public void fetchRouteData()
    {
        final LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestSingleUpdate(Constants.LOCATION_PROVIDER,new locationListener(),null);

    }
    private class locationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            String mDestination=null;
            if (location != null)
            {
                String url = Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                        + "key=" + Constants.GOOGLE_MAP_SERVER_KEY
                        + "&origin=" + location.getLatitude() + "," + location.getLongitude()
                        + "&destination=" + mDestination
                        + "&departure_time=" + Constants.MAP_DEPARTURETIME
                        + "&traffic_model=" + Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                        + "&mode=" + Constants.MAP_TRANSTMODE;

                JsonObjectRequest request = CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("test", response.toString());
                        //Use the routes to draw polyline on map
                        //routes = new ParseDirections(response).getRoutes();
                        //TODO:Store lat, long in db (ALSO, navigation inst[getNavigationList(),Call Distance Matrix API orderListFragment download();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                    }
                }, url);
                CargoCity.getmInstance().getRequestQueue().add(request);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
