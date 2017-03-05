package com.team766.robot.Actors;

import interfaces.JoystickReader;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.LogFactory;
import lib.Scheduler;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.CheesyDrive;
import com.team766.lib.Messages.ClimbDeploy;
import com.team766.lib.Messages.HDrive;
import com.team766.lib.Messages.HopperSetRoller;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.ResetDriveAngle;
import com.team766.lib.Messages.SetHopperState;
import com.team766.lib.Messages.TrackPeg;
import com.team766.lib.Messages.UpdateClimber;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.Buttons;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Robot;

public class OperatorControl extends Actor {
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoystick();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoystick();
	JoystickReader jBox = HardwareProvider.getInstance().getBoxJoystick();
	
	private double previousLeft, previousRight, previousHeading;
	
	private double[] leftAxis = new double[4];
	private double[] rightAxis = new double[4];
	private boolean[] prevPress = new boolean[13];  //previous press array
	private boolean toggleFieldCentric;
		
	public void init() {
		acceptableMessages = new Class[]{};
		previousLeft = 0;
		previousRight = 0;
		previousHeading = 0;
		toggleFieldCentric = false;
		
		
		//Stop autonomous movements
		sendMessage(new HDrive(0,0,0, false));
	}
	
	/*
	 *	Joystick values for RawAxis(1) are flipped
	 *	Forward is -, backwards is +
	 */
	public void run() {
		while(Robot.getState() == Robot.GameState.Teleop){
			
			leftAxis[0] = (Math.abs(jLeft.getRawAxis(0)) > Constants.leftAxisDeadband)? curveJoystick(jLeft.getRawAxis(0)) : 0;
			leftAxis[1] = (Math.abs(jLeft.getRawAxis(1)) > Constants.leftAxisDeadband)? curveJoystick(-jLeft.getRawAxis(1)) : 0;			
			leftAxis[2] = (Math.abs(jLeft.getRawAxis(2)) > Constants.leftAxisDeadband)? curveJoystick(-jLeft.getRawAxis(2)) : 0;
			leftAxis[3] = (Math.abs(jLeft.getRawAxis(3)) > Constants.leftAxisDeadband)? jLeft.getRawAxis(3) : 0;
			
			rightAxis[0] = (Math.abs(jRight.getRawAxis(0)) > Constants.rightAxisDeadband)? curveJoystick(jRight.getRawAxis(0)) : 0;
			rightAxis[1] = (Math.abs(jRight.getRawAxis(1)) > Constants.rightAxisDeadband)? -jRight.getRawAxis(1) : 0;
//			rightAxis[2] = (Math.abs(jRight.getRawAxis(2)) > Constants.rightAxisDeadband)? jRight.getRawAxis(2) : 0;
//			rightAxis[3] = (Math.abs(jRight.getRawAxis(3)) > Constants.rightAxisDeadband)? -jRight.getRawAxis(3) : 0;
			
			
			if(Constants.TANK_DRIVE){
				if(previousLeft != leftAxis[1])
					sendMessage(new MotorCommand(leftAxis[1], MotorCommand.Motor.leftDrive));
				if(previousRight != jRight.getRawAxis(1))
					sendMessage(new MotorCommand(rightAxis[1], MotorCommand.Motor.rightDrive));
				
				previousLeft = leftAxis[1];
				previousRight = rightAxis[1];
			}else{
				switch((int)ConstantsFileReader.getInstance().get("DriveScheme")){
					//2 joysicks
					case 0:
						if(previousLeft != leftAxis[0] ||
							previousHeading != rightAxis[0] ||
							previousRight != leftAxis[1]){
							sendMessage(new HDrive(leftAxis[0], leftAxis[1], rightAxis[0], toggleFieldCentric));
						}
		
						previousLeft = leftAxis[0];
						previousRight = leftAxis[1];
						previousHeading = rightAxis[0];
						break;
					//1 joysick - twist
					case 1:
						if(previousLeft != leftAxis[0] ||
							previousHeading != leftAxis[3] ||
							previousRight != leftAxis[1]){
							sendMessage(new  HDrive(leftAxis[0], leftAxis[1], leftAxis[3], toggleFieldCentric));
						}
		
						previousLeft = leftAxis[0];
						previousRight = leftAxis[1];
						previousHeading = leftAxis[3];
						break;
				}
			}
			

			
			//button for load balls(prevPress[0])
			if(!prevPress[0] && jBox.getRawButton(Buttons.loadBalls))
				sendMessage(new SetHopperState(SetHopperState.State.Intake));
			prevPress[0] = jBox.getRawButton(Buttons.loadBalls);
			
			
			//button for score balls(prevPress[1])
			if(!prevPress[1] && jBox.getRawButton(Buttons.scoreBalls))
				sendMessage(new SetHopperState(SetHopperState.State.Exhaust));
			prevPress[1] = jBox.getRawButton(Buttons.scoreBalls);
			
			
			//button for roller forward(prevPress[2])
			if(!prevPress[2] && jBox.getRawButton(Buttons.rollerForwards))
				sendMessage(new HopperSetRoller(jBox.getRawButton(Buttons.rollerForwards)));
			prevPress[2] = jBox.getRawButton(Buttons.rollerForwards);
			
			
			//button for roller backward(prevPress[3])
			if(!prevPress[3] && jBox.getRawButton(Buttons.rollerBackwards))
				sendMessage(new HopperSetRoller(jBox.getRawButton(Buttons.rollerForwards)));
			prevPress[3] = jBox.getRawButton(Buttons.rollerBackwards);
			
			
			//button for store ball(prevPress[4])
			if(!prevPress[4] && jBox.getRawButton(Buttons.store))
				sendMessage(new SetHopperState(SetHopperState.State.Store));
			prevPress[4] = jBox.getRawButton(Buttons.store);
			
			
			//button for load gears(prevPress[5])
			if(!prevPress[5] && jBox.getRawButton(Buttons.loadGears))
				sendMessage(new UpdateGearCollector(false, true));
			prevPress[5] = jBox.getRawButton(Buttons.loadGears);
			
			
			//button for score gears(prevPress[6])
			if(!prevPress[6] && jBox.getRawButton(Buttons.scoreGears))
				sendMessage(new UpdateGearCollector(true, false));
			prevPress[6] = jBox.getRawButton(Buttons.scoreGears);
			
			
			//button for deploy climb(prevPress[7])
			if(!prevPress[7] && jBox.getRawButton(Buttons.climbDeploy))
				sendMessage(new ClimbDeploy(jBox.getRawButton(Buttons.climbDeploy)));
			prevPress[7] = jBox.getRawButton(Buttons.climbDeploy);
			
			
			//button for up climb(prevPress[8])
			if(!prevPress[8] && jBox.getRawButton(Buttons.climbUp))
				sendMessage(new UpdateClimber(true));
			prevPress[8] = jBox.getRawButton(Buttons.climbUp);
			
			
			//button for down climb(prevPress[9])
			if(!prevPress[9] && jBox.getRawButton(Buttons.climbDown))
				sendMessage(new UpdateClimber(false));
			prevPress[9] = jBox.getRawButton(Buttons.climbDown);
			
			//button for disable field centric(prevPress[10])
			if(!prevPress[10] && jLeft.getRawButton(Buttons.disableFieldCentric))
				toggleFieldCentric = !toggleFieldCentric;
			prevPress[10] = jLeft.getRawButton(Buttons.disableFieldCentric);
			
			if(!prevPress[10] && jRight.getRawButton(Buttons.resetAngle))
				sendMessage(new ResetDriveAngle(0.0));
			prevPress[11] = jRight.getRawButton(Buttons.resetAngle);
			
			
			//button for track peg
			if(!prevPress[12] && jLeft.getRawButton(Buttons.trackPeg))
				sendMessage(new TrackPeg());	
			prevPress[12] = jLeft.getRawButton(Buttons.trackPeg);
			
//			
			
//			LogFactory.getInstance("General").printPeriodic("JoystickValues: " + jLeft.getRawAxis(1) + " R:" + jRight.getRawAxis(1), "Joysticks", 200);
			
			itsPerSec++;
			sleep();
		}
		Scheduler.getInstance().remove(this);
	}
	
	private double curveJoystick(double value){
		return value * Math.abs(value);
//		return Math.pow(value, 3);
	}
	
	public void step(){
	}
	
	public String toString(){
		return "Actor:\tOperator Control";
	}
	
	private double dotProduct(double x1, double y1, double x2, double y2){
		return (x1 * x2) + (y1 * y2);
	}

}
