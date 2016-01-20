package com.cargoexchange.cargocity.cargocity.constants;

import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.List;

/**
 * Created by root on 20/1/16.
 */
public class ApplicationSession
{
    private List<Order> mList;
    private int position=0;
    private String routeId;
    private  static ApplicationSession instance=null;
    //Prevents instantiation while object creation
    private void SingletonOrders()
    {

    }
    public static ApplicationSession getInstance()
    {
        if(instance==null) {
            instance = new ApplicationSession();
        }
        return instance;
    }
    public void setOrderList(List<Order> mList)
    {
        this.mList=mList;
    }
    public List<Order> getOrderList()
    {
        return mList;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }
    /*public void setStatus(String status,int position)
    {
        this.mList.get(position).mStatus=status;
    }*/

    public String getRouteId()
    {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}