package com.team766.robot.Actors.Drive;

import interfaces.SubActor;
import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.CheesyDrive;
import com.team766.robot.Constants;

public class CheesyDriveCommand extends CommandBase{

	CheesyDrive command;
	private boolean done;
	
	public CheesyDriveCommand(Message message){
		command = (CheesyDrive)message;
		done = false;
	}
	
	public void update() {
		Drive.angularVelocity.setSetpoint(command.getAngularVelocity() * (Constants.maxAngularVelocity));
		Drive.linearVelocity.setSetpoint(command.getLinearVelocity() * (Constants.maxLinearVelocity));
		
		Drive.angularVelocity.calculate(Drive.getAngularRate(), true);
		Drive.linearVelocity.calculate(Drive.avgLinearRate(), true);

		
		if(!command.getQuickTurn()){
			Drive.setLeft(Drive.linearVelocity.getOutput() + Drive.angularVelocity.getOutput());
			Drive.setRight(Drive.linearVelocity.getOutput() - Drive.angularVelocity.getOutput());
		}else{
			Drive.setLeft(Drive.angularVelocity.getOutput());
			Drive.setRight(-Drive.angularVelocity.getOutput());
		}
		
		done = true;
	}

	public void stop() {
	}

	public boolean isDone() {
		return done;
	}

}
