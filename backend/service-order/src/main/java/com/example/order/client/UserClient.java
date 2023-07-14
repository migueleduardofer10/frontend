package com.example.order.client;

import com.example.order.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-user", url = "http://localhost:8080")
public interface UserClient {


    @GetMapping("/users/{username}")
    public User getUserByUsername(@PathVariable("username") String username);


}
