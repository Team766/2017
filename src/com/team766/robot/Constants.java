package com.team766.robot;

import lib.RobotValues;

public abstract class Constants extends RobotValues{
	
	public static int getAutonMode(){
		return AutonMode;
	}
	
	public static void setAutonMode(int autonMode){
		AutonMode = autonMode;
	}
	
	public static final String[] AUTONS = new String[]{"None"};
	
	public static final int ACTOR_COUNT = 1;
	
	public static final boolean TANK_DRIVE = false;
	
	public static final double wheel_circumference = 0.2032 * Math.PI;
	public static final double counts_per_rev = 360;
	
	public static final double maxAngularVelocity = 50;
	public static final double maxLinearVelocity = 10;
	
	public static final double maxClimberMotor = 1;
	
	public static final double driveLeftDeadband = 0;
	public static final double driveRightDeadband = 0;
	public static final double driveCenterDeadband = 0;
	
	public static final double leftAxisDeadband = 0.05;
	public static final double rightAxisDeadband = 0.05;
	
	//Buttons
	public static final int driverQuickTurn = 1;
	public static final int dropGear = 2;
	public static final int collectGear = 3;
	public static final int climb = 4;
	
	//Axis
	public static final int steerAxis = 0;
	public static final int accelAxis = 1;
	
	//Drive PID
	public static final double MAX_STOPPING_VEL = 0.5;	//	UNITS:	m/s
	
	public static final double k_angularP =	0.04;//1/maxAngularVelocity;	//.012
	public static final double k_angularI = 0.0000;
	public static final double k_angularD = 0.3;
	public static final double k_angularThresh = 1;
	
	public static final double k_driveAngularP = 0.01;
	public static final double k_driveAngularI = 0.0008;
	public static final double k_driveAngularD = 0.0;
	
	public static final double k_linearP = 0.5;//1/maxLinearVelocity;
	public static final double k_linearI = 0.00;
	public static final double k_linearD = 0.00;
	public static final double k_linearThresh = 0.01;
	
	public static final double STARTING_HEADING = 0;
}
