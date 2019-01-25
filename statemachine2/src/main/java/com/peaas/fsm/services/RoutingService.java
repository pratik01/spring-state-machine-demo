package com.peaas.fsm.services;

import com.peaas.fsm.enums.States;

public interface RoutingService {
	public States execute(States state);
}
