package com.peter.villavanilia.model;

import java.util.ArrayList;

public class CartList {

    private ProductModel product;
    private ArrayList<AdditionItems> additions;

    public CartList() {
    }


    public CartList(ProductModel product, ArrayList<AdditionItems> additions) {
        this.product = product;
        this.additions = additions;
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
}
