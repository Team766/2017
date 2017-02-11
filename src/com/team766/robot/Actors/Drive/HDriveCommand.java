package com.team766.robot.Actors.Drive;

import lib.Message;
import interfaces.SubActor;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.HDrive;

public class HDriveCommand extends CommandBase{
	HDrive command;
	
	public double left, right, center, norm;
	
	public HDriveCommand(Message m){
		command = (HDrive)m;
	}

	@Override
	public void update() {
		double heading = command.getHeading();
		left = getVert() + heading;
		right = getVert() - heading;	
		center = (6.3/5.3) * getHoriz();
		/*
		Drive.setLeft(round2D(getVert()) + heading);
		Drive.setRight(round2D(getVert()) - heading);
		Drive.setCenter(round2D(getHoriz()));		
		*/
		
		norm = Math.max(Math.max(1.0, Math.abs(left)), Math.max(Math.abs(right), Math.abs(center)));
		left /= norm;
		right /= norm;
		center /= norm;
//		System.out.printf("l, r, c: %f %f %f\n", left, right, center);
		Drive.setLeft(left);
		Drive.setRight(right);
		Drive.setCenter(center);
		
	}

	@Override
	public void stop() {
	}

	/**
	 * Never done so that it can update on the angle.  New messages
	 * should be the way to update
	 */
	@Override
	public boolean isDone() {
		return false;
	}
	
	private double getHoriz(){
		return dot(command.getJoyX(), command.getJoyY(), 
				(Math.cos(Drive.getGyroAngleInRadians())), (-Math.sin(Drive.getGyroAngleInRadians())));
	}
	
	private double getVert(){
		return dot(command.getJoyX(), command.getJoyY(), 
				(Math.sin(Drive.getGyroAngleInRadians())), (Math.cos(Drive.getGyroAngleInRadians())));
	}
	
	//Rounds to two decimal places
	private double round2D(double in){
		return Math.round(in * 100.0)/100.0;
	}
	
	private double dot(double x, double y, double x1, double y1){
		return (x * x1) + (y * y1);
	}
	
}
