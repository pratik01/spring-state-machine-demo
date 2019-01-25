package com.peaas.fsm.services;

import org.springframework.stereotype.Service;

import com.peaas.fsm.enums.States;

@Service
public class ScreeningTask {

	public States execute(States state) {
		return States.BLACKLIST_SCANNING_COMPLETED;
	}
}
