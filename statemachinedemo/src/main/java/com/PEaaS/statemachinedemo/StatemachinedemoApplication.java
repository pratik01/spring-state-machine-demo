package com.PEaaS.statemachinedemo;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import com.PEaaS.statemachinedemo.enums.OrderEvents;
import com.PEaaS.statemachinedemo.enums.OrderStates;
import com.PEaaS.statemachinedemo.models.Order;
import com.PEaaS.statemachinedemo.services.OrderService;

@SpringBootApplication
public class StatemachinedemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatemachinedemoApplication.class, args);
	}

}

@Component
class Runner implements ApplicationRunner {

	@Autowired
	private StateMachineFactory<OrderStates, OrderEvents> factory;

	@Autowired
	private OrderService orderService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//this.test1();
		this.test2();
	}

	private void test2() {
		System.out.println(this.factory.getStateMachine("13232").getInitialState());
		Order order = this.orderService.create(new Date());		
		StateMachine<OrderStates, OrderEvents> paymentStateMachine = orderService.pay(order.getId(),
				UUID.randomUUID().toString(), OrderEvents.PAY);
		System.out.println("after calling pay() : " + paymentStateMachine.getState().getId().name());
		StateMachine<OrderStates, OrderEvents> fulfilledStateMachine = orderService.fulfill(order.getId(),
				UUID.randomUUID().toString(), OrderEvents.FULFILL);
		System.out.println("after calling fulfill() : " + fulfilledStateMachine.getState().getId().name());
	}

	@SuppressWarnings("unused")
	private void test1() {
		Long orderId = 13232L;
		StateMachine<OrderStates, OrderEvents> machine = this.factory.getStateMachine("13232");
		machine.getExtendedState().getVariables().put("orderId", orderId);
		machine.start();
		System.out.println("current state = " + machine.getState().getId().name());
		machine.sendEvent(OrderEvents.PAY);
		System.out.println("current state = " + machine.getState().getId().name());
		Message<OrderEvents> event = MessageBuilder.withPayload(OrderEvents.FULFILL).setHeader("a", "b").build();
		machine.sendEvent(event);
		System.out.println("current state = " + machine.getState().getId().name());
	}
}
