package com.team766.tests;

import lib.Scheduler;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.UpdateClimber;
import tests.RobotTestCase;

public class ClimberTest extends RobotTestCase{
	
	public void testClimberMotor() throws Exception{
		//True
		Scheduler.getInstance().sendMessage(new UpdateClimber(true));
		//Check climber motor moving
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getClimberMotor()).get() > 0;}, 2);
		
		
		//false
		Scheduler.getInstance().sendMessage(new UpdateClimber(false));
		//Check climber motor stop
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getClimberMotor()).get() == 0;}, 2);
		
	}

}
