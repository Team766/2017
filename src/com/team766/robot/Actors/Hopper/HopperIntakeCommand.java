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
		if(!sensor){
			//System.out.println("no sensor");
			Hopper.setHopperMotor(1.0);
		}
		else{
			//System.out.println("Has sensor");
			done = true;
		}
	}

	public void stop() {
		//System.out.println("Stop");
		Hopper.setHopperMotor(0.0);
	}

	public boolean isDone() {
		return done;
	}

}
