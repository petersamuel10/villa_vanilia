package com.peter.villavanilia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdditionsModel {

    @SerializedName("additions_name_ar")
    @Expose
    String additions_name_ar;

    @SerializedName("additions_name_eng")
    @Expose
    String additions_name_eng;

    @SerializedName("additions_type")
    @Expose
    String additions_type;

    @SerializedName("additions_id")
    @Expose
    String additions_id;

    @SerializedName("additions_items")
    @Expose
    ArrayList<AdditionItems> additions_items;

    public AdditionsModel() {
    }

    public AdditionsModel(String additions_name_ar, String additions_name_eng, String additions_type, String additions_id, ArrayList<AdditionItems> additions_items) {
        this.additions_name_ar = additions_name_ar;
        this.additions_name_eng = additions_name_eng;
        this.additions_type = additions_type;
        this.additions_id = additions_id;
        this.additions_items = additions_items;
    }

    public String getAdditions_name_ar() {
        return additions_name_ar;
    }

    public void setAdditions_name_ar(String additions_name_ar) {
        this.additions_name_ar = additions_name_ar;
    }

    public String getAdditions_name_eng() {
        return additions_name_eng;
    }

    public void setAdditions_name_eng(String additions_name_eng) {
        this.additions_name_eng = additions_name_eng;
    }

    public String getAdditions_type() {
        return additions_type;
    }

    public void setAdditions_type(String additions_type) {
        this.additions_type = additions_type;
    }

    public String getAdditions_id() {
        return additions_id;
    }

    public void setAdditions_id(String additions_id) {
        this.additions_id = additions_id;
    }

    public ArrayList<AdditionItems> getAdditions_items() {
        return additions_items;
    }

    public void setAdditions_items(ArrayList<AdditionItems> additions_items) {
        this.additions_items = additions_items;
    }
}
