package com.PEaaS.statemachinedemo.services;

import java.util.Date;

import org.springframework.statemachine.StateMachine;

import com.PEaaS.statemachinedemo.enums.OrderEvents;
import com.PEaaS.statemachinedemo.enums.OrderStates;
import com.PEaaS.statemachinedemo.models.Order;

public interface OrderService {
	public Order create(Date when);

	public StateMachine<OrderStates, OrderEvents> fulfill(Long orderId, String paymentConfirmationNumber,
			OrderEvents events);

	public StateMachine<OrderStates, OrderEvents> pay(Long orderId, String paymentConfirmationNumber,
			OrderEvents events);
}
