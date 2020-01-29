package com.laundry.model;

/**
 * Created by rekha_p on 19-01-2018.
 */
public class Orders {
    String orderId;
    String date;
    String status;

    public Orders(String orderId, String date, String status) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
