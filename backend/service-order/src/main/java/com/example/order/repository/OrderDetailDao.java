package com.example.order.repository;

import com.example.order.entity.OrderDetail;
import com.example.order.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailDao extends CrudRepository<OrderDetail, Integer> {
    public List<OrderDetail> findByUserName(String user);

    public List<OrderDetail> findByOrderStatus(String status);
}
