package com.PEaaS.statemachinedemo.config;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.Actions;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import com.PEaaS.statemachinedemo.enums.Events;
import com.PEaaS.statemachinedemo.enums.States;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

	public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
		config.withConfiguration().listener(listener()).autoStartup(true);
		// config.withConfiguration().autoStartup(true);
	}

	public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {

		states.withStates().initial(States.BACKLOG).state(States.IN_PROGRESS, startDevelopment()).state(States.TESTING)
				.end(States.DONE);
	}

	private Action<States, Events> startDevelopment() {
		return context -> {
			System.out.println("DEVELOP: " + context.getEvent());
		};
	}

	private StateMachineListener<States, Events> listener() throws RuntimeErrorException {
		return new StateMachineListenerAdapter<States, Events>() {
			@Override
			public void transition(Transition<States, Events> transition) {
				System.out.println("move from: " + ofNullableState(transition.getSource()) + " to: "
						+ ofNullableState(transition.getTarget()));
			}

			@Override
			public void eventNotAccepted(Message<Events> event) {
				System.out.println("Event not accepted = " + event);
			}

			private Object ofNullableState(State s) {
				return Optional.ofNullable(s).map(State::getId).orElse(null);
			}
		};
	}

	public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {

		transitions.withExternal().source(States.BACKLOG).target(States.IN_PROGRESS).event(Events.START_FEATURE).and()
				.withExternal().source(States.IN_PROGRESS).target(States.TESTING).event(Events.FINISH_FEATURE)
				.action(new Action<States, Events>() {

					@Override
					public void execute(StateContext<States, Events> context) {
						System.out.println(context.getExtendedState().getVariables().get("dbStatus"));
						System.out.println(context.getExtendedState().getVariables().get("reqStatus"));
					}
				}).and().withExternal().source(States.TESTING).target(States.IN_PROGRESS)
				.event(Events.QA_TEAM_REJECT).and().withExternal().source(States.TESTING).target(States.DONE)
				.event(Events.QA_TEAM_APPROVE).and().withExternal().source(States.BACKLOG).target(States.TESTING)
				.event(Events.ROCK_STAR_DOUBLE_TASK);
	}

	private Guard<States, Events> gurd() {
		
		return null;
		
	}

}
