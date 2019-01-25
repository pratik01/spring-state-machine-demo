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
	private RoutingService routingService;

	@Autowired	
	private StateMachineFactory<States, Events> factory;

	@Autowired
	private PostToAmiGoTask postToAmiGoTask;

	@Override
	public States execute(States state) {
		States updatedStatus = null; // store response status
		switch (state) {
		case ISSUED:
			System.out.println("Task execute : doRouting");
			updatedStatus = routingService.execute(state);
			if (updatedStatus.equals(States.ROUTING_DONE)) {
				this.sendEvent(state,Events.ROUTING_SUCCESS);
			} else {
				this.sendEvent(state,Events.ROUTING_ERROR);
			}
			return updatedStatus;
		case ROUTING_DONE:
			updatedStatus = postToAmiGoTask.execute(state);
			if (updatedStatus.equals(States.SERVICE_PROVIDER_DONE)) {
				this.sendEvent(state,Events.PUSH_TO_AMIGO);
			} else {
				this.sendEvent(state,Events.ROUTING_ERROR);
			}
			return null;
		case ROUTING_FAILED:
			return null;

		default:
			return null;
		}
	}

	private void sendEvent(States currentState,Events event) {
		StateMachine<States, Events> machine = factory.getStateMachine();
		Events noEvent = null;
		machine.getStateMachineAccessor().doWithAllRegions(access -> {
			access.resetStateMachine(
					new DefaultStateMachineContext<States, Events>(currentState, noEvent, null, null));
		});
		machine.start();
		machine.sendEvent(event);
		machine.stop();
	}

}
