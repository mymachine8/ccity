package com.cargoexchange.cargocity.cargocity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.fragments.NavigationInstructionFragment;
import com.cargoexchange.cargocity.cargocity.services.LocationService;
import com.cargoexchange.cargocity.cargocity.utils.ParseDirections;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAP_KEY = "AIzaSyAa_HArC674Ruuyw91n2jNvntSoaIPyZ64";
    private static final String MAP_TRAFFICMODEL_PESSIMISTIC = "pessimistic";
    private static final String MAP_TRAFFICMODEL_OPTIMISTIC = "optimistic";
    private static final String MAP_TRAFFICMODEL_BESTGUESS = "best_guess";
    private static final String MAP_DEPARTURETIME = "now";
    private static final String MAP_TRANSTMODE = "bus";
    private static final String MAP_REPLYJSON = "json";
    private static final String MAP_REPLYXML = "xml";
    private SupportMapFragment mapFragment;
    private boolean menuStatus = false;
    private FloatingActionButton mNavigationFloatingActionButton;
    private Intent navigationIntent;
    private String mDestination = "uppal,hyderabad";
    String url = "https://maps.googleapis.com/maps/api/directions/" + MAP_REPLYJSON + "?key=" + MAP_KEY + "&departure_time=" + MAP_DEPARTURETIME + "&traffic_model=" + MAP_TRAFFICMODEL_PESSIMISTIC + "&transit_mode=" + MAP_TRANSTMODE + "&origin=madhapur,hyderabad&destination=uppal,hyderabad";
    Bundle sendToNavigationFragmentBundle;
    ProgressDialog mapDataProgress;
    private List<List<HashMap<String, String>>> routes;
    final MarkerOptions markerCurrent = new MarkerOptions();
    final MarkerOptions markerA = new MarkerOptions();
    final MarkerOptions markerB = new MarkerOptions();
    ArrayList<LatLng> points;
    LatLng start;
    PolylineOptions lineOptions;
    private int TYPE = 0;
    private Location mPrevLocation, mCurrent;
    private LocationManager mLocationManager;
    private float distance = 0.0f;
    private MenuItem action_endMenuItem;
    private MenuItem action_navigationMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment); //R.id.mapfragment
        mapDataProgress = new ProgressDialog(this);
        mapDataProgress.setMessage("Loading...");
        mapDataProgress.setTitle("Map");
        mapDataProgress.show();
        JsonObjectRequest request = CargoCity.getmInstance().getProduct(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("test", response.toString());
                routes = new ParseDirections(response).getRoutes();
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

        mNavigationFloatingActionButton = (FloatingActionButton) findViewById(R.id.navigationFloatingActionButton);
        mNavigationFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + mDestination + "&mode=d")).setPackage("com.google.android.apps.maps");
                if (navigationIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(navigationIntent, 1);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (TYPE == 0) {
            points = null;
            lineOptions = null;
            start = new LatLng(0, 0);

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                lineOptions.color(Color.BLUE);

                // Fetching i-th route
                List<HashMap<String, String>> path = routes.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    if (j == 0) {
                        start = position;
                        markerA.position(position);
                        markerA.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck));
                    }
                    if (j == (path.size() - 1)) {
                        markerB.position(position);
                        markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    }
                    points.add(position);
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 14));
                googleMap.addMarker(markerA);
                lineOptions.addAll(points);
                googleMap.addMarker(markerB);
            }
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
            TYPE = 1;
            mapDataProgress.cancel();
        } else {
            googleMap.clear();
            //googleMap.addMarker(markerA);
            googleMap.addPolyline(lineOptions);
            googleMap.addMarker(markerB);
            LatLng current = new LatLng(mCurrent.getLatitude(), mCurrent.getLongitude());
            markerCurrent.position(current);
            markerCurrent.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_truck));
            googleMap.addMarker(markerCurrent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Item clicked", "clicked");
        int id = item.getItemId();
        switch (id) {
            case R.id.action_end_trip:
                Intent testintent=new Intent(MapActivity.this,DeliveryFeedbackActivity.class);
                startActivity(testintent);
                break;

            case R.id.action_start_trip:
                mNavigationFloatingActionButton.setVisibility(View.VISIBLE);
                menuStatus = true;
                this.invalidateOptionsMenu();
                break;

            case R.id.action_navigation_instruction:
                Fragment navigationFragment = new NavigationInstructionFragment();
                navigationFragment.setArguments(sendToNavigationFragmentBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, navigationFragment).addToBackStack("list").commit();
                break;
        }
        return false;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menuStatus) {
            menu.removeItem(R.id.action_start_trip);
            action_endMenuItem = menu.findItem(R.id.action_end_trip);
            action_navigationMenuItem = menu.findItem(R.id.action_navigation_instruction);
            action_endMenuItem.setVisible(true);
            action_navigationMenuItem.setVisible(true);
            trackVehicle();
        }
        return true;
    }

    private void trackVehicle() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mPrevLocation = mLocationManager.getLastKnownLocation(mLocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 3000, 0, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                distance=distance+location.distanceTo(mPrevLocation);
                mPrevLocation=location;
                mCurrent=location;
                mapFragment.getMapAsync(MapActivity.this);
                TYPE=1;

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        });
    }
}
