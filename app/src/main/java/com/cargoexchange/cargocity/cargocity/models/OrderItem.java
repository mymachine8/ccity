package com.cargoexchange.cargocity.cargocity.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String itemId;
    private String itemName;

    public OrderItem(String itemId, String itemName)
    {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
