package com.team766.robot.Actors.Drive;

import lib.LogFactory;
import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.MotorCommand;

public class MotorSubCommand extends CommandBase{

	MotorCommand command;
	boolean done;
	
	public MotorSubCommand(Message command){
		this.command = (MotorCommand)command;
		done = false;
	}
	
	public void update() {
		switch(command.getMotor()){
			case leftDrive:
				Drive.setLeft(command.getValue());
				break;
			case rightDrive:
				Drive.setRight(command.getValue());
				break;
			case centerDrive:
				Drive.setCenter(command.getValue());
				break;
			default:
				System.out.println("Motor not recognized!");
				break;
		}
		done = true;
	}
	

	public boolean isDone() {
		return done;
	}

	public void stop() {
	}

}
