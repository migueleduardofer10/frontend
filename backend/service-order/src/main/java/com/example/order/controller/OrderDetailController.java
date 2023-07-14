package com.example.order.controller;

import com.example.order.dto.OrderInput;
import com.example.order.dto.TransactionDetails;
import com.example.order.entity.OrderDetail;
import com.example.order.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/orders/{isSingleProductCheckout}")
    public void placeOrder(@PathVariable(name = "isSingleProductCheckout") boolean isSingleProductCheckout,
                           @RequestBody OrderInput orderInput) {
        orderDetailService.placeOrder(orderInput, isSingleProductCheckout);
    }

    @GetMapping("/orders")
    public List<OrderDetail> getOrderDetails() {
        return orderDetailService.getOrderDetails();
    }

    @GetMapping("/orders/{status}")
    public List<OrderDetail> getAllOrderDetails(@PathVariable(name = "status") String status) {
        return orderDetailService.getAllOrderDetails(status);
    }

    @GetMapping("/orders/{orderId}/deliver")
    public void markOrderAsDelivered(@PathVariable(name = "orderId") Integer orderId) {
        orderDetailService.markOrderAsDelivered(orderId);
    }

    @GetMapping("/transactions/{amount}")
    public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
        return orderDetailService.createTransaction(amount);
    }

}
