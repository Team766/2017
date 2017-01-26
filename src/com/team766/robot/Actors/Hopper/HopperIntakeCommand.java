package com.team766.robot.Actors.Hopper;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.HopperIntake;

import lib.Message;

public class HopperIntakeCommand extends CommandBase{
	
	HopperIntake command;
	boolean done;
	boolean sensor;
	
	public HopperIntakeCommand(Message m){
		command = (HopperIntake)m;
		done = false;
	}

	public void update() {
		sensor = Hopper.getHopperSensor();
		if(!sensor)
			Hopper.setHopperMotor(1.0);
		else
			done = true;
	}

	public void stop() {
	}

	public boolean isDone() {
		Hopper.setHopperMotor(0.0);
		return done;
	}

}
