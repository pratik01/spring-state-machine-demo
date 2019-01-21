package com.PEaaS.statemachinedemo;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.junit4.SpringRunner;

import com.PEaaS.statemachinedemo.enums.Events;
import com.PEaaS.statemachinedemo.enums.States;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatemachinedemoApplicationTests {

	@Autowired
	private StateMachine<States, Events> stateMachine;

	@Test
	public void initTest() {
		Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.BACKLOG);

		Assertions.assertThat(stateMachine).isNotNull();
	}

	@Test
	public void testGreenFlow() {
		// Arrange
		// Act
		stateMachine.sendEvent(Events.START_FEATURE);
		stateMachine.sendEvent(Events.FINISH_FEATURE);
		stateMachine.sendEvent(Events.QA_TEAM_APPROVE);
		// Asserts
		Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.DONE);
	}
}
