package com.team766.tests;

import lib.RobotValues;
import lib.Scheduler;

import org.junit.Test;

import tests.Gyro;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.HDrive;

public class AutonTest extends RobotTestCase{

	@Test
	public void testNoAuton() throws Exception {
		
		RobotValues.AutonMode = 0;
		
		autonInit();
		
		//Robot Doesn't move
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		//Heading 90
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(90.0);
		
		Scheduler.getInstance().sendMessage(new HDrive(0, 1, 0, true));
	}
}
