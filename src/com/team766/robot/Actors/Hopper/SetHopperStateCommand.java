package com.team766.robot.Actors.Hopper;

import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.SetHopperState;
import com.team766.lib.Messages.SetHopperState.State;

public class SetHopperStateCommand extends CommandBase{

	public SetHopperState command;
	private boolean done;
	
	public SetHopperStateCommand(Message command){
		this.command = (SetHopperState)command;
		done = false;
	}
	
	@Override
	public void update() {
		switch(command.getHopperState()){
			case Store:
				GearPlacer.setTopOpener(false);
				Hopper.setIntakeFlap(false);
				Hopper.setExhaustFlap(false);
				Hopper.setHopperMotor(0.0);
				done = true;
				break;
			case Intake:
				GearPlacer.setTopOpener(true);
				Hopper.setIntakeFlap(true);
				Hopper.setExhaustFlap(false);
				if(!Hopper.isBallPresent()){
					Hopper.setHopperMotor(1.0);
				}else{
					done = true;
					Hopper.setHopperMotor(0.0);
				}
				break;
			case Exhaust:
				GearPlacer.setTopOpener(false);
				Hopper.setIntakeFlap(false);
				Hopper.setExhaustFlap(true);
				Hopper.setHopperMotor(1.0);
				break;
		}
		
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
