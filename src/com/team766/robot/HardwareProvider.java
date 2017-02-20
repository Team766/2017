package com.team766.robot;

import interfaces.CameraInterface;
import interfaces.EncoderReader;
import interfaces.GyroReader;
import interfaces.JoystickReader;
import interfaces.RobotProvider;
import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.DigitalInputReader;

import com.team766.lib.ConfigFile;

public class HardwareProvider {
	private static HardwareProvider instance;
	
	public static HardwareProvider getInstance(){
		if(instance == null){
			instance = new HardwareProvider();
		}
		return instance;
	}
	
	// HAL
	public SpeedController getLeftDriveA(){
		return RobotProvider.instance.getMotor(ConfigFile.getLeftMotor()[0]);
	}
	public SpeedController getLeftDriveB(){
		return RobotProvider.instance.getMotor(ConfigFile.getLeftMotor()[1]);
	}
	public SpeedController getRightDriveA(){
		return RobotProvider.instance.getMotor(ConfigFile.getRightMotor()[0]);
	}
	public SpeedController getRightDriveB(){
		return RobotProvider.instance.getMotor(ConfigFile.getRightMotor()[1]);
	}
	public SpeedController getCenterDrive(){
		return RobotProvider.instance.getMotor(ConfigFile.getCenterMotor());
	}
	
	public SpeedController getClimber(){
		return RobotProvider.instance.getMotor(ConfigFile.getClimberMotor());
	}
	
	public SpeedController getHopper(){
		return RobotProvider.instance.getMotor(ConfigFile.getHopperMotor());
	}

	public EncoderReader getLeftEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1]);
	}
	public EncoderReader getRightEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1]);
	}
	public EncoderReader getCenterEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getCenterEncoder()[0], ConfigFile.getCenterEncoder()[1]);
	}
	
	public EncoderReader getClimberEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getClimberEncoder()[0], ConfigFile.getClimberEncoder()[1]);
	}
	
	public CameraInterface getCameraServer(){
		return RobotProvider.instance.getCamServer();
	}
	
	
	
	

	public GyroReader getGyro(){
		return RobotProvider.instance.getGyro(ConfigFile.getGyro());
	}
	
	public SolenoidController getGearPlacerOpener(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getGearPlacerOpener());
	}
	
	public SolenoidController getGearPlacer(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getGearPlacer());
	}
	
	public SolenoidController getClimberDeploy(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getClimberDeploy());
	}
	
	public SolenoidController getHopperOpener(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getHopperOpener());
	}
	
	public SolenoidController getHopperCloser(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getHopperCloser());
	}
	

	// Operator Devices
	public JoystickReader getLeftJoystick(){
		return RobotProvider.instance.getJoystick(ConfigFile.getLeftJoystick());
	}
	public JoystickReader getRightJoystick(){
		return RobotProvider.instance.getJoystick(ConfigFile.getRightJoystick());
	}
	public JoystickReader getBoxJoystick(){
		return RobotProvider.instance.getJoystick(ConfigFile.getBoxJoystick());
	}
}
