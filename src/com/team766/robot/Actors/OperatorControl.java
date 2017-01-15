package com.team766.robot.Actors;

import interfaces.JoystickReader;
import lib.Actor;
import lib.Scheduler;

import com.team766.lib.Messages.MotorCommand;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Robot;

public class OperatorControl extends Actor {
	
	JoystickReader jLeft = HardwareProvider.getInstance().getLeftJoystick();
	JoystickReader jRight = HardwareProvider.getInstance().getRightJoystick();
	JoystickReader jBox = HardwareProvider.getInstance().getBoxJoystick();
	
	
	public void init() {
		acceptableMessages = new Class[]{};
	}
	
	public void run() {
		while(Robot.getState() == Robot.GameState.Teleop){
			sendMessage(new MotorCommand(jLeft.getRawAxis(1), MotorCommand.Motor.leftDrive));
			sendMessage(new MotorCommand(jRight.getRawAxis(1), MotorCommand.Motor.rightDrive));
			
//			sendMessage(new CheesyDrive(jLeft.getRawButton(Constants.driverQuickTurn), jLeft.getRawAxis(Constants.accelAxis), jRight.getRawAxis(Constants.steerAxis)));
			
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

}
