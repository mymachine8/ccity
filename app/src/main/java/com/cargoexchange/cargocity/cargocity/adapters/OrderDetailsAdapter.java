package com.cargoexchange.cargocity.cargocity.adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cargoexchange.cargocity.cargocity.CargoCity;
import com.cargoexchange.cargocity.cargocity.R;
import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.fragments.OrdersListFragment;
import com.cargoexchange.cargocity.cargocity.models.Order;
import com.cargoexchange.cargocity.cargocity.utils.ParseAddress;
import com.cargoexchange.cargocity.cargocity.utils.ParseDirections;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 19/1/16.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> implements OnMapReadyCallback {
    private List<Order> orderDetails;
    private RouteSession mRouteSession;
    private static OrderItemClickListener mItemClickListener;
    private Fragment mFragmentInstance;


    private List<List<HashMap<String, String>>> routes;
    final MarkerOptions markerA = new MarkerOptions();
    final MarkerOptions markerB = new MarkerOptions();
    ArrayList<LatLng> points;
    LatLng start;
    PolylineOptions lineOptions;
    private LocationManager mLocationManager;

    private String mDestination;

    public OrderDetailsAdapter(List<Order> orderDetails, Fragment fragment) {
        mFragmentInstance = fragment;
        this.orderDetails = orderDetails;
        mLocationManager = (LocationManager) ((OrdersListFragment) mFragmentInstance).getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    public void setOnItemClickListener(OrderItemClickListener careClickListener) {
        this.mItemClickListener = careClickListener;
    }

    public OrderDetailsAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
        final View v = layoutInflator.inflate(R.layout.row_orderdetails, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mRouteSession = RouteSession.getInstance();
        //holder.mOrderno.setText(orderDetails.get(position).getOrderId());
        holder.mName.setText(orderDetails.get(position).getName());
        holder.mItems1.setText(orderDetails.get(position).getItems().get(0));
        holder.mItems2.setText(orderDetails.get(position).getItems().get(1));
        holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getLine1());
        holder.mAddressLine2.setText(orderDetails.get(position).getAddress().getLine2());
        holder.mExtraName.setText(orderDetails.get(position).getName());
        holder.mExtraOrderno.setText(orderDetails.get(position).getOrderId());
        holder.mExtraPhone.setText(orderDetails.get(position).getPhones().get(0).getNumber());
        holder.mExtraEmail.setText(orderDetails.get(position).getMailId());


        //holder.mAddressLine1.setText(orderDetails.get(position).getAddress().getHouseNumber());
        //holder.mAddressLocality.setText(orderDetails.get(position).getAddress().getAddressLine1());
        //holder.mAddressLandmark.setText(orderDetails.get(position).getAddress().getAddressLine2());
        //holder.mCity.setText(orderDetails.get(position).getAddress().getCity());
        if (mRouteSession.getmMatrixDownloadStatus() == 1) {
            if (mRouteSession.getmOrderList().get(position).getDeliveryStatus().equalsIgnoreCase(OrderStatus.IN_TRANSIT)) {
                holder.mDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mTime.setText(mRouteSession.getmDurationList().get(position));
                holder.mExtraDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mExtraTime.setText(mRouteSession.getmDurationList().get(position));

            } else {
                holder.mStatusImage.setImageResource(R.drawable.ic_tick);
            }
        }
        getDestination(mRouteSession, position);
        holder.mExtraAddress.setText(orderDetails.get(position).getAddress().getLine1()+" , "
                +orderDetails.get(position).getAddress().getLine2()+" , "
                +orderDetails.get(position).getAddress().getCity()+" , "
                +orderDetails.get(position).getAddress().getState());

        if (ActivityCompat.checkSelfPermission(((OrdersListFragment)mFragmentInstance).getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(((OrdersListFragment)mFragmentInstance).getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(Constants.LOCATION_PROVIDER);
        if(location!=null)
            fetchMapData(holder,location);
        else
            mLocationManager.requestSingleUpdate(Constants.LOCATION_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location)
                {
                    fetchMapData(holder,location);
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
            },null);
        //getLocation();
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public Order getItem(int position) {
        return orderDetails.get(position);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //MapsInitializer.initialize(null);
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
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mOrderno;
        TextView mName;
        TextView mAddressLine1, mAddressLine2;
        TextView mAddressLocality;
        TextView mAddressLandmark;
        TextView mCity;
        TextView mItems1, mItems2;
        ImageView mStatusImage, mCallImage;
        TextView mDistance;
        TextView mTime;
        TextView mExtraName;
        TextView mExtraOrderno;
        TextView mExtraExpectedTime;
        TextView mExtraProducts;
        TextView mExtraPhone;
        TextView mExtraEmail;
        TextView mExtraAddress;
        TextView mExtraDistance;
        TextView mExtraTime;
        FloatingActionButton mCallCustomer;
        FloatingActionButton mFullScreenMapFAB;

        MapView mSmallMap;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.nameedittext);
            mAddressLine1 = (TextView) itemView.findViewById(R.id.Line1addressedittext);
            mAddressLine2 = (TextView) itemView.findViewById(R.id.Line2addressedittext);
            mItems1 = (TextView) itemView.findViewById(R.id.itemedittext1);
            mItems2 = (TextView) itemView.findViewById(R.id.itemedittext2);
            mDistance = (TextView) itemView.findViewById(R.id.distancetextview);
            mTime = (TextView) itemView.findViewById(R.id.timetextview);
            mStatusImage = (ImageView) itemView.findViewById(R.id.orderstatusimage);
            mExtraName = (TextView) itemView.findViewById(R.id.nametextview);
            mExtraOrderno = (TextView) itemView.findViewById(R.id.ordernotextview);
            mExtraExpectedTime = (TextView) itemView.findViewById(R.id.expectedtimetextview);
            mExtraPhone = (TextView) itemView.findViewById(R.id.phonetextview);
            mExtraEmail = (TextView) itemView.findViewById(R.id.emailtextview);
            mExtraDistance = (TextView) itemView.findViewById(R.id.Extradistancetextview);
            mExtraTime = (TextView) itemView.findViewById(R.id.Extratimetextview);
            mFullScreenMapFAB = (FloatingActionButton) itemView.findViewById(R.id.mFullScreenMapFloatingActionButton);
            mCallCustomer = (FloatingActionButton) itemView.findViewById(R.id.CallActionFloatingActionButton);
            mExtraAddress=(TextView)itemView.findViewById(R.id.addresstextview);

            mSmallMap = (MapView) itemView.findViewById(R.id.mapfragment);
            mSmallMap.onCreate(null);
            mSmallMap.onResume();

            itemView.setOnClickListener(this);
            mStatusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickOrderStatus(v);
                }
            });
            mCallCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickCallExecutive(v);
                }
            });
            mFullScreenMapFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickFullScreenMap(v);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface OrderItemClickListener {
        public void onItemClick(int position, View v);
    }


    public void fetchMapData(final ViewHolder mholder,Location location)
    {

        if (location != null) {
            String url = Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                    + "key=" + Constants.GOOGLE_MAP_SERVER_KEY
                    + "&origin=" + location.getLatitude() + "," + location.getLongitude()
                    + "&destination=" + mDestination
                    + "&departure_time=" + Constants.MAP_DEPARTURETIME
                    + "&traffic_model=" + Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                    + "&mode=" + Constants.MAP_TRANSTMODE;

            JsonObjectRequest request = CargoCity.getmInstance().getGeneralRequest(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("new", response.toString());
                    //Use the routes to draw polyline on map
                    routes = new ParseDirections(response).getRoutes();
                    //TODO:pass this bundle to extract the navigation instructions
                    //sendToNavigationFragmentBundle = new Bundle();
                    //sendToNavigationFragmentBundle.putString("mapData", response.toString());
                    mholder.mSmallMap.getMapAsync(OrderDetailsAdapter.this);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(thisActivity, "Error!Please try again", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }, url);
            CargoCity.getmInstance().getRequestQueue().add(request);
        }
    }
    public void getDestination(RouteSession mRouteSession,int position)
    {

        String addressLine1 = new ParseAddress().getProcessedaddress(mRouteSession
                .getmOrderList()
                .get(position)
                .getAddress()
                .getLine1());
        String addressLine2 = new ParseAddress().getProcessedaddress(mRouteSession
                .getmOrderList()
                .get(position)
                .getAddress()
                .getLine2());
        String addressCity = new ParseAddress().getProcessedaddress(mRouteSession
                .getmOrderList()
                .get(position)
                .getAddress()
                .getCity());
        String addressState = new ParseAddress().getProcessedaddress(mRouteSession
                .getmOrderList()
                .get(position)
                .getAddress()
                .getState());
        mDestination =  addressLine1 + ","
                + addressLine2 + ","
                + addressCity + ","
                + addressState;

    }
}


