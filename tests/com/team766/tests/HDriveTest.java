package com.team766.tests;

import lib.Scheduler;

import org.junit.Test;

import tests.Gyro;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.HDrive;

public class HDriveTest extends RobotTestCase{

	@Test
	public void testHDriveControl() throws Exception {
		//Heading 0
		((Gyro)(instance.getGyro(ConfigFile.getHopperSensor()))).setAngle(0.0);
		
		//Joystick Forward
		Scheduler.getInstance().sendMessage(new HDrive(0, 1, 0));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() > 0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() > 0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		//Heading 90
		((Gyro)(instance.getGyro(ConfigFile.getHopperSensor()))).setAngle(90.0);
		
		//Joystick Forward
		Scheduler.getInstance().sendMessage(new HDrive(0, 1, 0));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2);
		//System.out.println("Herrooo");
		//System.out.println(instance.getMotor(ConfigFile.getCenterMotor()).get());
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() > 0.5;}, 2);
	}
	
	@Test
	public void testHDriveHeading() throws Exception {
		//Heading 0
		((Gyro)(instance.getGyro(ConfigFile.getHopperSensor()))).setAngle(0.0);
		
		//Turn in a circle
		Scheduler.getInstance().sendMessage(new HDrive(0, 0, 1));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() > 0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() < -0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		//#########################################################################################//
		
		//Heading 0
		((Gyro)(instance.getGyro(ConfigFile.getHopperSensor()))).setAngle(0.0);
		
		//Strafes sideways while turning
		Scheduler.getInstance().sendMessage(new HDrive(1, 0, 1));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() < 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		((Gyro)(instance.getGyro(ConfigFile.getHopperSensor()))).setAngle(90.0);
		//Facing 90 deg, drive motors now moving it sideways
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() < 0.5;}, 2);
		
	}
}
