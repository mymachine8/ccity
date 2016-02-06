package com.cargoexchange.cargocity.cargocity.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.models.Route;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteDetailsFragment extends Fragment {
    private static final String ROUTE_KEY = "route_key";
    private Route mRoute;
    private TextView routeIdTextView;
    private TextView routeNameTextView;
    private TextView vehicleNumberTextView;
    private TextView driverNameTextView;
    private TextView ordersCountTextView;
    private Button proceedBtn;

    public static RouteDetailsFragment newInstance(Route route) {
        RouteDetailsFragment fragment = new RouteDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ROUTE_KEY, route);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoute = (Route) getArguments().getSerializable(
                ROUTE_KEY);
        View view = inflater.inflate(R.layout.fragment_route_details, container, false);
        bindViewVariables(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setViewVariables();
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = OrdersListFragment.newInstance(mRoute);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.orders_container, fragment).commit();
            }
        });
    }

    private void bindViewVariables(View view){
        routeIdTextView = (TextView) view.findViewById(R.id.routeid_textview);
        routeNameTextView = (TextView) view.findViewById(R.id.routename_textview);
        vehicleNumberTextView = (TextView) view.findViewById(R.id.vehiclenumber_textview);
        driverNameTextView = (TextView) view.findViewById(R.id.drivername_textview);
        ordersCountTextView = (TextView) view.findViewById(R.id.numberoforder_textview);
        proceedBtn = (Button) view.findViewById(R.id.goto_orders_btn);
    }

    private void setViewVariables(){
            routeIdTextView.setText(Integer.toString(mRoute.getRouteId()));
            routeNameTextView.setText(mRoute.getName());
            vehicleNumberTextView.setText(mRoute.getVehicleNumber());
            driverNameTextView.setText(mRoute.getDriverName());
            ordersCountTextView.setText(Integer.toString(mRoute.getOrderList().size()));
    }

}
