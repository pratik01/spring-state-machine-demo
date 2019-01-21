package com.PEaaS.statemachinedemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.PEaaS.statemachinedemo.enums.OrderEvents;
import com.PEaaS.statemachinedemo.enums.OrderStates;

import lombok.extern.java.Log;

@Log
@Configuration
@EnableStateMachineFactory
public class SimpleEnumStateMachineConfiguration extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

	@Override
	public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
		transitions
			.withExternal()
				.source(OrderStates.SUBMITTED)
				.target(OrderStates.PAID)
				.event(OrderEvents.PAY)
				.and()
			.withExternal()
				.source(OrderStates.PAID)
				.target(OrderStates.FULFILLED)
				.event(OrderEvents.FULFILL)
				.and()
			.withExternal()
				.source(OrderStates.SUBMITTED)
				.target(OrderStates.CANCELLED)
				.event(OrderEvents.CANCEL)
				.and()
			.withExternal()
				.source(OrderStates.PAID)
				.target(OrderStates.CANCELLED)
				.event(OrderEvents.CANCEL);
	}

	@Override
	public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
		states
			.withStates()
				.initial(OrderStates.SUBMITTED)
				.stateEntry(OrderStates.SUBMITTED, context -> {
						Long orderId = (Long) context.getExtendedState().getVariables().get("orderId");
						System.out.println("orderId = " + orderId);
						System.out.println("entering submitted state");
					})
				.state(OrderStates.PAID)
				.end(OrderStates.FULFILLED)
				.end(OrderStates.CANCELLED);
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
		StateMachineListener<OrderStates, OrderEvents> adapter = new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
			@Override
			public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
				System.out.println("StateChange from " + from.getId().name() + " to " + to.getId().name());
			}
		};
		config.withConfiguration().autoStartup(false).listener(adapter);
	}
}