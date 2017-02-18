package com.team766.lib.Messages;

import lib.Message;

public class HDrive implements Message{
	private double x, y, head;
	private boolean fieldCentric;
	
	public HDrive(double joyX, double joyY, double heading, boolean fieldCentric){
		x = joyX;
		y = joyY;
		head = heading;
		this.fieldCentric = fieldCentric;
	}
	
	public double getJoyX(){
		return x;
	}
	
	public double getJoyY(){
		return y;
	}
	
	public double getHeading(){
		return head;
	}
	
	public boolean isFieldCentric(){
		return fieldCentric;
	}
	
	public String toString(){
		return "Message:\tH-Drive";
	}
}
