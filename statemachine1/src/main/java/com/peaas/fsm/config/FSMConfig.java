package com.peaas.fsm.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.peaas.fsm.enums.Events;
import com.peaas.fsm.enums.States;


@Configuration
@EnableStateMachineFactory
public class FSMConfig extends StateMachineConfigurerAdapter<States, Events> {
	@Override
	public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
		config.withConfiguration().autoStartup(false).listener(listener());
	}

	@Override
	public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
		states
			.withStates()
				.initial(States.ISSUED)				
				.states(EnumSet.allOf(States.class))
				.end(States.END);
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
		transitions
			.withExternal()
				.source(States.ISSUED)
				.target(States.ROUTING_DONE)
				.event(Events.ROUTING_SUCCESS).and()
			.withExternal()
				.source(States.ISSUED)
				.target(States.ROUTING_FAILED)
				.event(Events.ROUTING_ERROR)
				.and()
			.withExternal()
				.source(States.ROUTING_DONE)
				.target(States.SERVICE_PROVIDER_DONE)
				.event(Events.PUSH_TO_AMIGO);
			
	}

	@Bean
	public FSMListener listener() {
		return new FSMListener();
	}
}
