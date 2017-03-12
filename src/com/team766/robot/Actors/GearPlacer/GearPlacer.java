package com.team766.robot.Actors.GearPlacer;

import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.lib.Messages.RequestDropPeg;
import com.team766.lib.Messages.SnapToAngle;
import com.team766.lib.Messages.StartTrackingPeg;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.TrackPeg;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.AnalogInputReader;
import interfaces.SolenoidController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class GearPlacer extends Actor{
	
	SolenoidController topOpener = HardwareProvider.getInstance().getGearPlacerOpener();
	SolenoidController placer = HardwareProvider.getInstance().getGearPlacer();
	AnalogInputReader isGear = HardwareProvider.getInstance().getGearSensor();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	public void init() {
		acceptableMessages = new Class[]{UpdateGearCollector.class, DriveStatusUpdate.class, RequestDropPeg.class};
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
			sleep();
		}

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
	
	public double getPegSensorVoltage(){
		return isGear.getVoltage();
	}
	
	public boolean isPegPresent(){
		return getPegSensorVoltage() < Constants.PHOTOGATE_STEP_VOLTAGE;
	}

}
