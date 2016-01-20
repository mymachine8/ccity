package com.cargoexchange.cargocity.cargocity.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cargoexchange.cargocity.cargocity.MapActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.adapters.OrderDetailsAdapter;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.models.Customer;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.utils.GenerateUrl;
import com.cargoexchange.cargocity.cargocity.utils.RecyclerItemClickListener;
import com.cargoexchange.cargocity.cargocity.models.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrdersListFragment extends Fragment {
    private RecyclerView mOrdersListFragmentRecycler;
    private RecyclerView.LayoutManager mOrdersListLayoutManager;
    private LocationManager mLocationManager;
    private Location mLocation;
    private List<Order> mOrdersList;
    private OrderDetailsAdapter mOrderDetailsAdapter;
    private RouteSession mRouteSession;

    private FragmentActivity thisActivity;

    public OrdersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_orders_list, container, false);
        thisActivity=getActivity();
        mLocationManager=(LocationManager)thisActivity.getSystemService(Context.LOCATION_SERVICE);
        mOrdersListFragmentRecycler=(RecyclerView)view.findViewById(R.id.recylerview);
        mOrdersListLayoutManager=new LinearLayoutManager(thisActivity,LinearLayoutManager.VERTICAL,false);
        mOrdersListFragmentRecycler.setLayoutManager(mOrdersListLayoutManager);
        mOrdersList=new ArrayList<>();
        mOrdersList.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"), null));
        mOrdersList.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"),null));
        mOrdersList.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"),null));

        mRouteSession = RouteSession.getInstance();
        Map<String, Integer> orderStatusList = new HashMap<String, Integer>();
        for(Order order : mOrdersList) {
            orderStatusList.put(order.getOrderId(), OrderStatus.PENDING_DELIVERY);
        }
        mRouteSession.setmOrderStatusList(orderStatusList);
        mOrderDetailsAdapter = new OrderDetailsAdapter(mOrdersList);
        mOrdersListFragmentRecycler.setAdapter(mOrderDetailsAdapter);
        mOrdersListFragmentRecycler.addOnItemTouchListener(new RecyclerItemClickListener(thisActivity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
             public void onItemClick(View view, int position) {
             if (mRouteSession.getOrderStatus(mOrderDetailsAdapter.getItem(position).getOrderId()) == OrderStatus.PENDING_DELIVERY) {
                    Intent mapIntent = new Intent(thisActivity, MapActivity.class);
                    startActivity(mapIntent);
                //TODO:use a singleton class to keep track of the orders completed and according disable intents to next activity
                mLocation=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                new GenerateUrl(mLocation);
                }
            }
        }));

        //TODO:Download the matrix Data and parse it;
        return view;
    }
}
