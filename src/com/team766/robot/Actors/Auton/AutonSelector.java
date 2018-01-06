package com.team766.robot.Actors.Auton;

import lib.Actor;
import lib.ConstantsFileReader;
import lib.LogFactory;

import com.team766.lib.Messages.DriveDistance;
import com.team766.lib.Messages.DriveIntoPeg;
import com.team766.lib.Messages.DrivePath;
import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.lib.Messages.SnapToAngle;
import com.team766.lib.Messages.StartTrackingPeg;
import com.team766.lib.Messages.TurnAngle;
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
	public void iterate(){
		switch(Constants.AUTONS[autonMode]){
			case "None":
				System.out.println("Auton: None");
				LogFactory.getInstance("General").print("Auton: None");
				break;
			case "RightPegPath":
				System.out.println("Auton: RightPegPath");
				LogFactory.getInstance("General").print("Auton: RightPegPath");
				
				rightPegPath();
				break;	
			case "BoilerPath":
				System.out.println("Auton: BoilerPath");
				LogFactory.getInstance("General").print("Auton: BoilerPath");
				
				sendMessage(new DrivePath("BoilerPath", false));
				break;	
			case "StraightToPegVision":
				System.out.println("Auton: Using Vision to Drive straight to peg");
				LogFactory.getInstance("General").print("Auton: StraightToPegVision");
				
				waitForMessage(new StartTrackingPeg(), VisionStatusUpdate.class);
				System.out.println("Done alligning with Peg!");
				sendMessage(new DriveIntoPeg());
				break;
			case "StraightToPegDrive":
				System.out.println("Auton: StraightToPegDrive");
				LogFactory.getInstance("General").print("Auton: StraightToPegDrive");
				
				straightToPegDrive();
				break;
			case "CrossLineDrive":
				System.out.println("Auton: CrossLineDrive");
				LogFactory.getInstance("General").print("Auton: CrossLineDrive");
				
				waitForMessage(new DriveDistance(-7.0, 0), DriveStatusUpdate.class);
				break;
			case "LeftPegDrive":
				System.out.println("Auton: LeftPegDrive");
				LogFactory.getInstance("General").print("auton: LeftPegDrive");
				
				leftPegDrive();
				break;
			case "RightPegDrive":
				System.out.println("Auton: RightPegDrive");
				LogFactory.getInstance("General").print("Auton: RightPegDrive");
				
				rightPegDrive();
				break;
			default:
				System.out.println("Auton: Failed to select auton");
				LogFactory.getInstance("General").print("Auton: Failed to select auton");
				break;
		}
	}
	
	@Override
	public void run() {
		iterate();
	}
	
	public void step(){
	}
	
	private void rightPegPath(){
		waitForMessage(new DrivePath("ToPegPath", false), DriveStatusUpdate.class);
		waitForMessage(new SnapToAngle(), DriveStatusUpdate.class);
		//Drop gear
		sendMessage(new UpdateGearCollector(false, true));
		//Drive back
		waitForMessage(new DriveDistance(1.0,0), DriveStatusUpdate.class);
	}
	
	private void straightToPegDrive(){
		//sendMessage(new DrivePath("StraightToPegPath", false));
		//waitForMessage(new DrivePath("StraightToPegPath", false), DriveStatusUpdate.class);
					
		waitForMessage(new DriveDistance(ConstantsFileReader.getInstance().get("StraightDist"), 0), DriveStatusUpdate.class);
		
		//Wait
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
		
		//Drop Peg
		sendMessage(new UpdateGearCollector(false, true));
		//Drive back
		sendMessage(new DriveDistance(2.0,0));
	}
	
	private void leftPegDrive(){
//		waitForMessage(new DrivePath("ToPegPath", true), DriveStatusUpdate.class);
//		waitForMessage(new SnapToAngle(), DriveStatusUpdate.class);
		
		waitForMessage(new DriveDistance(ConstantsFileReader.getInstance().get("SideForwardDist"), 0), DriveStatusUpdate.class);
		waitForMessage(new TurnAngle(-ConstantsFileReader.getInstance().get("TurnToPeg")), DriveStatusUpdate.class);
		waitForMessage(new DriveDistance(ConstantsFileReader.getInstance().get("StraightToScoreDist"), 0), DriveStatusUpdate.class);
		//waitForMessage(new DrivePath("ToPegPath", false), DriveStatusUpdate.class);
		
		//Wait
//		try {
//			Thread.sleep(400);
//		} catch (InterruptedException e) {
//		}
		
		//Drop Gear
		sendMessage(new UpdateGearCollector(false, true));
		
		//Wait
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
		
		//Drive back
		waitForMessage(new DriveDistance(2.0,0), DriveStatusUpdate.class);
	}
	
	private void rightPegDrive(){
		waitForMessage(new DriveDistance(ConstantsFileReader.getInstance().get("SideForwardDist"), 0), DriveStatusUpdate.class);
		waitForMessage(new TurnAngle(ConstantsFileReader.getInstance().get("TurnToPeg")), DriveStatusUpdate.class);
		waitForMessage(new DriveDistance(ConstantsFileReader.getInstance().get("StraightToScoreDist"), 0), DriveStatusUpdate.class);
		//waitForMessage(new DrivePath("ToPegPath", false), DriveStatusUpdate.class);
		
		//Wait
//		try {
//			Thread.sleep(400);
//		} catch (InterruptedException e) {
//		}
		
		//Drop Gear
		sendMessage(new UpdateGearCollector(false, true));
		
		//Wait
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
		
		//Drive back
		waitForMessage(new DriveDistance(2.0,0), DriveStatusUpdate.class);
	}

	@Override
	public String toString() {
		return "Actor:\tAutonSelector";
	}
}