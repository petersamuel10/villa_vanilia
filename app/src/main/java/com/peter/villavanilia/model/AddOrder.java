package com.peter.villavanilia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddOrder implements Parcelable {

    @SerializedName("payment_status_id")
    @Expose
    String payment_status_id;

    @SerializedName("client_id")
    @Expose
    String client_id;

    @SerializedName("client_address")
    @Expose
    String client_address;

    @SerializedName("driver_notes")
    @Expose
    String driver_notes;

    @SerializedName("kitchan_notes")
    @Expose
    String kitchan_notes;

    @SerializedName("card_notes")
    @Expose
    String card_notes;

    @SerializedName("order_date")
    @Expose
    String order_date;

    @SerializedName("order_time")
    @Expose
    String order_time;

    @SerializedName("product_info")
    @Expose
    ArrayList<ProductModel> product_info;

    public AddOrder() {
    }

    public AddOrder(String payment_status_id, String client_id,String client_address, String driver_notes, String kitchan_notes,
                    String card_notes, String order_date, String order_time, ArrayList<ProductModel> product_info) {
        this.payment_status_id = payment_status_id;
        this.client_id = client_id;
        this.client_address = client_address;
        this.driver_notes = driver_notes;
        this.kitchan_notes = kitchan_notes;
        this.card_notes = card_notes;
        this.order_date = order_date;
        this.order_time = order_time;
        this.product_info = product_info;
    }

    protected AddOrder(Parcel in) {
        payment_status_id = in.readString();
        client_id = in.readString();
        client_address = in.readString();
        driver_notes = in.readString();
        kitchan_notes = in.readString();
        card_notes = in.readString();
        order_date = in.readString();
        order_time = in.readString();
        product_info = in.createTypedArrayList(ProductModel.CREATOR);
    }

    public static final Creator<AddOrder> CREATOR = new Creator<AddOrder>() {
        @Override
        public AddOrder createFromParcel(Parcel in) {
            return new AddOrder(in);
        }

        @Override
        public AddOrder[] newArray(int size) {
            return new AddOrder[size];
        }
    };

    public String getPayment_status_id() {
        return payment_status_id;
    }

    public void setPayment_status_id(String payment_status_id) {
        this.payment_status_id = payment_status_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_address() {
        return client_address;
    }

    public void setClient_address(String client_address) {
        this.client_address = client_address;
    }

    public String getDriver_notes() {
        return driver_notes;
    }

    public void setDriver_notes(String driver_notes) {
        this.driver_notes = driver_notes;
    }

    public String getKitchan_notes() {
        return kitchan_notes;
    }

    public void setKitchan_notes(String kitchan_notes) {
        this.kitchan_notes = kitchan_notes;
    }

    public String getCard_notes() {
        return card_notes;
    }

    public void setCard_notes(String card_notes) {
        this.card_notes = card_notes;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public ArrayList<ProductModel> getProduct_info() {
        return product_info;
    }

    public void setProduct_info(ArrayList<ProductModel> product_info) {
        this.product_info = product_info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(payment_status_id);
        dest.writeString(client_id);
        dest.writeString(client_address);
        dest.writeString(driver_notes);
        dest.writeString(kitchan_notes);
        dest.writeString(card_notes);
        dest.writeString(order_date);
        dest.writeString(order_time);
        dest.writeTypedList(product_info);
    }
}
