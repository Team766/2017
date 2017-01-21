package com.team766.robot.Actors.Drive;

import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.MotorCommand;

public class MotorSubCommand extends CommandBase{

	MotorCommand command;
		
	public MotorSubCommand(Message command){
		this.command = (MotorCommand)command;
	}
	
	public void update() {
		switch(command.getMotor()){
			case leftDrive:
				Drive.setLeft(command.getValue());
				break;
			case rightDrive:
				Drive.setRight(command.getValue());
				break;
			default:
				System.out.println("Motor not recognized!");
				break;
		}
	}
	

	public boolean isDone() {
		return true;
	}

	public void stop() {
	}

}
