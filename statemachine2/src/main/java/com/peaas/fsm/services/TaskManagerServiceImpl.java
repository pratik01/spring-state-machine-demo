package com.peaas.fsm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.peaas.fsm.enums.Events;
import com.peaas.fsm.enums.States;

@Service
public class TaskManagerServiceImpl implements TaskManagerService {

	@Autowired
	private StateMachineFactory<States, Events> factory;

	@Override
	public States execute(States currentState,Events event) {
		StateMachine<States, Events> machine = factory.getStateMachine();
		Events noEvent = null;
		machine.getStateMachineAccessor().doWithAllRegions(access -> {
			access.resetStateMachine(new DefaultStateMachineContext<States, Events>(currentState, noEvent, null, null));
		});
		machine.start();
		machine.sendEvent(event);
		machine.stop();
		return null;
	}

}
