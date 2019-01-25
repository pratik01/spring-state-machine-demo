package com.PEaaS.statemachinedemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PEaaS.statemachinedemo.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Order findOneById(Long orderId);
}
