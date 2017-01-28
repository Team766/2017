package com.team766.lib.Messages;

import lib.Message;

public class HDrive implements Message{
	private double x, y, head;
	
	public HDrive(double joyX, double joyY, double heading){
		x = joyX;
		y = joyY;
		head = heading;
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
	
	public String toString(){
		return "Message:\tH-Drive";
	}
}
