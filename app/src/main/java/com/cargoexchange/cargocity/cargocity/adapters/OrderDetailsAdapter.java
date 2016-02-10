package com.cargoexchange.cargocity.cargocity.adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>
{
    private List<Order> orderDetails = new ArrayList<>();
    private RouteSession mRouteSession;
    private static OrderItemClickListener mItemClickListener;
    private Fragment mFragmentInstance;
    private Context thisContext;
    final MarkerOptions markerA = new MarkerOptions();
    final MarkerOptions markerB = new MarkerOptions();
    ArrayList<LatLng> points;
    LatLng start;
    PolylineOptions lineOptions;
    private LocationManager mLocationManager;
    private String mDestination;

    private MylocationListener locationListener;

    public OrderDetailsAdapter(List<Order> orderDetails, Fragment fragment) {
        mFragmentInstance = fragment;
        thisContext = fragment.getContext();
        this.orderDetails.clear();

        this.orderDetails.addAll(orderDetails);
        mLocationManager = (LocationManager) ((OrdersListFragment) mFragmentInstance)
                .getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public void setOnItemClickListener(OrderItemClickListener careClickListener) {
        this.mItemClickListener = careClickListener;
    }

    public void updateData(List<Order> orderDetails) {
        this.orderDetails.clear();
        this.orderDetails.addAll(orderDetails);
        notifyDataSetChanged();
    }

    public OrderDetailsAdapter()
    {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
        final View v = layoutInflator.inflate(R.layout.row_orderdetails, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mSmallMap.onCreate(null);
        holder.mSmallMap.onResume();
        holder.mSmallMap.setTag(holder);

        String productsCSV=new String();
        mRouteSession = RouteSession.getInstance();
        Order order = orderDetails.get(position);
        holder.mName.setText(order.getName());
        holder.mItems1.setText(order.getItems().get(0));
        holder.mItems2.setText(order.getItems().get(1));
        holder.mAddressLine1.setText(order.getAddress().getLine1());
        holder.mAddressLine2.setText(order.getAddress().getLine2());
        holder.mExtraName.setText(order.getName());
        holder.mExtraOrderno.setText(order.getOrderId());
        holder.mExtraPhone.setText(order.getPhones().get(0).getNumber());
        holder.mExtraEmail.setText(order.getMailId());
        holder.mExtraOrderno.setText(order.getOrderId());
        for(int i=0;i<order.getItems().size();i++)
        {
            productsCSV=productsCSV+(order.getItems().get(i))+",";
        }
        productsCSV=productsCSV.substring(0,productsCSV.length()-1);
        holder.mExtraProducts.setText(productsCSV);

        if(order.getDeliveryStatus().equalsIgnoreCase(OrderStatus.DELIVERED))
        {
                holder.mStatusImage.setBackground(ContextCompat.getDrawable(thisContext,R.drawable.circular_button_truck_delivered));
                holder.mStatusImage.setClickable(false);
        }
        else if(order.getDeliveryStatus().equalsIgnoreCase(OrderStatus.DELIVERY_FAILED))
        {
                holder.mStatusImage.setBackground(ContextCompat.getDrawable(thisContext, R.drawable.circular_button_truck_returned));
                holder.mStatusImage.setClickable(false);
        }

        if (mRouteSession.getmMatrixDownloadStatus() == 1) {
            if (order.getDeliveryStatus().equalsIgnoreCase(OrderStatus.IN_TRANSIT))
            {
                holder.mDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mTime.setText(mRouteSession.getmDurationList().get(position));
                holder.mExtraDistance.setText(mRouteSession.getmDistanceList().get(position));
                holder.mExtraTime.setText(mRouteSession.getmDurationList().get(position));

            }
            else
            {
                holder.mStatusImage.setClickable(false);
                    if(order.getDeliveryStatus().equalsIgnoreCase(OrderStatus.DELIVERED)) {
                        holder.mStatusImage.setBackground(ContextCompat.getDrawable(thisContext, R.drawable.circular_button_truck_delivered));
                    }
                    else
                    {
                            holder.mStatusImage.setBackground(ContextCompat.getDrawable(thisContext, R.drawable.circular_button_truck_returned));
                    }
            }

        }
        getDestination(mRouteSession, position);
        holder.mExtraAddress.setText(orderDetails.get(position).getAddress().getLine1()+" , "
                +orderDetails.get(position).getAddress().getLine2()+" , "
                +orderDetails.get(position).getAddress().getCity()+" , "
                +orderDetails.get(position).getAddress().getState());

        if (ActivityCompat.checkSelfPermission(mFragmentInstance.getActivity()
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationListener = new MylocationListener(holder,position,this);
        Location location = mLocationManager.getLastKnownLocation(Constants.LOCATION_PROVIDER);
        if(location!=null)
            fetchMapData(holder,location,position,this);
        else {
            mLocationManager.requestSingleUpdate(Constants.LOCATION_PROVIDER,locationListener,null);
        }
        //getLocation();

        holder.itemView.setOnClickListener(holder);

        if(mRouteSession.getmOrderList().get(position).getDeliveryStatus()
                .equalsIgnoreCase(OrderStatus.IN_TRANSIT)) {
            holder.mStatusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickFullScreenMap(v, position);
                }
            });
        }
        else
            holder.mStatusImage.setClickable(false);

        holder.mExtraStatusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OrdersListFragment) mFragmentInstance).onClickFullScreenMap(v, position);
            }
        });

        if(mRouteSession.getmOrderList().get(position).getDeliveryStatus().equalsIgnoreCase(
                OrderStatus.IN_TRANSIT)) {
            holder.mCallCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OrdersListFragment) mFragmentInstance).onClickCallExecutive(v, position);
                }
            });
        }
        else
            holder.mCallCustomer.setClickable(false);

        holder.mExtraCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OrdersListFragment) mFragmentInstance).onClickCallExecutive(v, position);
            }
        });
    }
    private class MylocationListener implements LocationListener
    {
        ViewHolder holder;
        int position;
        OrderDetailsAdapter thisInstance;
        public MylocationListener(ViewHolder holder,int position,OrderDetailsAdapter thisInstance)
        {
            this.holder = holder;
            this.position=position;
            this.thisInstance=thisInstance;
        }
    @Override
        public void onLocationChanged(Location location)
        {
            fetchMapData(holder,location,position,thisInstance);

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

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public Order getItem(int position) {
        return orderDetails.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,OnMapReadyCallback
    {
        TextView mOrderno;

        TextView mName;

        TextView mAddressLine1, mAddressLine2;

        TextView mAddressLocality;

        TextView mAddressLandmark;

        TextView mCity;

        TextView mItems1, mItems2;

        ImageView mCallImage;

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

        ImageButton mCallCustomer;

        ImageButton mExtraCallCustomer;

        ImageButton mStatusImage;

        ImageButton mExtraStatusImage;

        MapView mSmallMap;
        private List<List<HashMap<String, String>>> routes;


        public ViewHolder(View itemView) {

            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.nameedittext);

            mAddressLine1 = (TextView) itemView.findViewById(R.id.Line1addressedittext);

            mAddressLine2 = (TextView) itemView.findViewById(R.id.Line2addressedittext);

            mItems1 = (TextView) itemView.findViewById(R.id.itemedittext1);

            mItems2 = (TextView) itemView.findViewById(R.id.itemedittext2);

            mDistance = (TextView) itemView.findViewById(R.id.distancetextview);

            mTime = (TextView) itemView.findViewById(R.id.timetextview);

            mStatusImage = (ImageButton) itemView.findViewById(R.id.orderstatusimage);

            mExtraName = (TextView) itemView.findViewById(R.id.nametextview);

            mExtraExpectedTime = (TextView) itemView.findViewById(R.id.expectedtimetextview);

            mExtraPhone = (TextView) itemView.findViewById(R.id.phonetextview);

            mExtraEmail = (TextView) itemView.findViewById(R.id.emailtextview);

            mExtraDistance = (TextView) itemView.findViewById(R.id.Extradistancetextview);

            mExtraTime = (TextView) itemView.findViewById(R.id.Extratimetextview);

            mCallCustomer = (ImageButton) itemView.findViewById(R.id.CallActionFloatingActionButton);

            mExtraCallCustomer=(ImageButton)itemView.findViewById(R.id.ExtraCallActionFloatingActionButton);

            mExtraAddress=(TextView)itemView.findViewById(R.id.Extraaddresstextview);

            mExtraProducts=(TextView)itemView.findViewById(R.id.Extraproductstextview);

            mExtraOrderno=(TextView)itemView.findViewById(R.id.Extraordernotextview);

            mExtraStatusImage=(ImageButton)itemView.findViewById(R.id.Extraorderstatusimage);

            mSmallMap = (MapView) itemView.findViewById(R.id.mapfragment);
            if(mSmallMap!=null) {
                mSmallMap.onCreate(null);
                mSmallMap.onResume();
            }

        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public void onMapReady(GoogleMap googleMap)
        {

            googleMap.clear();

            //MapsInitializer.initialize(null);
            for (int i = 0; i < routes.size(); i++)
            {
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
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 14));
                googleMap.addMarker(markerA);
                lineOptions.addAll(points);
                googleMap.addMarker(markerB);
            }
            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start ,12.0f));
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(start)
                    .bearing(-120)
                    .tilt(85.5f)
                    .zoom(12.0f)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

        }
    }

    public interface OrderItemClickListener {
        public void onItemClick(int position, View v);
    }


    public void fetchMapData(final ViewHolder mholder,Location location,int position,OrderDetailsAdapter thisInstance)
    {

        if (location != null) {
            String url = Constants.GOOGLE_MAP_DIRECTIONS_API_BASE_URL
                    + "key=" + Constants.GOOGLE_MAP_SERVER_KEY
                    + "&origin=" + location.getLatitude() + "," + location.getLongitude()
                    + "&destination=" + mDestination
                    + "&departure_time=" + Constants.MAP_DEPARTURETIME
                    + "&traffic_model=" + Constants.MAP_TRAFFICMODEL_PESSIMISTIC
                    + "&mode=" + Constants.MAP_TRANSTMODE;

            MyResponseListener myResponseListener = new MyResponseListener(mholder,position,thisInstance);
            JsonObjectRequest request = CargoCity.getmInstance().getGeneralRequest(myResponseListener, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(thisActivity, "Error!Please try again", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }, url);
            CargoCity.getmInstance().getRequestQueue().add(request);
        }
    }

    private class MyResponseListener implements Response.Listener<JSONObject> {
        private ViewHolder holder;
        private int position;
        private OrderDetailsAdapter thisInstance;
        public MyResponseListener(ViewHolder holder,int position,OrderDetailsAdapter thisInstance) {
            this.holder = holder;
            this.position=position;
            this.thisInstance=thisInstance;
        }
        @Override
        public void onResponse(JSONObject response) {
            Log.d("new", response.toString());
            //Use the routes to draw polyline on map
            holder.routes=null;
            holder.routes = new ParseDirections(response).getRoutes();
            //TODO:pass this bundle to extract the navigation instructions

            if(holder==holder.mSmallMap.getTag())
                holder.mSmallMap.getMapAsync(holder);

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


