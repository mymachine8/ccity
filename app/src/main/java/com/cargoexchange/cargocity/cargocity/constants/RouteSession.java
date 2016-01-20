package com.cargoexchange.cargocity.cargocity.constants;

import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.List;
import java.util.Map;
public class RouteSession
{
    private Map<String, Integer> mOrderStatusList;
    private String routeId;
    private  static RouteSession instance=null;
    private int position = 0;
    //Prevents instantiation while object creation
    private void SingletonOrders()
    {

    }
    public static RouteSession getInstance()
    {
        if(instance==null) {
            instance = new RouteSession();
        }
        return instance;
    }

    public Map<String, Integer> getmOrderStatusList() {
        return mOrderStatusList;
    }

    public void setmOrderStatusList(Map<String, Integer> mOrderStatusList) {
        this.mOrderStatusList = mOrderStatusList;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public String getRouteId()
    {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public static void setInstance(RouteSession instance) {
        RouteSession.instance = instance;
    }

    public int getOrderStatus(String orderId) {
        return mOrderStatusList.get(orderId);
    }
}