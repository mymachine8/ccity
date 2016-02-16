package com.cargoexchange.cargocity.cargocity.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.OrdersActivity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.CargoSharedPreferences;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.utils.GenerateRequest;
import com.cargoexchange.cargocity.cargocity.utils.NetworkAvailability;
import com.cargoexchange.cargocity.cargocity.utils.ParseJSON;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment
{

    private TextView mUsernameText;
    private TextView mPasswordText;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;
    private Activity thisActivity;
    private RelativeLayout loginLayout;
    String token = new String();
    private NetworkAvailability mNetworkAvailability;
    private NetworkInfo mWifi;
    public LoginFragment()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        thisActivity = getActivity();
        mNetworkAvailability=new NetworkAvailability(thisActivity);
        ConnectivityManager connManager = (ConnectivityManager) thisActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        thisActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loginLayout=(RelativeLayout)view.findViewById(R.id.loginLayout);
        loginLayout.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) thisActivity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(thisActivity.getCurrentFocus().getWindowToken(), 0);
            }
        });
        bindViewVariables(view);
        mLoginButton .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    login();
                }
            }
        });
        return view;
    }

    public void login() {
        if (!validate()) {
            return;
        }

        mLoginButton.setEnabled(false);
        mProgressDialog = new ProgressDialog(thisActivity,
                R.style.AppTheme);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating...");
        mProgressDialog.show();

        String email = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        submitLogin(email, password);

    }

    public boolean isNetworkConnected(){
        if(!mNetworkAvailability.isNetworkAvailable()) {
            Toast.makeText(thisActivity, "Network Unavailable", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validate() {
        boolean valid = true;

        String email = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (email.isEmpty()) {
            mUsernameText.setError("please enter username");
            valid = false;
        } else {
            mUsernameText.setError(null);
        }

        if (password.isEmpty()) {
            mPasswordText.setError("please enter password");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }
        return valid;
    }

    private void bindViewVariables(View view) {
        mLoginButton = (Button) view.findViewById(R.id.btn_login);
        mUsernameText = (TextView) view.findViewById(R.id.input_username);
        mPasswordText = (TextView) view.findViewById(R.id.input_password);
    }

    private void submitLogin(final String username,final String password){
        String uri = Constants.CARGO_API_BASE_URL + "auth/login";
        JSONObject credentialRequestData=new GenerateRequest().getRequest(username,password);
        if(credentialRequestData!=null) {
            JsonRequest request = CargoCity.getmInstance().postLoginRequest(
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String status;

                            try {
                                mLoginButton.setEnabled(true);
                                status = response.getString("status");
                                if (status.equalsIgnoreCase("success")) {
                                    JSONObject tokenObject = response.getJSONObject("token");
                                    token = tokenObject.getString("accessToken");
                                    SharedPreferences.Editor editor = thisActivity.getSharedPreferences(CargoSharedPreferences.MY_PREFS, thisActivity.MODE_PRIVATE).edit();
                                    editor.putString(CargoSharedPreferences.PREFERENCE_USERNAME, username);
                                    editor.putString(CargoSharedPreferences.PREFERENCE_ACCESSTOKEN, token);
                                    editor.commit();
                                    mProgressDialog.hide();
                                    Intent i = new Intent(thisActivity, OrdersActivity.class);
                                    startActivity(i);

                                } else {
                                    mProgressDialog.hide();
                                    displayToastMessage(response.getString("message"));
                                }
                            } catch (JSONException e) {
                                mProgressDialog.hide();
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.hide();
                            mLoginButton.setEnabled(true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                     String json = new String(response.data);
                                        json = ParseJSON.trimMessage(json, "message");
                                        if (json != null) displayToastMessage(json);
                                }
                            else {
                                displayToastMessage("Network error, please try later");
                            }
                            }
                    }, uri, credentialRequestData);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CargoCity.getmInstance().getRequestQueue().add(request);
        }
    }

    private void displayToastMessage(String message) {
        Toast.makeText(thisActivity, message, Toast.LENGTH_LONG).show();
        Log.e("LOGIN", message);
    }
}

