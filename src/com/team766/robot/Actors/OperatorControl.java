package com.team766.robot.Actors;

import interfaces.JoystickReader;
import lib.Actor;
import lib.LogFactory;
import lib.Scheduler;

import com.team766.lib.Messages.CheesyDrive;
import com.team766.lib.Messages.HDrive;
import com.team766.lib.Messages.MotorCommand;
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
	private int dropButton, collectButton;
	private boolean previousPress;
	
	private double[] leftAxis = new double[4];
	private double[] rightAxis = new double[4];
		
	public void init() {
		acceptableMessages = new Class[]{};
		previousLeft = 0;
		previousRight = 0;
		previousHeading = 0;
		previousPress = false;
	}
	
	/*
	 *	Joystick values for RawAxis(1) are flipped
	 *	Forward is -, backwards is +
	 */
	public void run() {
		while(Robot.getState() == Robot.GameState.Teleop){
			
			leftAxis[0] = (Math.abs(jLeft.getRawAxis(0)) > Constants.leftAxisDeadband)? jLeft.getRawAxis(0) : 0;
			leftAxis[1] = (Math.abs(jLeft.getRawAxis(1)) > Constants.leftAxisDeadband)? -jLeft.getRawAxis(1) : 0;			
			leftAxis[2] = (Math.abs(jLeft.getRawAxis(2)) > Constants.leftAxisDeadband)? -jLeft.getRawAxis(2) : 0;
			leftAxis[3] = (Math.abs(jLeft.getRawAxis(3)) > Constants.leftAxisDeadband)? -jLeft.getRawAxis(3) : 0;
			
			rightAxis[0] = (Math.abs(jRight.getRawAxis(0)) > Constants.leftAxisDeadband)? -jRight.getRawAxis(0) : 0;
			rightAxis[1] = (Math.abs(jRight.getRawAxis(1)) > Constants.leftAxisDeadband)? -jRight.getRawAxis(1) : 0;
			rightAxis[2] = (Math.abs(jRight.getRawAxis(2)) > Constants.leftAxisDeadband)? jRight.getRawAxis(2) : 0;
			rightAxis[3] = (Math.abs(jRight.getRawAxis(3)) > Constants.leftAxisDeadband)? -jRight.getRawAxis(3) : 0;
			
			
			if(Constants.TANK_DRIVE){
				if(previousLeft != leftAxis[1])
					sendMessage(new MotorCommand(leftAxis[1], MotorCommand.Motor.leftDrive));
				if(previousRight != jRight.getRawAxis(1))
					sendMessage(new MotorCommand(rightAxis[1], MotorCommand.Motor.rightDrive));
				
				previousLeft = leftAxis[1];
				previousRight = rightAxis[1];
			}else{
//				if(previousLeft != jLeft.getRawAxis(Constants.accelAxis) || 
//						previousRight != jRight.getRawAxis(Constants.steerAxis)){
//					sendMessage(new MotorCommand(-jLeft.getRawAxis(Constants.accelAxis) - jRight.getRawAxis(Constants.steerAxis), MotorCommand.Motor.leftDrive));
//					sendMessage(new MotorCommand(-jLeft.getRawAxis(Constants.accelAxis) + jRight.getRawAxis(Constants.steerAxis), MotorCommand.Motor.rightDrive));
//				}
				
				if(previousLeft != leftAxis[0] ||
					previousHeading != rightAxis[2] ||
					previousRight != leftAxis[1]){
					sendMessage(new  HDrive(leftAxis[0], leftAxis[1], rightAxis[2]));
				}
				//H-Drive
				

//				if(previousLeft != jLeft.getRawAxis(Constants.accelAxis) || 
//					previousRight != jRight.getRawAxis(Constants.steerAxis))
//					sendMessage(new CheesyDrive(jLeft.getRawButton(Constants.driverQuickTurn), jLeft.getRawAxis(Constants.accelAxis), jRight.getRawAxis(Constants.steerAxis)));
				
				previousLeft = leftAxis[0];
				previousRight = leftAxis[1];
				previousHeading = rightAxis[2];
			}
			
			if(jLeft.getRawButton(Buttons.scoreGears) || jLeft.getRawButton(Buttons.loadGears)){
				sendMessage(new UpdateGearCollector(jLeft.getRawButton(Buttons.loadGears), jLeft.getRawButton(Buttons.scoreGears)));
			}
			
			//button for climber
			if(!previousPress && jLeft.getRawButton(Buttons.climbUp)){
				sendMessage(new UpdateClimber(jLeft.getRawButton(Buttons.climbUp)));
			}
			previousPress = jLeft.getRawButton(Buttons.climbUp);
			
				
				
				
//			LogFactory.getInstance("General").printPeriodic("JoystickValues: " + jLeft.getRawAxis(1) + " R:" + jRight.getRawAxis(1), "Joysticks", 200);
			
			itsPerSec++;
			sleep();
		}
		Scheduler.getInstance().remove(this);
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
