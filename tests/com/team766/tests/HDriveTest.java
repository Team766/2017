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
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(0.0);
		
		//Joystick Forward
		Scheduler.getInstance().sendMessage(new HDrive(0, 1, 0, true));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		//Heading 90
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(90.0);
		
		Scheduler.getInstance().sendMessage(new HDrive(0, 1, 0, true));
		
		//Robot drives forwards
		//Use the line below to test what getCenterMotor's number actually is.
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2);
		//Use the line below to test what getCenterMotor's number actually is.
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() < 0;}, 2);
	}
	
	@Test
	public void testHDriveHeading() throws Exception {
		//Heading 0
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(0.0);
		
		//Turn in a circle
		Scheduler.getInstance().sendMessage(new HDrive(0, 0, 1, true));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() < -0.5;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
		//#########################################################################################//
		
		//Heading 0
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(0.0);
		
		//Strafe sideways while turning
		Scheduler.getInstance().sendMessage(new HDrive(1, 0, 1, true));
		
		//Robot drives forwards
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() < 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get()  == 0;}, 2);
		
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(90.0);
		//Facing 90 deg, drive motors now moving it sideways
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2);
		
		System.out.println("get centerMotor" + instance.getMotor(ConfigFile.getCenterMotor()).get());
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2);
		
	}
}
