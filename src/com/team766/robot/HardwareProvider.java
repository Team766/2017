package com.team766.robot;

import interfaces.EncoderReader;
import interfaces.GyroReader;
import interfaces.JoystickReader;
import interfaces.RobotProvider;
import interfaces.SpeedController;

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
	public SpeedController getLeftDrive(){
		return RobotProvider.instance.getMotor(ConfigFile.getLeftMotor());
	}
	public SpeedController getRightDrive(){
		return RobotProvider.instance.getMotor(ConfigFile.getRightMotor());
	}

	public EncoderReader getLeftEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1]);
	}
	public EncoderReader getRightEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1]);
	}

	public GyroReader getGyro(){
		return RobotProvider.instance.getGyro(ConfigFile.getGyro());
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
