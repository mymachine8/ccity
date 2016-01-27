package com.cargoexchange.cargocity.cargocity.models;

import java.util.List;

/**
 * Created by thespidy on 27/01/16.
 */
public class Route {
    private String name;
    private int routeId;
    private String vehicleNumber;
    private List<Phone> driverPhoneList;
    private List<Order> orderList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<Phone> getDriverPhoneList() {
        return driverPhoneList;
    }

    public void setDriverPhoneList(List<Phone> driverPhoneList) {
        this.driverPhoneList = driverPhoneList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
