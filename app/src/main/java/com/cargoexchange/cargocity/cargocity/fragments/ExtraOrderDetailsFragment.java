package com.cargoexchange.cargocity.cargocity.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.MapActivity;
import com.cargoexchange.cargocity.cargocity.OrdersActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.utils.ParseAddress;
import com.cargoexchange.cargocity.cargocity.utils.ParseDirections;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

public class ExtraOrderDetailsFragment extends Fragment implements OnMapReadyCallback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView mName;
    private TextView mOrdeNo;
    private TextView mExpectedTime;
    private TextView mPhoneNo;
    private TextView mEmailId;
    private TextView mDistance;
    private TextView mDuration;
    private ListView mItems;
    private ListView mAddress;
    private FloatingActionButton mFullScreen;
    private RouteSession mRouteSesion;
    private int position;
    private ProgressDialog mMapDataProgress;
    private OrdersActivity thisActivity;
    private CardView mMapCard;
    private List<List<HashMap<String, String>>> routes;
    private SupportMapFragment mapFragment;
    final MarkerOptions markerCurrent = new MarkerOptions();
    final MarkerOptions markerA = new MarkerOptions();
    final MarkerOptions markerB = new MarkerOptions();
    ArrayList<LatLng> points;
    LatLng start;
    PolylineOptions lineOptions;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExtraOrderDetailsFragment() {
        // Required empty public constructor
    }

    public static ExtraOrderDetailsFragment newInstance(String param1, String param2) {
        ExtraOrderDetailsFragment fragment = new ExtraOrderDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_order_details, container, false);
        thisActivity=(OrdersActivity)getActivity();
        position = getArguments().getInt("position");
        mRouteSesion = RouteSession.getInstance();
        bindViews(view);
        bindViewsValues();
        return view;
    }

    public void bindViews(View v) {
        mName = (TextView) v.findViewById(R.id.nametextview);
        mOrdeNo = (TextView) v.findViewById(R.id.ordernotextview);
        mExpectedTime = (TextView) v.findViewById(R.id.expectedtimetextview);
        mPhoneNo = (TextView) v.findViewById(R.id.phonetextview);
        mEmailId = (TextView) v.findViewById(R.id.emailtextview);
        mDistance = (TextView) v.findViewById(R.id.distancetextview);
        mDuration = (TextView) v.findViewById(R.id.timetextview);
        mItems=(ListView)v.findViewById(R.id.itemlistlistview);
        mAddress=(ListView)v.findViewById(R.id.addresslistview);
        mFullScreen=(FloatingActionButton)v.findViewById(R.id.FullScreenMapFloatingActionButton);
        mFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("hello","hi");
                Intent MapIntent=new Intent(thisActivity,MapActivity.class);
                startActivity(MapIntent);
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getChildFragmentManager();
                mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapsmallfragment);
                if (mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                }
                bindMap();
            }
        }, 1400);

    }

    public void bindViewsValues() {
        mName.setText(mRouteSesion
                .getmOrderList()
                .get(position)
                .getName());
        mOrdeNo.setText(mRouteSesion
                .getmOrderList()
                .get(position)
                .getOrderId());
        mPhoneNo.setText(mRouteSesion
                .getmOrderList()
                .get(position)
                .getPhones().get(0).getNumber());
        mEmailId.setText(mRouteSesion
                .getmOrderList()
                .get(position)
                .getMailId());
        mDistance.setText(mRouteSesion
                .getmDistanceList()
                .get(position));
        mDuration.setText(mRouteSesion
                .getmDurationList()
                .get(position));

        List<String> items=new ArrayList<>();
        for(int i=0;i<mRouteSesion.getmOrderList().get(position).getItems().size();i++)
        {
            items.add(mRouteSesion
                    .getmOrderList()
                    .get(position)
                    .getItems()
                    .get(i));
        }

        ArrayAdapter mItemsAdapter=new ArrayAdapter(thisActivity,
                R.layout.row_items,
                items);
        mItems.setAdapter(mItemsAdapter);
        items=new ArrayList<>();
        Address maddress=mRouteSesion.getmOrderList().get(position).getAddress();
        if(!maddress.getLine1().equals(null) && !maddress.getLine1().equalsIgnoreCase(" "))
            items.add(maddress.getLine1());
        if(!maddress.getLine2().equals(null) && !maddress.getLine2().equalsIgnoreCase(" ")) {
            if (!maddress.getPincode().equals(null) && !maddress.getPincode().equalsIgnoreCase(" "))
            {
                items.add(maddress.getLine2() + "," + maddress.getPincode());
            }
            else
                items.add(maddress.getLine2());
        }
        if(!maddress.getCity().equals(null) && !maddress.getCity().equalsIgnoreCase(" ")) {
            if (!maddress.getState().equals(null) && !maddress.getState().equalsIgnoreCase(" ")) {
                items.add(maddress.getCity() + "," + maddress.getState());
            } else
                items.add(maddress.getCity());
        }

        ArrayAdapter mAddressAdapter=new ArrayAdapter(thisActivity,
                R.layout.row_items,
                items);

        mAddress.setAdapter(mAddressAdapter);
    }

    public void bindMap() {
        String addressLine1 = new ParseAddress().getProcessedaddress(mRouteSesion
                .getmOrderList()
                .get(position)
                .getAddress()
                .getLine1());
        String addressLine2 = new ParseAddress().getProcessedaddress(mRouteSesion
                .getmOrderList()
                .get(position)
                .getAddress()
                .getLine2());
        String addressCity = new ParseAddress().getProcessedaddress(mRouteSesion
                .getmOrderList()
                .get(position)
                .getAddress()
                .getCity());
        String addressState = new ParseAddress().getProcessedaddress(mRouteSesion
                .getmOrderList()
                .get(position)
                .getAddress()
                .getState());
        String mDestination =  addressLine1 + ","
                + addressLine2 + ","
                + addressCity + ","
                + addressState;

        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String url= Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                +"key=" +Constants.GOOGLE_MAP_SERVER_KEY
                +"&origin="+location.getLatitude()+","+location.getLongitude()
                +"&destination="+mDestination
                +"&departure_time="+Constants.MAP_DEPARTURETIME
                +"&traffic_model="+Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                +"&mode="+Constants.MAP_TRANSTMODE;
        mMapDataProgress=new ProgressDialog(thisActivity);
        mMapDataProgress.setTitle("Map");
        mMapDataProgress.setMessage("loading...");
        mMapDataProgress.show();
        JsonRequest request=CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                routes = new ParseDirections(response).getRoutes();
                mapFragment.getMapAsync(ExtraOrderDetailsFragment.this);

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        },url);
        CargoCity.getmInstance().getRequestQueue().add(request);


    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
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
        mMapDataProgress.cancel();

    }
}
