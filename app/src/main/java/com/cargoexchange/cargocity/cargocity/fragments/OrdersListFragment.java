package com.cargoexchange.cargocity.cargocity.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.adapters.OrderDetailsAdapter;
import com.cargoexchange.cargocity.cargocity.models.Address;
import com.cargoexchange.cargocity.cargocity.models.Customer;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.models.OrderItem;

import java.util.ArrayList;
import java.util.List;


public class OrdersListFragment extends Fragment {
    private RecyclerView mOrdersListFragmentRecycler;
    private RecyclerView.LayoutManager mOrdersListLayoutManager;

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
        mOrdersListFragmentRecycler=(RecyclerView)view.findViewById(R.id.recylerview);
        mOrdersListLayoutManager=new LinearLayoutManager(thisActivity,LinearLayoutManager.VERTICAL,false);
        mOrdersListFragmentRecycler.setLayoutManager(mOrdersListLayoutManager);
        List<Order> dummyData=new ArrayList<>();
        dummyData.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"),null));
        dummyData.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"),null));
        dummyData.add(new Order("ABC", new Customer("Somesh", "NA", "Mohan", "abc@xyz.com", "NA", "NA"), new Address("HOUSE NO 123", "ROAD NO 21", "JUBILEE HILLS", "Hyderabad", "NA", "Andhra Pradesh"),null));
        mOrdersListFragmentRecycler.setAdapter(new OrderDetailsAdapter(dummyData));
        return view;
    }
}
