package com.team766.lib.Messages;

import lib.Message;

public class CheesyDrive implements Message{
	private boolean quickTurn;
	private double accel;
	private double steer;
	
	public CheesyDrive(boolean quickTurn, double accel, double steer){
		this.quickTurn = quickTurn;
		this.accel = accel;
		this.steer = steer;
	}
	
	public double getAngularVelocity(){
		return steer;
	}
	
	public double getLinearVelocity(){
		return accel;
	}
	
	public boolean getQuickTurn(){
		return quickTurn;
	}
	
	public String toString() {
		return "Message:\tCheesy Drive";
	}
}
