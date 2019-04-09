package com.peter.villavanilia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductModel implements Parcelable {

    @SerializedName("product_id")
    @Expose
    String product_id;

    @SerializedName("product_title_ar")
    @Expose
    String product_title_ar;

    @SerializedName("product_title_en")
    @Expose
    String product_title_en;

    @SerializedName("product_price")
    @Expose
    String product_price;

    @SerializedName("product_discount")
    @Expose
    String product_discount;

    @SerializedName("product_img")
    @Expose
    String product_img;

    // for add order
    @SerializedName("additions_items")
    @Expose
    ArrayList<AdditionItems> additions_items;

    // for order details
    @SerializedName("additions")
    @Expose
    ArrayList<AdditionItems> additions_order_details;

    public ProductModel() {
    }

    public ProductModel(String product_id, String product_title_ar, String product_title_en, String product_price, String product_discount, String product_img) {
        this.product_id = product_id;
        this.product_title_ar = product_title_ar;
        this.product_title_en = product_title_en;
        this.product_price = product_price;
        this.product_discount = product_discount;
        this.product_img = product_img;
    }


    protected ProductModel(Parcel in) {
        product_id = in.readString();
        product_title_ar = in.readString();
        product_title_en = in.readString();
        product_price = in.readString();
        product_discount = in.readString();
        product_img = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_title_ar() {
        return product_title_ar;
    }

    public void setProduct_title_ar(String product_title_ar) {
        this.product_title_ar = product_title_ar;
    }

    public String getProduct_title_en() {
        return product_title_en;
    }

    public void setProduct_title_en(String product_title_en) {
        this.product_title_en = product_title_en;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public ArrayList<AdditionItems> getAdditions_items() {
        return additions_items;
    }

    public void setAdditions_items(ArrayList<AdditionItems> additions_items) {
        this.additions_items = additions_items;
    }


    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public ArrayList<AdditionItems> getAdditions_order_details() {
        return additions_order_details;
    }

    public void setAdditions_order_details(ArrayList<AdditionItems> additions_order_details) {
        this.additions_order_details = additions_order_details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(product_title_ar);
        dest.writeString(product_title_en);
        dest.writeString(product_price);
        dest.writeString(product_discount);
        dest.writeString(product_img);
    }
}
