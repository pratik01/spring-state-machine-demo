package com.peaas.fsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.peaas.fsm.enums.Events;
import com.peaas.fsm.enums.States;
import com.peaas.fsm.services.PostToAmiGoTask;
import com.peaas.fsm.services.ScreeningTask;

public class FSMListener extends StateMachineListenerAdapter<States, Events> {

	private StateMachine<States, Events> machine;

	@Autowired
	private PostToAmiGoTask postToAmiGoTask;

	@Autowired
	private ScreeningTask screeningTask;

	@Override
	public void stateEntered(State<States, Events> state) {
		System.out.println("State Entered");		
	}

	
	
	@Override
	public void stateChanged(State<States, Events> from, State<States, Events> to) {
		System.out.println("state changed from " + from.getId().name() + " to " + to.getId().name());
		// TODO add logic here for update the latest status based on FSM
		States updatedState = null;
		switch (to.getId()) {
		case ROUTING_DONE:
			// call task PUSH_TO_AMIGO
			updatedState = postToAmiGoTask.execute(States.ROUTING_DONE);
			System.out.println(updatedState);
			if (updatedState.equals(States.SERVICE_PROVIDER_DONE)) {
				machine.sendEvent(Events.PUSH_TO_AMIGO);
			} else {
				machine.sendEvent(Events.PUSH_TO_AMIGO_FAILED);
			}
			break;
		case ROUTING_FAILED:
			break;
		case SERVICE_PROVIDER_DONE:
			updatedState = screeningTask.execute(States.SERVICE_PROVIDER_DONE);
			if (updatedState.equals(States.AML_SUSPENDED)) {
				machine.sendEvent(Events.SCREENING_SUSPENDED);
			} else {
				machine.sendEvent(Events.SCREENING_DONE);
			}
			break;
		case AML_SUSPENDED:
			break;
		case BLACKLIST_SCANNING_COMPLETED:
			// do bank profile validation
			updatedState = States.RO_VERIFICATION_DONE;
			if(updatedState.equals(States.RO_VERIFICATION_DONE)) {
				machine.sendEvent(Events.RO_VERIFICATION_SUCCESS);
			}else if(updatedState.equals(States.AVAILABLE_FOR_MANUAL_RO)) {
				machine.sendEvent(Events.RO_VERIFICATION_FAILED);
			}
			break;
		default:
			break;

		}
	}

	@Override
	public void eventNotAccepted(Message<Events> event) {
		System.out.println("Event not accepted");
	}

	@Override
	public void stateContext(StateContext<States, Events> stateContext) {
		machine = stateContext.getStateMachine();
	}
}
