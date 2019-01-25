package com.peaas.fsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.peaas.fsm.enums.States;
import com.peaas.fsm.services.TaskManagerService;

@SpringBootApplication
public class Application {

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}
}

@Component
class Runner implements ApplicationRunner {

	@Autowired
	private TaskManagerService taskManagerService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			States dbStatus = States.ISSUED;
			do {
				dbStatus = taskManagerService.execute(dbStatus);
			} while (dbStatus != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}