package com.peter.villavanilia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdditionItems implements Parcelable {

    @SerializedName("additions_item_ar_name")
    @Expose
    String additions_item_ar_name;

    @SerializedName("additions_item_en_name")
    @Expose
    String additions_item_en_name;

    @SerializedName("additions_item_price")
    @Expose
    String additions_item_price;

    @SerializedName("additions_item_id")
    @Expose
    String additions_item_id;

    public AdditionItems() {
    }

    public AdditionItems(String additions_item_ar_name, String additions_item_en_name, String additions_item_price, String additions_item_id) {
        this.additions_item_ar_name = additions_item_ar_name;
        this.additions_item_en_name = additions_item_en_name;
        this.additions_item_price = additions_item_price;
        this.additions_item_id = additions_item_id;
    }

    protected AdditionItems(Parcel in) {
        additions_item_ar_name = in.readString();
        additions_item_en_name = in.readString();
        additions_item_price = in.readString();
        additions_item_id = in.readString();
    }

    public static final Creator<AdditionItems> CREATOR = new Creator<AdditionItems>() {
        @Override
        public AdditionItems createFromParcel(Parcel in) {
            return new AdditionItems(in);
        }

        @Override
        public AdditionItems[] newArray(int size) {
            return new AdditionItems[size];
        }
    };

    public String getAdditions_item_ar_name() {
        return additions_item_ar_name;
    }

    public void setAdditions_item_ar_name(String additions_item_ar_name) {
        this.additions_item_ar_name = additions_item_ar_name;
    }

    public String getAdditions_item_en_name() {
        return additions_item_en_name;
    }

    public void setAdditions_item_en_name(String additions_item_en_name) {
        this.additions_item_en_name = additions_item_en_name;
    }

    public String getAdditions_item_price() {
        return additions_item_price;
    }

    public void setAdditions_item_price(String additions_item_price) {
        this.additions_item_price = additions_item_price;
    }

    public String getAdditions_item_id() {
        return additions_item_id;
    }

    public void setAdditions_item_id(String additions_item_id) {
        this.additions_item_id = additions_item_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(additions_item_ar_name);
        dest.writeString(additions_item_en_name);
        dest.writeString(additions_item_price);
        dest.writeString(additions_item_id);
    }
}
