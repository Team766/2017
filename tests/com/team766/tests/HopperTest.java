package com.team766.tests;

import lib.Scheduler;

import org.junit.Test;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.HopperIntake;

import tests.DigitalInput;
import tests.RobotTestCase;

public class HopperTest extends RobotTestCase{

	public void testHopperMotor() throws Exception{
		Scheduler.getInstance().sendMessage(new HopperIntake());
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() > 0;}, 2); 
		
		((DigitalInput)(instance.getDigitalInput(ConfigFile.getHopperSensor()))).set(true);
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() == 0;}, 2); 
	}
}
