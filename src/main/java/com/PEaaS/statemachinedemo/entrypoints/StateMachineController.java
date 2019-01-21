package com.PEaaS.statemachinedemo.entrypoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PEaaS.statemachinedemo.enums.Events;
import com.PEaaS.statemachinedemo.enums.States;
import com.PEaaS.statemachinedemo.enums.Variables;

@RestController
@RequestMapping("/api/statemachine")
public class StateMachineController {

	@Autowired
	private StateMachine<States, Events> stateMachine;

	@GetMapping
	public Object validateStateMachine() {
		try {

			stateMachine.getExtendedState().getVariables().put(Variables.DB_STATUS, States.IN_PROGRESS);
			stateMachine.getExtendedState().getVariables().put(Variables.REQ_STATUS, States.BACKLOG);
			
			stateMachine.sendEvent(Events.START_FEATURE);
			System.out.println(stateMachine.getState().getId());
			stateMachine.sendEvent(Events.FINISH_FEATURE);
			System.out.println(stateMachine.getState().getId());
			boolean isValidState = stateMachine.sendEvent(Events.FINISH_FEATURE);
			System.out.println("isValidState = " + isValidState);
			System.out.println(stateMachine.getState().getId());

		} catch (Exception e) {
			System.out.println("error = " + e.getMessage());
		}

		return stateMachine.getState().getId();

	}

	@GetMapping("/test1")
	public Object test1() {
		System.out.println(stateMachine.getState().getId());
		stateMachine.sendEvent(Events.ROCK_STAR_DOUBLE_TASK);
		System.out.println(stateMachine.getState().getId());
		return stateMachine.getState().getId();

	}
}
