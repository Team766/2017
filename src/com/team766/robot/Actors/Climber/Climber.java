package com.team766.robot.Actors.Climber;

import com.team766.robot.HardwareProvider;

import interfaces.EncoderReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Climber extends Actor {
	private boolean commandFinished;
	
	SpeedController climberMotor = HardwareProvider.getInstance().getClimber();
	EncoderReader climberEncoder = HardwareProvider.getInstance().getClimberEncoder();
	
	Message currentMessage;
	SubActor currentCommand;
	


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
		return "Actor: \tClimber";
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
	
	protected double getClimberMotor(){
		return climberMotor.get();
	}
	
	protected double getClimberEncoder(){
		return climberEncoder.get();
	}
	
	protected void setClimberMotor(double value){
		climberMotor.set(value);
	}
	
	

}
