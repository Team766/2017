package com.team766.lib.Messages;

import lib.Message;
import lib.StatusUpdateMessage;

public class VisionStatusUpdate extends StatusUpdateMessage{
	
	private double angle, dist;
	
	public VisionStatusUpdate(boolean done, Message currentMessage, double angle, double dist){
		super(done, currentMessage);
		
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
