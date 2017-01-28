package com.team766.lib.Messages;

import lib.Message;

public class MotorCommand implements Message{

	private double value;
	private Motor motor;
	
	public enum Motor{
		rightDrive,
		leftDrive
	}
	
	public MotorCommand(double in, Motor mot){
		value = in;
		motor = mot;
	}
	
	public double getValue(){
		return value;
	}
	
	public Motor getMotor(){
		return motor;
	}
	
	public String toString() {
		return "Message:\tMotor Command";
	}

}
