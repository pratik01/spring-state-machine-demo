package com.peaas.fsm.config;

import org.springframework.messaging.Message;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.peaas.fsm.enums.Events;
import com.peaas.fsm.enums.States;

public class FSMListener extends StateMachineListenerAdapter<States, Events> {
	
	@Override
	public void stateEntered(State<States, Events> state) {
		System.out.println("State Entered");
	}
	
	@Override
	public void stateChanged(State<States, Events> from, State<States, Events> to) {
		System.out.println("state changed from " + from.getId().name() + " to " + to.getId().name());
		//TODO add logic here for update the latest status based on FSM
	}
	
	@Override
	public void eventNotAccepted(Message<Events> event) {
		System.out.println("Event not accepted");
	}
	
	
}
