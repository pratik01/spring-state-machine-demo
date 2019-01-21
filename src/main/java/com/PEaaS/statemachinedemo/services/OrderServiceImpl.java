package com.PEaaS.statemachinedemo.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import com.PEaaS.statemachinedemo.enums.OrderEvents;
import com.PEaaS.statemachinedemo.enums.OrderStates;
import com.PEaaS.statemachinedemo.models.Order;
import com.PEaaS.statemachinedemo.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private StateMachineFactory<OrderStates, OrderEvents> factory;

	private static final String ORDER_ID_HEADER = "orderId";

	public Order create(Date when) {
		return this.orderRepository.save(new Order(when, OrderStates.SUBMITTED));
	}

	public StateMachine<OrderStates, OrderEvents> fulfill(Long orderId, String paymentConfirmationNumber,
			OrderEvents events) {
		StateMachine<OrderStates, OrderEvents> sm = this.build(orderId);
		Message<OrderEvents> fulfillmentMessage = MessageBuilder.withPayload(OrderEvents.FULFILL)
				.setHeader(ORDER_ID_HEADER, orderId)
				.build();
		sm.sendEvent(fulfillmentMessage);
		return sm;
	}

	public StateMachine<OrderStates, OrderEvents> pay(Long orderId, String paymentConfirmationNumber,
			OrderEvents events) {
		StateMachine<OrderStates, OrderEvents> sm = this.build(orderId);

		Message<OrderEvents> paymentMessage = MessageBuilder.withPayload(OrderEvents.PAY)
				.setHeader(ORDER_ID_HEADER, orderId).setHeader("paymentConfirmationNumber", paymentConfirmationNumber)
				.build();
		sm.sendEvent(paymentMessage);
		return sm;
	}

	private StateMachine<OrderStates, OrderEvents> build(Long orderId) {
		Optional<Order> obj = this.orderRepository.findById(orderId);
		StateMachine<OrderStates, OrderEvents> sm = null;
		if (obj.isPresent()) {
			Order order = obj.get();
			String orderIdKey = order.getId().toString();
			sm = factory.getStateMachine(orderIdKey);
			sm.stop();
			sm.getStateMachineAccessor().doWithAllRegions(sma -> {
				sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<OrderStates, OrderEvents>() {
					@Override
					public void preStateChange(State<OrderStates, OrderEvents> state, Message<OrderEvents> message,
							Transition<OrderStates, OrderEvents> transition,
							StateMachine<OrderStates, OrderEvents> stateMachine) {
						Optional.ofNullable(message).ifPresent(msg -> {
							Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L)))
									.ifPresent(orderId -> {
										Order order = orderRepository.findOneById(orderId);
										order.setOrderState(state.getId());
										orderRepository.save(order);
									});
						});
					}
				});
				sma.resetStateMachine(new DefaultStateMachineContext<OrderStates, OrderEvents>(order.getOrderState(),
						null, null, null));

			});
			sm.start();
		}
		return sm;
	}
}
