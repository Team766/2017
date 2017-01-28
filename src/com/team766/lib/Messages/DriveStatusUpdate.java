package com.team766.lib.Messages;

import lib.Message;
import lib.StatusUpdateMessage;

public class DriveStatusUpdate extends StatusUpdateMessage{
	
	private double posX, posY;
	private double velocity;
	
	public DriveStatusUpdate(boolean done, Message currentMessage, double posX, double posY, double velocity){
		super(done, currentMessage);
		
		this.posX = posX;
		this.posY = posY;
		this.velocity = velocity;
	}
	
	public double posX(){
		return posX;
	}
	
	public double posY(){
		return posY;
	}
	
	public double getVelocity(){
		return velocity;
	}
	
	public String toString(){
		return "Message:\tDriveStatusUpdate";
	}
}
