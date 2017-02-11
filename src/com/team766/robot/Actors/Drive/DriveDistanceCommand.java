package com.team766.robot.Actors.Drive;

import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.DriveDistance;
import com.team766.robot.Constants;

public class DriveDistanceCommand extends CommandBase{
	
	DriveDistance command;
	private boolean done;
	private boolean doneDrivingStraight;
	
	public DriveDistanceCommand(Message message){
		command = (DriveDistance)message;
		done = false;
		doneDrivingStraight = false;
		Drive.distancePID.setSetpoint(command.getDistance() + Drive.avgDist());
		
		Drive.anglePID.setSetpoint(0.0 + Drive.getAngle());
	}

	@Override
	public void update() {
		Drive.distancePID.calculate(Drive.avgDist(), false);
		Drive.anglePID.calculate(Drive.getAngle(),false);
		
		printDrive();
		printSetMotorVal();
		
		if(!doneDrivingStraight){
			Drive.setLeft(Drive.distancePID.getOutput() );//- Drive.anglePID.getOutput());
			Drive.setRight(Drive.distancePID.getOutput() );//+ Drive.anglePID.getOutput());
			
			if(Drive.distancePID.isDone()){
				doneDrivingStraight = true;
				Drive.anglePID.setSetpoint(command.getAngle() + Drive.getAngle());
			}
		}
		else{
			Drive.setLeft(Drive.anglePID.getOutput());
			Drive.setRight(-Drive.anglePID.getOutput());
			
			if(Drive.anglePID.isDone())
				done = true;
		}
	}

	@Override
	public void stop() {
		Drive.setDrive(0);
	}
	
	public void printDrive(){
		//System.out.println("Left distance: " + Drive.leftDist());
		//System.out.println("Right distance: " + Drive.rightDist());
		//System.out.println("Get angle: " + Drive.getAngle());
		System.out.println("Get AngularRate: " + Drive.getAngularRate());
		System.out.println("Get aveLinearRate: " + Drive.avgLinearRate());
	}
	
	public void printSetMotorVal(){
		System.out.println("distancePID: " + Drive.distancePID.getOutput());
		System.out.println("anglePID: " + Drive.anglePID.getOutput());
		
		//System.out.println("setLeft: " + (Drive.distancePID.getOutput()));
		//System.out.println("setRight: " + (Drive.distancePID.getOutput()));
		System.out.println("done driving straight: " + doneDrivingStraight);
		System.out.println("done turning: " + done);
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
