package com.team766.robot.Actors.Auton;

import lib.Actor;
import lib.LogFactory;

import com.team766.lib.Messages.DriveIntoPeg;
import com.team766.lib.Messages.DrivePath;
import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.lib.Messages.SnapToAngle;
import com.team766.lib.Messages.StartTrackingPeg;
import com.team766.lib.Messages.TrackPeg;
import com.team766.lib.Messages.VisionStatusUpdate;
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
			case "DriveToPeg":
				System.out.println("Auton: DriveToPeg");
				LogFactory.getInstance("General").print("Auton: DriveToPeg");
				waitForMessage(new DrivePath("ToPegPath"), DriveStatusUpdate.class);
				waitForMessage(new SnapToAngle(), DriveStatusUpdate.class);
				break;	
			case "BoilerPath":
				System.out.println("Auton: BoilerPath");
				LogFactory.getInstance("General").print("Auton: BoilerPath");
				sendMessage(new DrivePath("BoilerPath"));
				break;	
			case "StraightToPeg":
				System.out.println("Auton: Using Vision to Drive straight to peg");
				LogFactory.getInstance("General").print("Auton: StraightToPeg");
				waitForMessage(new StartTrackingPeg(), VisionStatusUpdate.class);
				System.out.println("Done alligning with Peg!");
				sendMessage(new DriveIntoPeg());
				break;
			case "StraightToPegPath":
				System.out.println("Auton: StraightToPegPath");
				LogFactory.getInstance("General").print("Auton: StraightToPegPath");
				sendMessage(new DrivePath("StraightToPegPath"));
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
