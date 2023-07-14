package com.example.order.client;

import com.example.order.model.ImageModel;
import com.example.order.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FeignClient(name = "service-product", url = "http://localhost:9090")
public interface ProductClient {
    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable("productId") Integer productId);

    @GetMapping("/products/{productId}")
    public Product getProductDetailsById(@PathVariable("productId") Integer productId);
}
