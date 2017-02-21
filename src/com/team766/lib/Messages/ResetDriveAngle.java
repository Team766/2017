package com.team766.lib.Messages;

import lib.Message;

public class ResetDriveAngle implements Message{
	private double angle;
	
	public ResetDriveAngle(double angle){
		this.angle = angle;
	}
	
	public double getAngle(){
		return angle;
	}
	
	public String toString(){
		return "Message:\tReset Drive Angle";
	}
}
