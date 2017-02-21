package com.team766.lib.Messages;

import lib.Message;

public class VisionStatusUpdate implements Message{
	
	private double angle, dist;
	
	public VisionStatusUpdate(double angle, double dist){
		this.angle = angle;
		this.dist = dist;
	}
	
	public double getAngle(){
		return angle;
	}
	
	public double getDist(){
		return dist;
	}
		
	public String toString(){
		return "Message:\tVisionStatusUpdate";
	}
}
