package com.example.order.client;

import com.example.order.model.Cart;
import com.example.order.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-cart", url = "http://localhost:8888")
public interface CartClient {


    @GetMapping("/carts/{userName}")
    public List<Cart> getCartDetails(@PathVariable(name = "userName") String userName);

    //Get
}
