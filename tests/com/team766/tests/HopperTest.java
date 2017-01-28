package com.team766.tests;

import lib.Scheduler;



import org.junit.Test;

import tests.DigitalInput;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.HopperIntake;

public class HopperTest extends RobotTestCase{

	@Test
	public void testHopperMotor() throws Exception{
		Scheduler.getInstance().sendMessage(new HopperIntake());
		//no ball first
		((DigitalInput)(instance.getDigitalInput(ConfigFile.getHopperSensor()))).set(false);
		
		//Check hopper motor moving forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() > 0;}, 2);
		
		((DigitalInput)(instance.getDigitalInput(ConfigFile.getHopperSensor()))).set(true);
		
		//Check hopper motor is not moving
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() == 0;}, 2); 
	}
}
