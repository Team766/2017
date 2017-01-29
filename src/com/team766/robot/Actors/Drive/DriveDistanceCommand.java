package com.team766.robot.Actors.Drive;

import lib.Message;

import com.team766.lib.CommandBase;
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
		Drive.distancePID.setSetpoint(command.getDistance());
		
		Drive.anglePID.setSetpoint(0.0);
	}

	@Override
	public void update() {
		Drive.distancePID.calculate(Drive.avgDist(), false);
		Drive.anglePID.calculate(Drive.getAngle(),false);
		
		if(!doneDrivingStraight){
			Drive.setLeft(Drive.distancePID.getOutput() - Drive.anglePID.getOutput());
			Drive.setRight(Drive.distancePID.getOutput() + Drive.anglePID.getOutput());
			
			if(Drive.distancePID.isDone()){
				doneDrivingStraight = true;
				Drive.anglePID.setSetpoint(command.getAngle());
			}
		}
		else{
			Drive.setLeft(-Drive.anglePID.getOutput());
			Drive.setRight(Drive.anglePID.getOutput());
			
			if(Drive.anglePID.isDone())
				done = true;
		}
		
		
		
	}

	@Override
	public void stop() {
		Drive.setDrive(0);
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
