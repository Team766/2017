package com.team766.tests;

import org.junit.Test;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.DriveDistance;
import com.team766.robot.Constants;

import lib.Scheduler;
import tests.Encoder;
import tests.Gyro;

public class DriveTest extends TestCase {

	@Test
	public void testDriveDistance() throws Exception {
		//Forward 5 meters, at end point 
		resetDrive();
		
		Scheduler.getInstance().sendMessage(new DriveDistance(5, 0));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2); 
		
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(0);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(5 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(5 / Constants.wheel_circumference * Constants.counts_per_rev));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0;}, 2); 
	}
	
	@Test
	public void testDriveDistProfile() throws Exception {
		//sets end point to 10 meters 45 degrees, checks middle of path
		resetDrive();
		
		Scheduler.getInstance().sendMessage(new DriveDistance(10, 45));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2); 
		
		//sets sensors to end point
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(45);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(10 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(10 / Constants.wheel_circumference * Constants.counts_per_rev));
	
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2);
		
	}
	
	@Test
	public void testDriveDist2() throws Exception {
		//sets end point to 2 meters 300 degrees, checks middle of path
		
		resetDrive();
		
		Scheduler.getInstance().sendMessage(new DriveDistance(2, 300));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2); 
				
		//sets sensors to end point
		((Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(300);
		((Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(2.0 / Constants.wheel_circumference * Constants.counts_per_rev));
		((Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(2.0 / Constants.wheel_circumference * Constants.counts_per_rev));
			
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2); 
	}
	
	@Test
	public void testDriveDist3() throws Exception {
		//sets end point to 50 meters 180 degrees, checks middle of path
		resetDrive();
		
		Scheduler.getInstance().sendMessage(new DriveDistance(50, 180));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2); 
				
		//sets sensors to end point
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(180);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(50.0 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(50.0 / Constants.wheel_circumference * Constants.counts_per_rev));
			
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2); 
		
	}
	
	@Test
	public void testDriveDist4() throws Exception { 
		// sets end point to 20 meters 90 degrees, checks middle of path
		resetDrive();
		
		Scheduler.getInstance().sendMessage(new DriveDistance(20, 90));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0;}, 2); 
		
		//sets sensors to end point
		((Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(90);
		((Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(20.0 / Constants.wheel_circumference * Constants.counts_per_rev));
		((Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(20.0 / Constants.wheel_circumference * Constants.counts_per_rev));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0;}, 2); 
		
	}
}
