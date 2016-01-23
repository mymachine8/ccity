package com.cargoexchange.cargocity.cargocity.fragments;


import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.services.LocationService;

public class EnterRouteFragment extends Fragment
{

    private EditText mRouteIdText;
    private Button mRouteSubmitBtn;
    private FragmentActivity thisActivity;
    private ProgressDialog mProgressDialog;
    private RouteSession mApplicationSession;
    private String routeId;

    public EnterRouteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_route, container, false);
        thisActivity = getActivity();
        initializeProgressDialog();
        mRouteIdText = (EditText) view.findViewById(R.id.input_route_id);
        mRouteSubmitBtn = (Button) view.findViewById(R.id.btn_route);
        mRouteSubmitBtn .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                routeSubmit();
            }
        });
        return view;
    }

    public void routeSubmit() {
        routeId = mRouteIdText.getText().toString();
        if (routeId.isEmpty()) {
            mRouteIdText.setError("Please enter Route Id");
            return;
        }else{
            mRouteIdText.setError("");
        }
        mRouteSubmitBtn.setEnabled(false);
        mProgressDialog.show();
        new FetchOrdersTask().execute(routeId);

    }

    public void initializeProgressDialog() {
        mProgressDialog = new ProgressDialog(thisActivity,
                R.style.AppTheme);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Fetching Orders...");
    }

    private class FetchOrdersTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String routeId = params[0];
            String ordersUri = Constants.CARGO_API_BASE_URL + "/route";
            //TODO: Write  to get data from server
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
            mProgressDialog.dismiss();
            if (success)
            {
                Intent serviceintent=new Intent(thisActivity,LocationService.class);
                thisActivity.startService(serviceintent);
                //TODO:Stop this service on the logout event
                mApplicationSession= RouteSession.getInstance();
                mApplicationSession.setRouteId(routeId);
                Fragment ordersListFragment = new OrdersListFragment() ;
                thisActivity.getSupportFragmentManager().beginTransaction().replace(R.id.orders_container, ordersListFragment).commit();

            }

        }
    }
}
