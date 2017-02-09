package com.team766.lib.Messages;

import lib.Message;

public class DriveDistance implements Message{
	private double distance;
	private double angle;
	
	public DriveDistance(double distance, double angle){
		this.distance = distance; //meters
		this.angle = angle; //degrees
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getAngle(){
		return angle;
	}
	
	public String toString(){
		return "Message:\tDrive Distance";
	}

}
