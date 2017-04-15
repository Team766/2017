package com.team766.robot.Actors.Hopper;

import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.SetHopperState;
import com.team766.lib.Messages.SetHopperState.State;
import com.team766.robot.Constants;

public class SetHopperStateCommand extends CommandBase{

	private SetHopperState command;
	State state_;
	private boolean done;
	private double startTime;
	private boolean initilized;
	
	public SetHopperStateCommand(Message message){
		command = (SetHopperState)message;
		done = false;
		initilized = false;
		state_ = command.getHopperState();
	}
	
	@Override
	public void update() {
		switch(state_){
			case Store:
				System.out.println("Storing!");
				GearPlacer.setTopOpener(false);
				Hopper.setIntakeFlap(false);
				Hopper.setExhaustFlap(false);
				Hopper.setHopperMotor(0.0);
				done = true;
				break;
				
			case Intake:
				if(!initilized){
					startTime = System.currentTimeMillis();
					initilized = true;
				}
				GearPlacer.setTopOpener(true);
				Hopper.setIntakeFlap(true);
				Hopper.setExhaustFlap(false);
				if((System.currentTimeMillis() - startTime) < (Constants.HopperRunTime * 1000.0)){
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
