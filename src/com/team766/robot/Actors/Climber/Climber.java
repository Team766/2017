package com.team766.robot.Actors.Climber;

import com.team766.lib.Messages.UpdateClimber;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.HardwareProvider;




import interfaces.EncoderReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Climber extends Actor {
	private boolean commandFinished;
	
	private double motorSpeed = 1;
	private double maxMotorSpeed = 10;
	
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
				
				if(currentMessage instanceof UpdateClimber){
					UpdateClimber climberMessage = (UpdateClimber)currentMessage;
					//this.setClimberMotor(climberMessage.getClimb());
					if(climberMessage.getClimb() == true)
						this.setClimberMotor(motorSpeed);
					else
						this.setClimberMotor(0.0);
				}
				
			}
			step();
		}
	}

	@Override
	public void init() {
		acceptableMessages = new Class[]{UpdateClimber.class};
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
	
	protected void setClimberMotor(double d){
		climberMotor.set(d);
	}
	
	

}
