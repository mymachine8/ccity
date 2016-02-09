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
import android.support.v4.app.FragmentTransaction;
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
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.fragments.NavigationInstructionFragment;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.services.LocationService;
import com.cargoexchange.cargocity.cargocity.utils.IsLocationLatest;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseAddress;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static final String MAP_KEY = "AIzaSyAa_HArC674Ruuyw91n2jNvntSoaIPyZ64";
    private static final String MAP_TRAFFICMODEL_PESSIMISTIC = "pessimistic";
    private static final String MAP_TRAFFICMODEL_OPTIMISTIC = "optimistic";
    private static final String MAP_TRAFFICMODEL_BESTGUESS = "best_guess";
    private static final String MAP_DEPARTURETIME = "now";
    private static final String MAP_TRANSTMODE = "bus";
    private static final String MAP_REPLYJSON = "json";
    private static final String MAP_REPLYXML = "xml";
    private static final String FRAGMENT_DIRECTIONS = "fragment_directions";
    private static final int TRIP_STARTED=1;
    private static final int TRIP_NOT_STARTED=0;
    private static int TRIP_STATUS=TRIP_NOT_STARTED;
    private SupportMapFragment mapFragment;
    private boolean menuStatus = false;
    private FloatingActionButton mNavigationFloatingActionButton;
    private Intent navigationIntent;
    private String mDestination=new String();
    String url;
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
    private int locationCount = 0;
    private boolean isDirectionListOpen;
    private Fragment mNavigationFragment;
    private RouteSession mRouteSession;
    private ParseAddress mParseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfragment); //R.id.mapfragment
        mRouteSession = RouteSession.getInstance();
        int pos=(int)getIntent().getExtras().get("position");
        mParseAddress = new ParseAddress();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mDestination = getParsedAddress(mRouteSession.getmOrderList().get(pos).getAddress());
        checkpermission();
    }

    public String getParsedAddress(Address address){
        String addressStr = mParseAddress.getProcessedaddress(address.getLine1())
                + "," + mParseAddress.getProcessedaddress(address.getLine2())
                + "," + mParseAddress.getProcessedaddress(address.getCity())
                + "," + mParseAddress.getProcessedaddress(address.getState());

        return addressStr;
    }

    public void checkpermission()
    {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},Constants.PERMISSION_ACCESS_LOCATION);
        }
        else
        {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Location location = mLocationManager.getLastKnownLocation(Constants.LOCATION_PROVIDER);
                if (location != null)
                    fetchMapData(location);
                else
                {
                    location = mLocationManager.getLastKnownLocation(Constants.NETWORK_LOCATION_PROVIDER);
                    if(location!=null)
                        fetchMapData(location);
                    else
                    {
                            Toast.makeText(this,"Unable to fetch location",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(viewIntent, Constants.LOCATION_SETTINGS_ACTION);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==Constants.LOCATION_SETTINGS_ACTION)
        {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                checkpermission();
            }
            else {
                Toast.makeText(this, "Please turn on Location services", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void fetchMapData(Location location)
    {
        if (location != null) {
            url = Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                    + "key=" + Constants.GOOGLE_MAP_SERVER_KEY
                    + "&origin=" + location.getLatitude() + "," + location.getLongitude()
                    + "&destination=" + mDestination
                    + "&departure_time=" + Constants.MAP_DEPARTURETIME
                    + "&traffic_model=" + Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                    + "&mode=" + Constants.MAP_TRANSTMODE;
            mapDataProgress = new ProgressDialog(this);
            mapDataProgress.setMessage("Loading...");
            mapDataProgress.setTitle("Map");
            mapDataProgress.show();
            JsonObjectRequest request = CargoCity.getmInstance().getGeneralRequest(
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("test", response.toString());
                    //Use the routes to draw polyline on map
                    routes = new ParseDirections(response).getRoutes();
                    //pass this bundle to extract the navigation instructions
                    sendToNavigationFragmentBundle = new Bundle();
                    sendToNavigationFragmentBundle.putString("mapData",response.toString());
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
            Toast.makeText(this, "Error fethcing location", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
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
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 14));
                googleMap.addMarker(markerA);
                lineOptions.addAll(points);
                googleMap.addMarker(markerB);
            }
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
            TYPE = 1;
            mapDataProgress.cancel();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 14.0f));
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
    public void onBackPressed()
    {
        if(TRIP_STATUS==TRIP_NOT_STARTED)
            super.onBackPressed();
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
                TRIP_STATUS=TRIP_STARTED;
                this.invalidateOptionsMenu();
                break;

            case R.id.action_navigation_instruction:
                if(isDirectionListOpen) {
                    isDirectionListOpen = false;
                    NavigationInstructionFragment navFragment = (NavigationInstructionFragment)
                                    getSupportFragmentManager()
                                    .findFragmentByTag(FRAGMENT_DIRECTIONS);
                    FragmentTransaction ft = getSupportFragmentManager()
                                    .beginTransaction();
                    ft.detach(navFragment);
                    ft.attach(mapFragment);
                    ft.commit();
                }
                else {
                    isDirectionListOpen = true;
                    if(mNavigationFragment == null){
                        mNavigationFragment = new NavigationInstructionFragment();
                        mNavigationFragment.setArguments(sendToNavigationFragmentBundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, mNavigationFragment, FRAGMENT_DIRECTIONS)
                                .commit();
                    }
                    else {
                        NavigationInstructionFragment navFragment = (NavigationInstructionFragment)
                                 getSupportFragmentManager()
                                .findFragmentByTag(FRAGMENT_DIRECTIONS);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(mapFragment);
                        ft.attach(navFragment);
                        ft.commit();
                    }
                }

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
            if(isNetworkAvailable()) {
                trackVehicle();
            }
        }
        return true;
    }

    private boolean isNetworkAvailable()
    {
        NetworkAvailability networkAvailability=new NetworkAvailability(MapActivity.this);
        if(networkAvailability.isNetworkAvailable())
            return true;
        else
            return false;
    }

    private void trackVehicle() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mPrevLocation = mLocationManager.getLastKnownLocation(mLocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 3000, 0, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                if(locationCount!=0)
                {
                    distance=distance+location.distanceTo(mPrevLocation);
                    mPrevLocation=location;
                    mCurrent=location;
                    mapFragment.getMapAsync(MapActivity.this);
                    TYPE=1;
                }
                else
                {
                    mPrevLocation=location;
                    locationCount+=1;
                }

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