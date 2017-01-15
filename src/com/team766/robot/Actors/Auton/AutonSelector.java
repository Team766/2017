package com.team766.robot.Actors.Auton;

import lib.Actor;
import lib.LogFactory;

import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.robot.Constants;

public class AutonSelector extends Actor{
	
	private int autonMode;
	
	public AutonSelector(int mode){
		this.autonMode = mode;
	}
	
	@Override
	public void init() {
		System.out.println("Init!");
		acceptableMessages = new Class[]{DriveStatusUpdate.class};
	}
	
	@Override
	public void run() {
		switch(Constants.AUTONS[autonMode]){
			case "None":
				System.out.println("Auton: None");
				LogFactory.getInstance("General").print("Auton: None");
				break;
			default:
				System.out.println("Auton: Failed to select auton");
				LogFactory.getInstance("General").print("Auton: Failed to select auton");
				break;
		}
	}
	
	public void step(){
	}

	@Override
	public String toString() {
		return "Actor:\tAutonSelector";
	}
}
