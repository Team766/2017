package com.team766.robot.Actors.Hopper;


import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;
public class Hopper extends Actor{
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	
	SpeedController hopperMotor;
	@Override
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

	@Override
	public void init() {
		acceptableMessages = new Class[]{};
		
	}

	@Override
	public String toString() {
		
		return "Actor: \tHopper";
	}

	@Override
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

}
