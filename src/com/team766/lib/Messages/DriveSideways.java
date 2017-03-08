package com.team766.lib.Messages;

import lib.Message;

public class DriveSideways implements Message{
	
	private double distance;
	
	public DriveSideways(double distance){
		this.distance = distance;	
	}
	
	public double getDistance(){
		return distance;
	}
	
	public String toString(){
		return "Message:\tDrive Sideways";
	}

}
