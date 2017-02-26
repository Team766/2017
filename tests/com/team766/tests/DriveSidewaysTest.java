package com.team766.tests;

import lib.Scheduler;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.DriveSideways;
import com.team766.robot.Constants;

import tests.Gyro;
import tests.RobotTestCase;

public class DriveSidewaysTest extends RobotTestCase {
	
	public void testDriveSideways() throws Exception {
		
		//set end point to 5 feet to the left
		Scheduler.getInstance().sendMessage(new DriveSideways(5));
		
		((tests.Encoder)instance.getEncoder(ConfigFile.getCenterEncoder()[0], ConfigFile.getCenterEncoder()[1])).set((int)(5.0 / Constants.wheel_circumference * Constants.counts_per_rev));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() != 0;}, 2);
		//assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() != 0;}, 2);
		//assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() != 0;}, 2);

		
		
		
		
		

		
	}

}
