package com.team766.robot.Actors.Drive;

import lib.Message;
import interfaces.SubActor;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.HDrive;

public class HDriveCommand implements SubActor{
	HDrive command;
	
	public HDriveCommand(Message m){
		command = (HDrive)m;
	}

	@Override
	public void update() {
		double heading = command.getHeading();
		CommandBase.Drive.setLeft(round2D(getVert()) + heading);
		CommandBase.Drive.setRight(round2D(getVert()) - heading);
		CommandBase.Drive.setCenter(round2D(getHoriz()));		
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
				(Math.cos(CommandBase.Drive.getGyroAngleInRadians())), (-Math.sin(CommandBase.Drive.getGyroAngleInRadians())));
	}
	
	private double getVert(){
		return dot(command.getJoyX(), command.getJoyY(), 
				(Math.sin(CommandBase.Drive.getGyroAngleInRadians())), (Math.cos(CommandBase.Drive.getGyroAngleInRadians())));
	}
	
	//Rounds to two decimal places
	private double round2D(double in){
		return Math.round(in * 100.0)/100.0;
	}
	
	private double dot(double x, double y, double x1, double y1){
		return (x * x1) + (y * y1);
	}
	
}
