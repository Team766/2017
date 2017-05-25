package com.team766.robot;

import lib.ConstantsFileReader;
import lib.RobotValues;
public abstract class Constants extends RobotValues{
	
	public static int getAutonMode(){
		return AutonMode;
	}
	
	public static void setAutonMode(int autonMode){
		AutonMode = autonMode;
	}
	
	public static final String[] AUTONS = new String[]{"None", "DriveToPeg", "BoilerPath", "StraightToPeg", "StraightToPegPath", "FlipDriveToPegPath","CrossLine", "DriveToPegPath"};
	
	public static final int ACTOR_COUNT = 1;
	
	public static final String LOG_FILE = ConstantsFileReader.getInstance().getStr("LOG_FILE");
	
	public static final boolean TANK_DRIVE = false;
	
	public static final double wheel_circumference = 1.0/3.0 * Math.PI; //feet
	public static final double counts_per_rev = 1000.0;
	
	//Auton constants
	public static final double ALLIGNING_SIDEWAYS_DIST_THRESH = 3.5 / 12.0;
	public static final double DRIVE_INTO_PEG_THRESH = 10.0 / 12.0;
	
	//Analog Sensors
	public static final double PHOTOGATE_STEP_VOLTAGE = 2.5;
	
	public static final double follower_wheel_circumference = 0.27083 * Math.PI; //3.25 in
	public static final double center_counts_per_rev = 360.0;
	
	
	public static final double maxAngularVelocity = 50;
	public static final double maxLinearVelocity = 32.81; //ft/sec
	
	public static final double maxClimberMotor = 1;
	
	public static final double driveLeftDeadband = 0;
	public static final double driveRightDeadband = 0;
	public static final double driveCenterDeadband = 0;
	
	public static final double leftAxisDeadband = 0;	//0.1
	public static final double rightAxisDeadband = 0.01;
	
	//Axis
	public static final int steerAxis = 0;
	public static final int accelAxis = 1;
	
	//Drive PID
	public static final double MAX_STOPPING_VEL = 1.64;	//	UNITS:	ft/s
	
	public static final double k_angularP =	0.035;//1/maxAngularVelocity;	//.012
	public static final double k_angularI = 0.0000;
	public static final double k_angularD = 0.3;
	public static final double k_angularThresh = 1;
	
	public static final double k_driveAngularP = 0.001;
	public static final double k_driveAngularI = 0.0008;
	public static final double k_driveAngularD = 0.0;
	
	public static final double k_linearP = 0.03;//1/maxLinearVelocity; //feet
	public static final double k_linearI = 0.00;
	public static final double k_linearD = 0.00;
	public static final double k_linearThresh = 0.01;
	
	public static final double HopperRunTime = 2.0; //seconds
	
	public static final double STARTING_HEADING = 0;
}
