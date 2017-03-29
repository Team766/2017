package com.team766.tests;

import lib.RobotValues;
import lib.Scheduler;

import org.junit.Test;

import tests.Encoder;
import tests.Gyro;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.HDrive;

public class AutonTest extends TestCase {

	@Test
	public void testNoAuton() throws Exception {
		
		reset();
		
		RobotValues.AutonMode = 0;
				
		autonInit();
		
		//Robot Doesn't move
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
	}
	
	private void reset(){
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(0.0);
		
		((Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set(0);
		((Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set(0);
		((Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getCenterEncoder()[1])).set(0);
	}
}
