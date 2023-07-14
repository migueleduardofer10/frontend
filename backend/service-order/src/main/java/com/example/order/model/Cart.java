package com.example.order.model;

import jakarta.persistence.*;
import lombok.Data;


public class Cart {
    private Integer cartId;


    private String userName;


    private Integer productId;

    public Cart(){

    }

    public Cart(Integer product, String user) {
        this.productId = product;
        this.userName = user;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }


}
