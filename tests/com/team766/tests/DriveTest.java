package com.team766.tests;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.DriveDistance;
import com.team766.robot.Constants;

import lib.Scheduler;
import tests.RobotTestCase;

public class DriveTest extends RobotTestCase {

	public void testDriveDistance() throws Exception {
		
		//Forward 5 meters, at end point (Moving)
		Scheduler.getInstance().sendMessage(new DriveDistance(5, 0));
		
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(0);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(5 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(5 / Constants.wheel_circumference * Constants.counts_per_rev));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2); 
		

		//sets end point to 10 meters 45 degrees, checks middle of path (just checking, not moving)
		Scheduler.getInstance().sendMessage(new DriveDistance(10, 45));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() != 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() != 0;}, 2); 
		
		//sets sensors to end point (moving)
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(45);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(10 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(10 / Constants.wheel_circumference * Constants.counts_per_rev));
	
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2);
		
		
		//sets end point to 2 meters 300 degrees, checks middle of path (just checking, not moving)
		Scheduler.getInstance().sendMessage(new DriveDistance(2, 300));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() != 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() != 0;}, 2); 
				
		//sets sensors to end point (moving)
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(300);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(2 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(2 / Constants.wheel_circumference * Constants.counts_per_rev));
			
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2); 
		

		//sets end point to 50 meters 180 degrees, checks middle of path (just checking, not moving)
		Scheduler.getInstance().sendMessage(new DriveDistance(50, 180));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() != 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() != 0;}, 2); 
				
		//sets sensors to end point (moving)
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(180);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(50 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(50 / Constants.wheel_circumference * Constants.counts_per_rev));
			
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2); 
		
		
		// sets end point to 20 meters 90 degrees, checks middle of path (just checking, not moving)
		Scheduler.getInstance().sendMessage(new DriveDistance(20, 90));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() != 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() != 0;}, 2); 
		
		//sets sensors to end point (moving)
		((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(90);
		((tests.Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set((int)(20 / Constants.wheel_circumference * Constants.counts_per_rev));
		((tests.Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set((int)(20 / Constants.wheel_circumference * Constants.counts_per_rev));
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()).get() == 0;}, 2); 
		
		
		
	}
}
