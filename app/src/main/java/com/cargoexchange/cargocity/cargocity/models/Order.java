package com.cargoexchange.cargocity.cargocity.models;

import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private Address address;
    private List<OrderItem> orderItemsList;
    private int mstatus;

    public Order(String orderId, Customer customer, Address address, List<OrderItem> orderItemsList,int mstatus)
    {
        this.orderId = orderId;
        this.customer = customer;
        this.address = address;
        this.orderItemsList = orderItemsList;
        this.mstatus=mstatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    public int getMstatus() {
        return mstatus;
    }

    public void setMstatus(int mstatus) {
        this.mstatus = mstatus;
    }
}
