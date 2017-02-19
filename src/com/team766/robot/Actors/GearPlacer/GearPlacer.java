package com.team766.robot.Actors.GearPlacer;

import com.team766.lib.Messages.CheesyDrive;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.MotorSubCommand;

import interfaces.SolenoidController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class GearPlacer extends Actor{
	
	SolenoidController topOpener = HardwareProvider.getInstance().getGearPlacerOpener();
	SolenoidController placer = HardwareProvider.getInstance().getGearPlacer();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	public void init() {
		acceptableMessages = new Class[]{UpdateGearCollector.class};
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
				
				if(currentMessage instanceof UpdateGearCollector){
					UpdateGearCollector gearMessage = (UpdateGearCollector)currentMessage;
					this.setTopOpener(gearMessage.getTop());
					this.setPlacer(gearMessage.getBottom());
				}
					
			}
			step();
		}

	}

	public String toString() {
		return "Actor:\tGearPlacer";
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
	
	protected boolean getOpener(){
		return topOpener.get();
	}
	
	protected boolean getPlacer(){
		return placer.get();
	}
	
	public void setTopOpener(boolean open){
		topOpener.set(open);
	}
	
	protected void setPlacer(boolean on){
		placer.set(on);
	}

}
