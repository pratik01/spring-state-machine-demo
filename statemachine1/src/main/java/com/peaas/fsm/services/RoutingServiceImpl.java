package com.peaas.fsm.services;

import org.springframework.stereotype.Service;

import com.peaas.fsm.enums.States;

@Service
public class RoutingServiceImpl implements RoutingService{

	@Override
	public States execute(States state) {
		return States.ROUTING_DONE;
	}

}
