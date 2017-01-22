package com.team766.lib;

import interfaces.ConfigFileReader;

/**
 * Class for accessing the port or index for each hardware device
 * 
 * @author Brett Levenson
 *
 */

public class ConfigFile {
	
	public static int getLeftMotor(){
		return ConfigFileReader.getInstance().getPort("leftMotor");
	}
	
	public static int getRightMotor(){
		return ConfigFileReader.getInstance().getPort("rightMotor");
	}
	
	public static int[] getLeftEncoder(){
		return ConfigFileReader.getInstance().getPorts("leftEncoder");
	}
	
	public static int[] getRightEncoder(){
		return ConfigFileReader.getInstance().getPorts("rightEncoder");
	}
	
	public static int[] getClimberEncoder(){
		return ConfigFileReader.getInstance().getPorts("climberEncoder");
	}
	
	
	public static int getGyro(){
		return ConfigFileReader.getInstance().getPort("gyro");
	}
	
	public static int getLeftJoystick(){
		return ConfigFileReader.getInstance().getPort("leftJoystick");
	}
	
	public static int getRightJoystick(){
		return ConfigFileReader.getInstance().getPort("rightJoystick");
	}
	
	public static int getBoxJoystick(){
		return ConfigFileReader.getInstance().getPort("boxJoystick");
	}
	
	public static int getClimberMotor(){
		return ConfigFileReader.getInstance().getPort("climberMotor");
	}
	
	public static int getHopperMotor(){
		return ConfigFileReader.getInstance().getPort("hopperMotor");
	}
}
