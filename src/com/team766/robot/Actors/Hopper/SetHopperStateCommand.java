package com.team766.robot.Actors.Hopper;

import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.SetHopperState;
import com.team766.lib.Messages.SetHopperState.State;

public class SetHopperStateCommand extends CommandBase{

	public SetHopperState command;
	private boolean done;
	private long startTime, currTime;
	
	public SetHopperStateCommand(Message command){
		this.command = (SetHopperState)command;
		done = false;
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void update() {
		currTime = System.currentTimeMillis();
		switch(command.getHopperState()){
			case Store:
				GearPlacer.setTopOpener(false);
				Hopper.setIntakeFlap(false);
				Hopper.setExhaustFlap(false);
				Hopper.setHopperMotor(0.0);
				break;
			case Intake:
				GearPlacer.setTopOpener(true);
				Hopper.setIntakeFlap(true);
				Hopper.setExhaustFlap(false);
				if(currTime - startTime < 5000)
					Hopper.setHopperMotor(1.0);
				else
					done = true;
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
