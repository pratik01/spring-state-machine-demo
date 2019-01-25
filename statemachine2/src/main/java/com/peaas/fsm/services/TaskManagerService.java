package com.peaas.fsm.services;

import com.peaas.fsm.enums.Events;
import com.peaas.fsm.enums.States;

public interface TaskManagerService {
	public States execute(States state, Events event);
}
