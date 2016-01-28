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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.MapActivity;
import com.cargoexchange.cargocity.cargocity.OrdersActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.adapters.OrderDetailsAdapter;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.models.Customer;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.models.Route;
import com.cargoexchange.cargocity.cargocity.utils.GenerateUrl;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseDistanceMatrix;
import com.cargoexchange.cargocity.cargocity.utils.RecyclerItemClickListener;
import com.cargoexchange.cargocity.cargocity.models.OrderItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrdersListFragment extends Fragment
{
    private RecyclerView mOrdersListFragmentRecycler;
    private RecyclerView.LayoutManager mOrdersListLayoutManager;
    private LocationManager mLocationManager;
    private Location mLocation;
    private List<Order> mOrdersList;
    private Route mRoute;
    private OrderDetailsAdapter mOrderDetailsAdapter;
    private RouteSession mRouteSession;
    private CardView mOrderDetailsCard;
    private FloatingActionButton mCallCustomer;
    private ProgressDialog processData;
    private Location mLastKnownLocation;
    List<String> mDistanceList;
    List<String> mDurationList;

    private FragmentActivity thisActivity;

    public OrdersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRouteSession = RouteSession.getInstance();
        mOrdersList = mRouteSession.getmOrderList();
        if (mOrdersList == null || mOrdersList.size() == 0) {
            mOrdersList = new ArrayList<>();
            List<OrderItem> items=new ArrayList<>();
            items.add(new OrderItem("123","Lens"));
            items.add(new OrderItem("123","Bike"));
        /*    mOrdersList.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("Sri Nagar Colony", "Khairatabad", "Hyderabad", "NA", "Telangana"),items, OrderStatus.IN_TRANSIT));
            mOrdersList.add(new Order("ABC", new Customer("Krishna", "NA", "Chaitanya", "def@xyz.com", "NA", "NA"), new Address("Ayappa Society", "Madhapur", "Hyderabad", "NA", "Telangana"),items, OrderStatus.IN_TRANSIT));
            mOrdersList.add(new Order("ABC", new Customer("Kinkar", "NA", "Banerji", "xyz@xyz.com", "NA", "NA"), new Address("ROAD NO 21", "Banjara Hills", "Hyderabad", "NA", "Telangana"),items, OrderStatus.IN_TRANSIT));*/
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        thisActivity = getActivity();
        mLocationManager = (LocationManager) thisActivity.getSystemService(Context.LOCATION_SERVICE);
        mOrdersListFragmentRecycler = (RecyclerView) view.findViewById(R.id.recylerview);
        mOrdersListLayoutManager = new LinearLayoutManager(thisActivity, LinearLayoutManager.VERTICAL, false);
        mOrdersListFragmentRecycler.setLayoutManager(mOrdersListLayoutManager);
        mOrderDetailsCard=(CardView)view.findViewById(R.id.orderdetailscardview);
        processData=new ProgressDialog(getActivity());
        processData.setMessage("Loading data");
        processData.setTitle("Orders");

        int hasLocationfinePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasLocationCoarsePermission=ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasLocationfinePermission == PackageManager.PERMISSION_GRANTED && hasLocationCoarsePermission==PackageManager.PERMISSION_GRANTED)
        {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
            if (mLastKnownLocation != null) {
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
                mLastKnownLocation = location;
                download(location);
            }
        }
        else
        {
            setOldData();
        }
        int haslocationfinePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
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
        mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
        mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
        mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                //if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.PENDING_DELIVERY)
                onCardClickAction(view,position);
            }
        }));
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
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (mRouteSession.getmOrderList().get(position).getDeliveryStatus() == OrderStatus.IN_TRANSIT) {
                //Intent mapIntent = new Intent(thisActivity, MapActivity.class);
                Fragment mExtraDetailsFragment = new ExtraOrderDetailsFragment();
                Bundle mDataForExtraDetailsFragment = new Bundle();
                mDataForExtraDetailsFragment.putInt("position", position);
                mExtraDetailsFragment.setArguments(mDataForExtraDetailsFragment);
                mRouteSession.setPosition(position);
                thisActivity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.card_flip_right_in,
                                R.anim.card_flip_right_out,
                                R.anim.card_flip_left_in,
                                R.anim.card_flip_left_out)
                        .replace(R.id.orders_container, mExtraDetailsFragment)
                        .addToBackStack("OrdersState")
                        .commit();
                //mRouteSession.setPosition(position);
                //startActivity(mapIntent);
                //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
            }
            else
            {
                Toast.makeText(thisActivity, "Delivery Completed", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            showEnableGPSDialog();
        }
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
                mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
                mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);

                mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.PENDING_DELIVERY)
                        onCardClickAction(view, position);
                    }
                }));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "error");
                mRouteSession.setmMatrixDownloadStatus(0);
                mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
                mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
                mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onCardClickAction(view, position);
                    }
                }));
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
/*public void something()
{
    if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        //return TODO;
    }
    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    String url=new GenerateUrl(mLocation).getMurl();
    JsonObjectRequest request= CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            mRouteSession.setmMatrixDownloadStatus(1);
            ParseDistanceMatrix mParseDistanceMatrix = new ParseDistanceMatrix(response);
            mDistanceList = mParseDistanceMatrix.getDistanceList();
            mDurationList = mParseDistanceMatrix.getDurationList();
            mRouteSession.setmDistanceList(mDistanceList);
            mRouteSession.setmDurationList(mDurationList);
            mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
            mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
            mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.PENDING_DELIVERY)
                    if (mRouteSession.getmOrderList().get(position).getDeliveryStatus() == OrderStatus.IN_TRANSIT) {
                        //Intent mapIntent = new Intent(thisActivity, MapActivity.class);
                        Fragment mExtraDetailsFragment = new ExtraOrderDetailsFragment();
                        Bundle mDataForExtraDetailsFragment = new Bundle();
                        mDataForExtraDetailsFragment.putInt("position", position);
                        mExtraDetailsFragment.setArguments(mDataForExtraDetailsFragment);
                        thisActivity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.card_flip_right_in,
                                        R.anim.card_flip_right_out,
                                        R.anim.card_flip_left_in,
                                        R.anim.card_flip_left_out)
                                .replace(R.id.orders_container, mExtraDetailsFragment)
                                .addToBackStack(null)
                                .commit();
                        //mRouteSession.setPosition(position);
                        //startActivity(mapIntent);
                        //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
                    } else {
                        Toast.makeText(thisActivity, "Delivery Completed", Toast.LENGTH_SHORT).show();
                    }
                }
            }));
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mRouteSession.setmMatrixDownloadStatus(0);
            mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
            mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
            mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.IN_TRANSIT) {
                        Intent mapIntent = new Intent(thisActivity, MapActivity.class);
                        startActivity(mapIntent);
                        //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
                    }
                }
            }));
            Toast.makeText(thisActivity, "Error Downloading Distance", Toast.LENGTH_SHORT).show();
        }
    }, url);
    CargoCity.getmInstance().getRequestQueue().add(request);
}*/
