package com.cargoexchange.cargocity.cargocity.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.models.Customer;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.utils.GenerateUrl;
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
    private OrderDetailsAdapter mOrderDetailsAdapter;
    private RouteSession mRouteSession;
    private CardView mOrderDetailsCard;
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
            mOrdersList.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("Plot No 105a", "Sri Nagar Colony", " ", "Hyderabad", "NA", "Telangana"),items, OrderStatus.PENDING_DELIVERY));
            mOrdersList.add(new Order("ABC", new Customer("Krishna", "NA", "Chaitanya", "def@xyz.com", "NA", "NA"), new Address("Amulya Grand", "Ayappa Society", "Madhapur", "Hyderabad", "NA", "Telangana"),items, OrderStatus.PENDING_DELIVERY));
            mOrdersList.add(new Order("ABC", new Customer("Kinkar", "NA", "Banerji", "xyz@xyz.com", "NA", "NA"), new Address("Arth Design Build", "ROAD NO 21", "Banjara Hills", "Hyderabad", "NA", "Telangana"),items, OrderStatus.PENDING_DELIVERY));
            Map<String, Integer> orderStatusList = new HashMap<String, Integer>();
            for (Order order : mOrdersList) {
                orderStatusList.put(order.getOrderId(), OrderStatus.PENDING_DELIVERY);
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
        JsonObjectRequest request= CargoCity.getmInstance().getProduct(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
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
                        if(mRouteSession.getmOrderList().get(position).getMstatus()==OrderStatus.PENDING_DELIVERY)
                        {
                            //Intent mapIntent = new Intent(thisActivity, MapActivity.class);
                            Fragment mExtraDetailsFragment=new ExtraOrderDetailsFragment();
                            Bundle mDataForExtraDetailsFragment=new Bundle();
                            mDataForExtraDetailsFragment.putInt("position",position);
                            mExtraDetailsFragment.setArguments(mDataForExtraDetailsFragment);
                            thisActivity
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.card_flip_right_in,
                                            R.anim.card_flip_right_out,
                                            R.anim.card_flip_left_in,
                                            R.anim.card_flip_left_out)
                                    .replace(R.id.orders_container,mExtraDetailsFragment)
                                    .addToBackStack(null)
                                    .commit();
                            //mRouteSession.setPosition(position);
                            //startActivity(mapIntent);
                            //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
                        }
                        else
                        {
                            Toast.makeText(thisActivity,"Delivery Completed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                mRouteSession.setmMatrixDownloadStatus(0);
                mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
                mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
                mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.PENDING_DELIVERY) {
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
        return view;
    }
}
