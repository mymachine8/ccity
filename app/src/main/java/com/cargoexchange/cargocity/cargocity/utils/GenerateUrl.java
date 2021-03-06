package com.cargoexchange.cargocity.cargocity.utils;

import android.location.Location;
import android.util.Log;

import com.cargoexchange.cargocity.cargocity.constants.Constants;
import com.cargoexchange.cargocity.cargocity.constants.OrderStatus;
import com.cargoexchange.cargocity.cargocity.constants.RouteSession;
import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 20/1/16.
 */
public class GenerateUrl
{
    Location mLocation;
    private Map<String,Integer> mOrderStatus;
    private List<Order> mOrderList;
    private RouteSession mRouteSession;

    public String getDestination() {
        return Destination;
    }

    private String murl=new String();
    private String Destination;

    public GenerateUrl(Location mLocation)
    {
        this.mLocation=mLocation;
        generate();
    }

    public String getMurl() {
        return murl;
    }

    public void generate()
    {
        String mHouseno;
        String mAddressLine1;
        String mAddressLine2;
        String mCity;
        String mState;
        List<String> mTemp=new ArrayList<>();
        String mDestination=new String();
        ParseAddress mParseAddress=new ParseAddress();
        mRouteSession=RouteSession.getInstance();
        //mOrderStatus=mRouteSession.getmOrderStatusList();
        mOrderList=mRouteSession.getmOrderList();
        int sizeOrders=mOrderList.size();
        for(int i=0;i<sizeOrders;i++)
        {
            //if(mOrderList.get(i).getMstatus()== OrderStatus.PENDING_DELIVERY)
            //{
                mAddressLine1 = mParseAddress.getProcessedaddress(mOrderList.get(i).getAddress().getLine1());
                mAddressLine2 = mParseAddress.getProcessedaddress(mOrderList.get(i).getAddress().getLine2());
                mCity = mOrderList.get(i).getAddress().getCity();
                mState = mParseAddress.getProcessedaddress(mOrderList.get(i).getAddress().getState());
                Destination=(mAddressLine1+mAddressLine2+mCity+mState);
                mTemp.add(mAddressLine1+","+mAddressLine2+","+mCity+","+mState);
            //}
        }
        for(int i=0;i<mTemp.size()-1;i++)
        {
            mDestination=mDestination+mTemp.get(i)+"|";
        }
        mDestination=mDestination+mTemp.get(mTemp.size()-1);

        murl= Constants.GOOGLE_MAP_MATRIX_API_BASE_URL+"key="+Constants.GOOGLE_MAP_SERVER_KEY+"&departure_time="+Constants.MAP_DEPARTURETIME+"&traffic_model="+Constants.MAP_TRAFFICMODEL_PESSIMISTIC+"&mode="+Constants.MAP_TRANSTMODE+"&origins="+mLocation.getLatitude()+","+mLocation.getLongitude()+"&destinations="+mDestination;
        Log.d("URL=",murl);
    }
}
