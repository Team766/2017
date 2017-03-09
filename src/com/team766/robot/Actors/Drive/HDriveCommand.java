package com.team766.robot.Actors.Drive;

import lib.ConstantsFileReader;
import lib.Message;
import interfaces.SubActor;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.HDrive;

public class HDriveCommand extends CommandBase{
	HDrive command;
	
	public double left, right, center, norm;
	private double lockHeading;
	
	public HDriveCommand(Message m){
		command = (HDrive)m;
		lockHeading = Drive.getAngle();
	}

	@Override
	public void update() {
		double heading = command.getHeading();
		if(command.isFieldCentric()){
			left = getVert() + heading;
			right = getVert() - heading;	
			center = (6.3/5.3) * getHoriz();
		}else{
			left = command.getJoyY() + heading;
			right = command.getJoyY() - heading;	
			center = (6.3/5.3) * command.getJoyX();
		}
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
		if(command.getHeading() == 0){
			Drive.setLeft((ConstantsFileReader.getInstance().get("LeftDriveFeedTerm") * round2D(left)) + ((lockHeading - Drive.getAngle()) * ConstantsFileReader.getInstance().get("DriveCorrectionP")));
			Drive.setRight((ConstantsFileReader.getInstance().get("RightDriveFeedTerm") * round2D(right)) - ((lockHeading - Drive.getAngle()) * ConstantsFileReader.getInstance().get("DriveCorrectionP")));
		}else{
			Drive.setLeft(round2D(left));
			Drive.setRight(round2D(right));
		}
		Drive.setCenter(round2D(center));
		
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
