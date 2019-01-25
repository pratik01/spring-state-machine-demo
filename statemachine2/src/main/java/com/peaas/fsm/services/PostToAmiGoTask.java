package com.peaas.fsm.services;

import org.springframework.stereotype.Service;

import com.peaas.fsm.enums.States;

@Service
public class PostToAmiGoTask {

	public States execute(States state) {
		return States.SERVICE_PROVIDER_DONE;
	}
}
