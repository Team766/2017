package com.team766.robot.Actors.GearPlacer;

import interfaces.DigitalInputReader;
import interfaces.SolenoidController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

import com.team766.lib.Messages.GearCollectorUpdate;
import com.team766.lib.Messages.RequestDropPeg;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.HardwareProvider;

public class GearPlacer extends Actor{
	
	SolenoidController topOpener = HardwareProvider.getInstance().getGearPlacerOpener();
	SolenoidController placer = HardwareProvider.getInstance().getGearPlacer();
	DigitalInputReader isGear = HardwareProvider.getInstance().getGearSensor();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	public void init() {
		acceptableMessages = new Class[]{UpdateGearCollector.class, Stop.class, RequestDropPeg.class};
	}
	
	public void iterate(){
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			if(currentMessage instanceof UpdateGearCollector){
				currentCommand = null;
				UpdateGearCollector gearMessage = (UpdateGearCollector)currentMessage;
				setTopOpener(gearMessage.getTop());
				setPlacer(gearMessage.getBottom());
				commandFinished = true;
			}
			else if(currentMessage instanceof RequestDropPeg){
				currentCommand = new SubActor(){
					private boolean seenPeg = false;
					@Override
					public void update() {
						//Wait for peg to latch
						if(isPegPresent()){
							setTopOpener(false);
							setPlacer(true);
							seenPeg = true;
						}
					}

					@Override
					public void stop() {
					}

					@Override
					public boolean isDone() {
						return seenPeg && !isPegPresent();
					}
				};
			}
			else if(currentMessage instanceof Stop)
				stopCurrentCommand();
				
		}
		
		step();
		
		sendMessage(new GearCollectorUpdate(commandFinished, currentMessage));
	}
	
	public void run() {
		while(enabled){
			iterate();
			sleep();
		}
		
		//Kill all processes
		stopCurrentCommand();

	}

	public String toString() {
		return "Actor:\tGearPlacer";
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
	
	public boolean isPegPresent(){
		return isGear.get();
	}

}
