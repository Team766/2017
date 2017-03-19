package com.team766.robot.Actors.Auton;

import lib.Actor;
import lib.LogFactory;

import com.team766.lib.Messages.DriveDistance;
import com.team766.lib.Messages.DriveIntoPeg;
import com.team766.lib.Messages.DriveIntoPegStop;
import com.team766.lib.Messages.DrivePath;
import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.lib.Messages.RequestDropPeg;
import com.team766.lib.Messages.SnapToAngle;
import com.team766.lib.Messages.StartTrackingPeg;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.lib.Messages.VisionStatusUpdate;
import com.team766.robot.Constants;

public class AutonSelector extends Actor{
	
	private int autonMode;
	
	public AutonSelector(int mode){
		this.autonMode = mode;
	}
	
	@Override
	public void init() {
		acceptableMessages = new Class[]{DriveStatusUpdate.class, VisionStatusUpdate.class};
	}

	
	@Override
	public void run() {
		switch(Constants.AUTONS[autonMode]){
			case "None":
				System.out.println("Auton: None");
				LogFactory.getInstance("General").print("Auton: None");
//				waitForMessage(new DriveIntoPegStop(), DriveStatusUpdate.class);
				break;
			case "DriveToPeg":
				System.out.println("Auton: DriveToPeg");
				LogFactory.getInstance("General").print("Auton: DriveToPeg");
				waitForMessage(new DrivePath("ToPegPath", false), DriveStatusUpdate.class);
				waitForMessage(new SnapToAngle(), DriveStatusUpdate.class);
				break;	
			case "BoilerPath":
				System.out.println("Auton: BoilerPath");
				LogFactory.getInstance("General").print("Auton: BoilerPath");
				sendMessage(new DrivePath("BoilerPath", false));
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
//				sendMessage(new DrivePath("StraightToPegPath", false));
//				waitForMessage(new DrivePath("StraightToPegPath", false), DriveStatusUpdate.class);
				waitForMessage(new DriveDistance(-10.0, 0), DriveStatusUpdate.class);
				sendMessage(new UpdateGearCollector(false, true));
				break;
			case "FlipDriveToPegPath":
				System.out.println("Auton: FlipDriveToPegPath");
				LogFactory.getInstance("General").print("auton: FlipDriveToPegPath");
				waitForMessage(new DrivePath("ToPegPath", true), DriveStatusUpdate.class);
				waitForMessage(new SnapToAngle(), DriveStatusUpdate.class);
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
