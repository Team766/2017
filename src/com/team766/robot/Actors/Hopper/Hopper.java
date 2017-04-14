package com.team766.robot.Actors.Hopper;

import com.team766.lib.Messages.HopperSetRoller;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.SetHopperState;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.MotorSubCommand;

import interfaces.AnalogInputReader;
import interfaces.DigitalInputReader;
import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Hopper extends Actor{
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	AnalogInputReader hopperSensor = HardwareProvider.getInstance().getHopperSensor();
	SpeedController hopperMotor = HardwareProvider.getInstance().getHopper();
	SolenoidController intakeFlap = HardwareProvider.getInstance().getHopperOpener();
	SolenoidController exhaustFlap = HardwareProvider.getInstance().getHopperCloser();
	
	public void init() { 
		acceptableMessages = new Class[]{SetHopperState.class, HopperSetRoller.class};
	}
	
	public void iterate(){
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			if(currentMessage instanceof SetHopperState){
				currentCommand = new SetHopperStateCommand(currentMessage);
			}else if(currentMessage instanceof Stop){
				stopCurrentCommand();
			}else if(currentMessage instanceof HopperSetRoller){
				HopperSetRoller HopperMessage = (HopperSetRoller)currentMessage;
				
				if(!HopperMessage.getOff()){
					if(HopperMessage.getForward())
						this.setHopperMotor(1.0);
					else
						this.setHopperMotor(-1.0);
				}else{
					setHopperMotor(0.0);
				}
			}
		}
		
		step();
	}
	
	public void run() {
		while(enabled){
			iterate();
			sleep();
		}
		
		//No longer enabled, kill all
		stopCurrentCommand();
	}

	public String toString() {
		return "Actor: \tHopper";
	}

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
	
	public double getHopperMotor(){
		return hopperMotor.get();
	}
	
	public void setHopperMotor(double value){
		hopperMotor.set(value);
	}
	
	public void setIntakeFlap(boolean b){
		intakeFlap.set(b);
	}
	
	public void setExhaustFlap(boolean b){
		exhaustFlap.set(b);
	}
	
	public boolean getHopperOpener(){
		return intakeFlap.get();
	}
	
	public boolean getHopperCloser(){
		return exhaustFlap.get();
	}
	
	public double getHopperSensorVoltage(){
		return hopperSensor.getVoltage();
	}
	
	public boolean isBallPresent(){
		return getHopperSensorVoltage() < Constants.PHOTOGATE_STEP_VOLTAGE;
	}

}
