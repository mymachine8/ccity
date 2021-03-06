package com.cargoexchange.cargocity.cargocity.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.models.Route;
import com.cargoexchange.cargocity.cargocity.services.LocationService;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseJSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

public class EnterRouteFragment extends Fragment
{

    private EditText mRouteIdText;
    private Button mRouteSubmitBtn;
    private FragmentActivity thisActivity;
    private ProgressDialog mProgressDialog;
    private RouteSession mApplicationSession;
    private String routeId;
    Intent serviceintent;

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
                if (new NetworkAvailability(thisActivity).isNetworkAvailable())
                    routeSubmit();
                else
                    Toast.makeText(thisActivity, "Network Unavailable", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void routeSubmit() {
        routeId = mRouteIdText.getText().toString();
        if (routeId.isEmpty()) {
            mRouteIdText.setError("Please enter Route Id");
            return;
        }
        //mRouteSubmitBtn.setEnabled(true);
        mProgressDialog.show();
        getOrdersForRoute(routeId);

    }

    public void initializeProgressDialog() {
        mProgressDialog = new ProgressDialog(thisActivity,
                R.style.AppTheme);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Fetching Orders...");
    }

    private void getOrdersForRoute(final String routeId){
        String uri = Constants.CARGO_API_BASE_URL + "cityroutes/" + routeId;
            JsonRequest request = CargoCity.getmInstance().getRequest(
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String status;
                            mProgressDialog.hide();
                            try {
                                status = response.getString("status");
                                if (status.equalsIgnoreCase("success")) {
                                    String data = response.getString("data");
                                    Gson gson = new GsonBuilder().create();
                                    Route route = gson.fromJson(data, Route.class);
                                    InputMethodManager inputMethodManager = (InputMethodManager)thisActivity
                                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(thisActivity.getCurrentFocus().getWindowToken(), 0);
                                    onSuccessOrdersList(route);
                                } else {
                                    String error_message = response.getString("message");
                                    Toast toast = Toast.makeText(thisActivity,
                                            error_message, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                Log.e("PARSE_ERROR", e.getMessage().toString());
                            } catch (JsonParseException e) {
                                Log.e("PARSE_ERROR", e.getMessage().toString());
                            } catch (Exception e) {
                                Log.e("GENERAL", e.getMessage().toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.hide();
                            mRouteSubmitBtn.setEnabled(true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                String json = new String(response.data);
                                json = ParseJSON.trimMessage(json, "message");
                                if (json != null) {
                                    displayToastMessage(json);
                                }
                            }
                        }
                    }, uri);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CargoCity.getmInstance().getRequestQueue().add(request);
    }

    private void displayToastMessage(String message) {
        Toast.makeText(thisActivity, message, Toast.LENGTH_LONG).show();
        Log.e("ROUTE", message);
    }

    private void onSuccessOrdersList(Route route) {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            callRouteDetailsFragment(route);
        }
        else
        {
            //TODO: SOME TOAST OR PERMISSION REQUEST
        }
    }
    public void callRouteDetailsFragment(Route route){
        serviceintent=new Intent(thisActivity,LocationService.class);
        thisActivity.startService(serviceintent);
        //TODO:Stop this service on the logout event
        mApplicationSession = RouteSession.getInstance();
        mApplicationSession.setRouteId(routeId);
        mApplicationSession.setVehicleNo(route.getVehicleNumber());
        Fragment routeDetailsFragment = RouteDetailsFragment.newInstance(route);
        thisActivity.getSupportFragmentManager().beginTransaction().replace(R.id.orders_container, routeDetailsFragment).commit();
    }
}