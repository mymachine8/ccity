package com.cargoexchange.cargocity.cargocity.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.DeliveryFeedbackActivity;
import com.cargoexchange.cargocity.cargocity.MapActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.adapters.OrderDetailsAdapter;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.models.Route;
import com.cargoexchange.cargocity.cargocity.utils.AnimationHelper;
import com.cargoexchange.cargocity.cargocity.utils.GenerateUrl;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseDistanceMatrix;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrdersListFragment extends Fragment
{
    private static String ORDERS_LIST_KEY = "orders_list_key";

    private RecyclerView mOrdersListFragmentRecycler;

    private RecyclerView.LayoutManager mOrdersListLayoutManager;

    private LocationManager mLocationManager;

    private List<Order> mOrdersList;

    private Route mRoute;

    private OrderDetailsAdapter mOrderDetailsAdapter;

    private RouteSession mRouteSession;

    private ProgressDialog processData;

    private Location mLastKnownLocation;

    List<String> mDistanceList;

    List<String> mDurationList;

    private final int CARD_EXPANDED=1;

    private final int CARD_COMPACT=0;

    private int cardStatus=0;

    private Fragment thisFragment;

    private FragmentActivity thisActivity;

    private OrdersListFragment context;

    private boolean GPS_ENABLED_IN_APP=false;

    public OrdersListFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Route route) {

        OrdersListFragment fragment = new OrdersListFragment();

        Bundle bundle = new Bundle();

        bundle.putSerializable(ORDERS_LIST_KEY, route);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRouteSession = RouteSession.getInstance();

        mOrdersList = mRouteSession.getmOrderList();

        thisFragment = this;

        context=this;

        if (mOrdersList == null || mOrdersList.size() == 0)
        {
            mRoute = (Route) getArguments().getSerializable(ORDERS_LIST_KEY);

            mOrdersList = mRoute.getOrderList();

            Map<String, String> orderStatusList = new HashMap<String, String>();

            for (Order order : mOrdersList) {
                orderStatusList.put(order.getOrderId(), OrderStatus.IN_TRANSIT);
            }
            mRouteSession.setmOrderStatusList(orderStatusList);

            mRouteSession.setmOrderList(mOrdersList);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        thisActivity = getActivity();

        mLocationManager = (LocationManager) thisActivity.getSystemService(Context.LOCATION_SERVICE);

        mOrdersListFragmentRecycler = (RecyclerView) view.findViewById(R.id.recylerview);

        mOrdersListLayoutManager = new LinearLayoutManager(thisActivity, LinearLayoutManager.VERTICAL, false);

        mOrdersListFragmentRecycler.setLayoutManager(mOrdersListLayoutManager);

        //mOrderDetailsCard=(CardView)view.findViewById(R.id.orderdetailscardview);

        processData=new ProgressDialog(getActivity());

        processData.setMessage("Loading data");

        processData.setTitle("Orders");

        mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList, thisFragment);

        mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);

        registerClickEvents();

        int hasLocationfinePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        int hasLocationCoarsePermission=ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasLocationfinePermission == PackageManager.PERMISSION_GRANTED && hasLocationCoarsePermission==PackageManager.PERMISSION_GRANTED)
        {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                setData();
            }
            else
            {
                showEnableGPSDialog();
            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSION_ACCESS_LOCATION);
        }
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==Constants.LOCATION_SETTINGS_ACTION)
        {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                GPS_ENABLED_IN_APP=true;
                setData();
            }
            else
            {
                Toast.makeText(thisActivity, "Please turn on Location services", Toast.LENGTH_SHORT).show();
                setOldData();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case Constants.PERMISSION_ACCESS_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    int hasLocationfinePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                    int hasLocationCoarsePermission=ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (hasLocationfinePermission == PackageManager.PERMISSION_GRANTED && hasLocationCoarsePermission==PackageManager.PERMISSION_GRANTED)
                    {
                        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                        {
                            setData();
                        }
                        else
                        {
                            showEnableGPSDialog();
                        }
                    }
                }
                else
                {
                    Log.d("outgoing","hello");

                    System.runFinalization();

                    Intent startMain = new Intent(Intent.ACTION_MAIN);

                    startMain.addCategory(Intent.CATEGORY_HOME);

                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(startMain);

                }
                return;
            }
        }
    }



    public class mLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            download(location);
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
    public void setData()
    {
        Location location=mLocationManager.getLastKnownLocation(Constants.NETWORK_LOCATION_PROVIDER);

        if(location!=null)
        {
            if (mLastKnownLocation != null && !GPS_ENABLED_IN_APP) {
                if (location.getTime() - mLastKnownLocation.getTime() > Constants.TWO_MINUTES)
                {
                    mLastKnownLocation = location;
                    download(location);
                }
                else
                {
                    //Retrieve the back stack data
                    setOldData();
                }
            } else {
                GPS_ENABLED_IN_APP=false;
                mLastKnownLocation = location;
                download(location);
            }
        }
        else
        {
            setOldData();
        }
        int haslocationfinePermission = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION);

        if (haslocationfinePermission == PackageManager.PERMISSION_GRANTED)
        {
            mLocationManager.requestSingleUpdate(Constants.LOCATION_PROVIDER, new mLocationListener(), null);
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_ACCESS_LOCATION);
        }
    }


    public void setOldData()
    {
        mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList, thisFragment);

        mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);

        registerClickEvents();
    }

    public void showEnableGPSDialog()
    {
        AlertDialog.Builder enableGPS;enableGPS=new AlertDialog.Builder(thisActivity);

        enableGPS.setMessage("Please Enable Location");

        enableGPS.setTitle("Location Alert");

        enableGPS.setPositiveButton("ENABLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivityForResult(viewIntent, Constants.LOCATION_SETTINGS_ACTION);
            }
        });

        enableGPS.create();

        enableGPS.show();
    }
    public void onCardClickAction(View view,int position)
    {
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (mRouteSession.getmOrderList().get(position).getDeliveryStatus().equals(OrderStatus.IN_TRANSIT))
            {
                //Intent mapIntent = new Intent(thisActivity, DeliveryFeedbackActivity.class);

                Fragment mExtraDetailsFragment = new ExtraOrderDetailsFragment();

                Bundle mDataForExtraDetailsFragment = new Bundle();

                mDataForExtraDetailsFragment.putInt("position", position);

                mExtraDetailsFragment.setArguments(mDataForExtraDetailsFragment);

                cardStatus=mRouteSession.getmOrderList().get(position).getCardStatus();

                if(cardStatus==CARD_EXPANDED)
                {
                    //slideDown(view);
                    Log.d("response","compact card");
                    new AnimationHelper(view,cardStatus,context,thisActivity,position);

                    mRouteSession.getmOrderList().get(position).setCardStatus(CARD_EXPANDED);


                }
                else
                {
                    //slideUp(view);
                    Log.d("response","compact expanded");

                    new AnimationHelper(view,cardStatus,context,thisActivity,position);

                    mRouteSession.getmOrderList().get(position).setCardStatus(CARD_COMPACT);
                }
                //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
            }
            else
            {
                Toast.makeText(thisActivity, mRouteSession.getmOrderList().get(position).getDeliveryStatus(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            showEnableGPSDialog();
        }
    }


    public void registerClickEvents(){

        mOrderDetailsAdapter.setOnItemClickListener(new OrderDetailsAdapter.OrderItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                onCardClickAction(v, position);
            }
        });
    }

    public void onClickOrderStatus(View v,int position) {

        Log.d("Order_Click", "Clicked on the order status");

        mRouteSession.setPosition(position);

        Intent testintent=new Intent(getActivity(),DeliveryFeedbackActivity.class);

        startActivity(testintent);
    }

    public void onClickCallExecutive(View v,int position){

        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+mRouteSession.getmOrderList().get(position).getPhones().get(0).getCountryCode()+mRouteSession.getmOrderList().get(position).getPhones().get(0).getNumber()));

        startActivity(intent);
    }
    public void onClickFullScreenMap(View v,int position)
    {
        mRouteSession.setPosition(position);

        Intent MapIntent = new Intent(thisActivity, MapActivity.class);

        MapIntent.putExtra("position",position);

        startActivity(MapIntent);
    }

    public void download(Location location)
    {
        NetworkAvailability mNetworkAvailability=new NetworkAvailability(thisActivity);

        processData.show();

        String url=new GenerateUrl(location).getMurl();

        JsonObjectRequest request= CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("response", response.toString());

                processData.dismiss();

                processData.cancel();

                mRouteSession.setmMatrixDownloadStatus(1);

                ParseDistanceMatrix mParseDistanceMatrix = new ParseDistanceMatrix(response);

                mDistanceList = mParseDistanceMatrix.getDistanceList();

                mDurationList = mParseDistanceMatrix.getDurationList();

                mRouteSession.setmDistanceList(mDistanceList);

                mRouteSession.setmDurationList(mDurationList);

                mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "error");

                mRouteSession.setmMatrixDownloadStatus(0);

                mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList,thisFragment);

                mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);

                registerClickEvents();

                processData.dismiss();

                processData.cancel();

                Toast.makeText(thisActivity, "Error Downloading Distance", Toast.LENGTH_SHORT).show();
            }
        }, url);
        if(mNetworkAvailability.isNetworkAvailable())

            CargoCity.getmInstance().getRequestQueue().add(request);
        else
            Toast.makeText(thisActivity,"Network Unavailable",Toast.LENGTH_SHORT).show();
    }
}

