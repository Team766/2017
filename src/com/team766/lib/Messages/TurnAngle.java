package com.team766.lib.Messages;

import lib.Message;

public class TurnAngle implements Message{
	private double angle;
	
	public TurnAngle(double angle){
		this.angle = angle; //degrees
	}
	
	public double getAngle(){
		return angle;
	}
	
	public String toString(){
		return "Message:\tTurnAngle";
	}
}
