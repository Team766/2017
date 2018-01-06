package com.team766.lib;

import interfaces.ConfigFileReader;

/**
 * Class for accessing the port or index for each hardware device
 * 
 * @author Brett Levenson
 *
 */

public class ConfigFile {
	
	//Drive
	public static int[] getLeftMotor(){
		return ConfigFileReader.getInstance().getPorts("leftMotor");
	}
	
	public static int[] getRightMotor(){
		return ConfigFileReader.getInstance().getPorts("rightMotor");
	}
	
	public static int getCenterMotor(){
		return ConfigFileReader.getInstance().getPort("centerMotor");
	}
	
	public static int[] getLeftEncoder(){
		return ConfigFileReader.getInstance().getPorts("leftEncoder");
	}
	
	public static int[] getRightEncoder(){
		return ConfigFileReader.getInstance().getPorts("rightEncoder");
	}
	
	public static int[] getCenterEncoder(){
		return ConfigFileReader.getInstance().getPorts("centerEncoder");
	}
	
	public static int getGyro(){
		return ConfigFileReader.getInstance().getPort("gyro");
	}
	
	//Joysticks
	public static int getLeftJoystick(){
		return ConfigFileReader.getInstance().getPort("leftJoystick");
	}
	
	public static int getRightJoystick(){
		return ConfigFileReader.getInstance().getPort("rightJoystick");
	}
	
	public static int getBoxJoystick(){
		return ConfigFileReader.getInstance().getPort("boxJoystick");
	}
	
	//Hopper
	public static int getHopperMotor(){
		return ConfigFileReader.getInstance().getPort("hopperMotor");
	}
	
	public static int getHopperOpener(){
		return ConfigFileReader.getInstance().getPort("hopperOpener");
	}
	
	public static int getHopperCloser(){
		return ConfigFileReader.getInstance().getPort("hopperCloser");
	}
	
	public static int getHopperSensor(){
		return ConfigFileReader.getInstance().getPort("hopperSensor");
	}
	
	//Gear Placer
	public static int getGearSensor(){
		return ConfigFileReader.getInstance().getPort("gearSensor");
	}
	
	public static int getGearPlacerOpener(){
		return ConfigFileReader.getInstance().getPort("gearPlacerOpener");
	}
	
	public static int getGearPlacer(){
		return ConfigFileReader.getInstance().getPort("gearPlacer");
	}
	
    //Climber
	public static int getClimberMotor(){
		return ConfigFileReader.getInstance().getPort("climberMotor");
	}
	
	public static int[] getClimberEncoder(){
		return ConfigFileReader.getInstance().getPorts("climberEncoder");
	} 
	
	public static int getClimberDeployOut() {
		return ConfigFileReader.getInstance().getPort("climberDeployOut");
	}
	
	public static int getClimberDeployIn() {
		return ConfigFileReader.getInstance().getPort("climberDeployIn");
	}
	
}
