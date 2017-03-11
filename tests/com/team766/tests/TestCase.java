package com.team766.tests;

import com.team766.lib.ConfigFile;

import tests.RobotTestCase;
import tests.Gyro;
import tests.Encoder;

public class TestCase extends RobotTestCase{
	
	public void resetDrive() throws Exception{
		((Encoder)instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1])).set(0);
		((Encoder)instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1])).set(0);
		((Encoder)instance.getEncoder(ConfigFile.getCenterEncoder()[0], ConfigFile.getCenterEncoder()[1])).set(0);
		((Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(0);
		
		//Check encoder sets took effect
		assertTrueTimed(() -> {return instance.getGyro(ConfigFile.getGyro()).getAngle() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1]).get() == 0;}, 2); 
		assertTrueTimed(() -> {return instance.getEncoder(ConfigFile.getCenterEncoder()[0], ConfigFile.getCenterEncoder()[1]).get() == 0;}, 2); 
		
	}
	
	public void resetClimber() throws Exception{
		((Encoder)instance.getEncoder(ConfigFile.getClimberEncoder()[0], ConfigFile.getClimberEncoder()[1])).set(0);
		
		assertTrueTimed(() -> {return instance.getEncoder(ConfigFile.getClimberEncoder()[0], ConfigFile.getClimberEncoder()[1]).get() == 0;}, 2); 
	}
	
	public void resetAll() throws Exception{
		resetDrive();
		resetClimber();
	}

}
