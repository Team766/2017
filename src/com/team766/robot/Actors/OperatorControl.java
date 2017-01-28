package com.team766.robot.Actors;

import interfaces.JoystickReader;
import lib.Actor;
import lib.LogFactory;
import lib.Scheduler;

import com.team766.lib.Messages.CheesyDrive;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.UpdateClimber;
import com.team766.lib.Messages.UpdateGearCollector;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Robot;

public class OperatorControl extends Actor {
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoystick();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoystick();
	JoystickReader jBox = HardwareProvider.getInstance().getBoxJoystick();
	
	private double previousLeft, previousRight;
	private int dropButton, collectButton;
	private boolean previousPress;
		
	public void init() {
		acceptableMessages = new Class[]{};
		previousLeft = 0;
		previousRight = 0;
		previousPress = false;
	}
	
	/*
	 *	Joystick values for RawAxis(1) are flipped
	 *	Forward is -, backwards is +
	 */
	public void run() {
		while(Robot.getState() == Robot.GameState.Teleop){
			
			if(Constants.TANK_DRIVE){
				if(previousLeft != jLeft.getRawAxis(1))
					sendMessage(new MotorCommand(-jLeft.getRawAxis(1), MotorCommand.Motor.leftDrive));
				if(previousRight != jRight.getRawAxis(1))
					sendMessage(new MotorCommand(-jRight.getRawAxis(1), MotorCommand.Motor.rightDrive));
			}else{
				if(previousLeft != jLeft.getRawAxis(Constants.accelAxis) || 
						previousRight != jRight.getRawAxis(Constants.steerAxis)){
					sendMessage(new MotorCommand(-jLeft.getRawAxis(Constants.accelAxis) - jRight.getRawAxis(Constants.steerAxis), MotorCommand.Motor.leftDrive));
					sendMessage(new MotorCommand(-jLeft.getRawAxis(Constants.accelAxis) + jRight.getRawAxis(Constants.steerAxis), MotorCommand.Motor.rightDrive));
				}
				

//				if(previousLeft != jLeft.getRawAxis(Constants.accelAxis) || 
//					previousRight != jRight.getRawAxis(Constants.steerAxis))
//					sendMessage(new CheesyDrive(jLeft.getRawButton(Constants.driverQuickTurn), jLeft.getRawAxis(Constants.accelAxis), jRight.getRawAxis(Constants.steerAxis)));
			}
			
			if(jLeft.getRawButton(Constants.dropGear) || jLeft.getRawButton(Constants.collectGear)){
				sendMessage(new UpdateGearCollector(jLeft.getRawButton(Constants.collectGear), jLeft.getRawButton(Constants.dropGear)));
			}
			
			//button for climber
			if(!previousPress && jLeft.getRawButton(Constants.climb)){
				sendMessage(new UpdateClimber(jLeft.getRawButton(Constants.climb)));
			}
			previousPress = jLeft.getRawButton(Constants.climb);
			
				
				
				
//			LogFactory.getInstance("General").printPeriodic("JoystickValues: " + jLeft.getRawAxis(1) + " R:" + jRight.getRawAxis(1), "Joysticks", 200);
			
			previousLeft = jLeft.getRawAxis(1);
			previousRight = jRight.getRawAxis(1);
			
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
