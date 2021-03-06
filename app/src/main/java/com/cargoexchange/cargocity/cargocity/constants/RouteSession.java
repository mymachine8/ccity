package com.cargoexchange.cargocity.cargocity.constants;

import com.cargoexchange.cargocity.cargocity.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class RouteSession
{
    private Map<String, String> mOrderStatusList;
    private String OrderId;
    private List<Order> mOrderList;
    private List<String> mDistanceList;
    private List<String> mDurationList;
    private int mMatrixDownloadStatus;
    private String routeId;
    private String vehicleNo;
    private  static RouteSession instance=null;
    private int position = 0;
    private List<String> delayedUploadOrderNoList=new ArrayList();
    private List<String> delayedUploadFileNameList=new ArrayList();
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

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public Map<String, String> getmOrderStatusList() {
        return mOrderStatusList;
    }

    public void setmOrderStatusList(Map<String, String> mOrderStatusList) {
        this.mOrderStatusList = mOrderStatusList;
    }

    public List<Order> getmOrderList() {
        return mOrderList;
    }

    public void setmOrderList(List<Order> mOrderList) {
        this.mOrderList = mOrderList;
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

    public String getOrderStatus(String orderId) {
        return mOrderStatusList.get(orderId);
    }

    public List<String> getmDistanceList() {
        return mDistanceList;
    }

    public void setmDistanceList(List<String> mDistanceList) {
        this.mDistanceList = mDistanceList;
    }

    public List<String> getmDurationList() {
        return mDurationList;
    }

    public void setmDurationList(List<String> mDurationList) {
        this.mDurationList = mDurationList;
    }

    public int getmMatrixDownloadStatus() {
        return mMatrixDownloadStatus;
    }

    public void setmMatrixDownloadStatus(int mMatrixDownloadStatus) {
        this.mMatrixDownloadStatus = mMatrixDownloadStatus;
    }

    public List getDelayedUploadOrderNoList() {
        return delayedUploadOrderNoList;
    }

    public void setDelayedUploadOrderNoList(List delayedUploadOrderNoList) {
        this.delayedUploadOrderNoList = delayedUploadOrderNoList;
    }

    public List getDelayedUploadFileNameList() {
        return delayedUploadFileNameList;
    }

    public void setDelayedUploadFileNameList(List delayedUploadFileNameList) {
        this.delayedUploadFileNameList = delayedUploadFileNameList;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}