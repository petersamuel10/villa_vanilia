package com.peter.villavanilia.model;

import java.util.ArrayList;

public class CartModel {

    private ProductModel product;
    private ArrayList<AdditionItems> additions;
    private  float total;

    public CartModel() {
    }

    public CartModel(ProductModel product, ArrayList<AdditionItems> additions, float total) {
        this.product = product;
        this.additions = additions;
        this.total = total;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public ArrayList<AdditionItems> getAdditions() {
        return additions;
    }

    public void setAdditions(ArrayList<AdditionItems> additions) {
        this.additions = additions;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
