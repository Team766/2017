package com.team766.robot.Actors.Climber;

import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.ClimbDeploy;
import com.team766.lib.Messages.UpdateClimber;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.HardwareProvider;

import interfaces.EncoderReader;
import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;

public class Climber extends Actor {
	private boolean commandFinished;
	
	private double motorSpeed = 1.0;
	private double maxMotorSpeed = 10;
	private boolean currentlyOut;
	
	SpeedController climberMotor = HardwareProvider.getInstance().getClimber();
	EncoderReader climberEncoder = HardwareProvider.getInstance().getClimberEncoder();
	SolenoidController climberSolenoidOut = HardwareProvider.getInstance().getClimberDeployOut();
	SolenoidController climberSolenoidIn = HardwareProvider.getInstance().getClimberDeployIn();
	
	Message currentMessage;
	SubActor currentCommand;
	
	@Override
	public void init() {
		acceptableMessages = new Class[]{UpdateClimber.class, ClimbDeploy.class, Stop.class};
		currentlyOut = true;
		setClimberDeploy(currentlyOut);
	}

	@Override
	public void iterate(){
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			if(currentMessage instanceof UpdateClimber){
				UpdateClimber climberMessage = (UpdateClimber)currentMessage;
				//this.setClimberMotor(climberMessage.getClimb());
				if(climberMessage.getClimb())
					this.setClimberMotor(ConstantsFileReader.getInstance().get("climberSpeed"));
				else
					this.setClimberMotor(0.0);
			}
			else if(currentMessage instanceof Stop)
				stopCurrentCommand();
			else if(currentMessage instanceof ClimbDeploy){
				ClimbDeploy climberMessage = (ClimbDeploy)currentMessage;
				if(climberMessage.isToggle())
					toggleClimberDeploy();
				else
					this.setClimberDeploy(!climberMessage.getClimbDeploy());
			}
		}
		step();
	}
	
	@Override
	public void run() {
		while(enabled){
			iterate();
			sleep();
		}
		
		//Kill processes
		stopCurrentCommand();
	}

	@Override
	public String toString() {
		return "Actor: \tClimber";
	}

	@Override
	public void step() {
		if(currentCommand != null){
			if(currentCommand.isDone()){
				stopCurrentCommand();
			}else{
				currentCommand.update();
			}
		}
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null)
			currentCommand.stop();
		commandFinished = true;
		currentCommand = null;
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
	
	protected void toggleClimberDeploy(){
		setClimberDeploy(!currentlyOut);
	}
	
	protected void setClimberDeploy(boolean out){
		climberSolenoidOut.set(out);
		climberSolenoidIn.set(!out);
		currentlyOut = out;
	}
}
