package com.team766.robot.Actors.Hopper;

import com.team766.lib.Messages.HopperIntake;
import interfaces.DigitalInputReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Hopper extends Actor{
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	SpeedController hopperMotor;
	DigitalInputReader hopperSensor;
	
	public void init() {
		acceptableMessages = new Class[]{HopperIntake.class};
	}
	
	public void run() {
		while(true){
			if(newMessage()){
				if(currentCommand != null)
					currentCommand.stop();
				
				commandFinished = false;
				
				currentMessage = readMessage();
				if(currentMessage == null)
					break;
			}
			step();
		}
	}

	public String toString() {
		return "Actor: \tHopper";
	}

	public void step() {
		if(currentCommand != null){
			if(currentCommand.isDone()){
				currentCommand.stop();
				commandFinished = true;
				currentCommand = null;
			}else{
				currentCommand.update();
			}
		}
	}
	
	public double getHopperMotor(){
		return hopperMotor.get();
	}
	
	public void setHopperMotor(double value){
		hopperMotor.set(value);
	}
	
	public boolean getHopperSensor(){
		return hopperSensor.get();
	}

}
