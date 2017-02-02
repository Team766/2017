package com.team766.robot.Actors.Drive;

import lib.Message;
import interfaces.SubActor;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.HDrive;

public class HDriveCommand extends CommandBase{
	HDrive command;
	
	public HDriveCommand(Message m){
		command = (HDrive)m;
	}

	@Override
	public void update() {
		double heading = command.getHeading();
		Drive.setLeft(round2D(getVert()) + heading);
		Drive.setRight(round2D(getVert()) - heading);
		Drive.setCenter(round2D(getHoriz()));		
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
