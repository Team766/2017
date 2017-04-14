package com.team766.robot.Actors.Drive;

import interfaces.EncoderReader;
import interfaces.GyroReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.LogFactory;
import lib.Message;
import lib.PIDController;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.CheesyDrive;
import com.team766.lib.Messages.DriveDistance;
import com.team766.lib.Messages.DriveIntoPegStop;
import com.team766.lib.Messages.DrivePath;
import com.team766.lib.Messages.DriveSideways;
import com.team766.lib.Messages.DriveStatusUpdate;
import com.team766.lib.Messages.HDrive;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.RequestDropPeg;
import com.team766.lib.Messages.ResetDriveAngle;
import com.team766.lib.Messages.SnapToAngle;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.TurnAngle;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

public class Drive extends Actor{

	SpeedController leftMotorA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftMotorB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightMotorA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightMotorB = HardwareProvider.getInstance().getRightDriveB();	
	SpeedController centerMotor = HardwareProvider.getInstance().getCenterDrive();
	
	EncoderReader leftEncoder = HardwareProvider.getInstance().getLeftEncoder();
	EncoderReader rightEncoder = HardwareProvider.getInstance().getRightEncoder();
	EncoderReader centerEncoder = HardwareProvider.getInstance().getCenterEncoder();
	
	GyroReader gyro = HardwareProvider.getInstance().getGyro();
	
	PIDController angularVelocity = new PIDController(Constants.k_angularP, Constants.k_angularI, Constants.k_angularD, Constants.k_angularThresh);
	PIDController linearVelocity = new PIDController(Constants.k_linearP, Constants.k_linearI, Constants.k_linearD, Constants.k_linearThresh);
	
	PIDController anglePID = new PIDController(Constants.k_angularP, Constants.k_angularI, Constants.k_angularD, Constants.k_angularThresh);
	PIDController distancePID = new PIDController(Constants.k_linearP, Constants.k_linearI, Constants.k_linearD, Constants.k_linearThresh);
	
	//Position
	protected double xPos = 0;
	protected double yPos = 0;
	private double lastPosTime;
	private double lastHeading;
	private double currHeading;
	
	private double lastVelTime;
	private double lastAngVelTime;
	private double rightVel;
	private double leftVel;
	private double centerVel;
	private double angVel;
	private double lastAngle;
	private double lastRightDist;
	private double lastLeftDist;
	private double lastCenterDist;
	private double leftAccel;
	private double rightAccel;
	private double centerAccel;
	private double lastAccelTime;
	private double lastRightVel;
	private double lastLeftVel;
	private double lastCenterVel;


	
	private boolean commandFinished;
	
	private double gyroOffset;
	
	Message currentMessage;
	MotorCommand[] motors;
	SubActor currentCommand;
	
	public void init() {
		acceptableMessages = new Class[]{MotorCommand.class, CheesyDrive.class, HDrive.class, DrivePath.class, DriveDistance.class, ResetDriveAngle.class, SnapToAngle.class, Stop.class, DriveSideways.class, DriveIntoPegStop.class, TurnAngle.class};
		commandFinished = false;
		
		lastPosTime = System.currentTimeMillis() / 1000.0;
		lastHeading = 0;
		
		lastVelTime = System.currentTimeMillis() / 1000.0;
		leftVel = 0;
		rightVel = 0;
		centerVel = 0;
		lastLeftDist = leftDist();
		lastRightDist = rightDist();
		lastCenterDist = centerDist();
		leftAccel = 0;
		rightAccel = 0;
		centerAccel = 0;
		lastAngle = 0;
		lastLeftVel = leftRate();
		lastRightVel = rightRate();
		lastCenterVel = centerRate();
		lastAccelTime = System.currentTimeMillis() / 1000.0;
		
		gyroOffset = Constants.STARTING_HEADING;
		
		lastAngVelTime = System.currentTimeMillis() / 1000.0;
		
	}

	public void iterate(){
		
		//Check for new messages
		if(newMessage()){
			stopCurrentCommand();
			commandFinished = false;
			
			currentMessage = readMessage();
			
			if(currentMessage == null)
				return;
			
			LogFactory.getInstance("Vision").print("New Message: " + currentMessage);
							
			if(currentMessage instanceof SnapToAngle){
				System.out.println("rawAngle: " + getRawAngle());
				if(Math.abs(getRawAngle()) < 30)
					currentCommand = new DriveDistanceCommand(0, -gyroOffset);
				else if(getRawAngle() >= 30)
					currentCommand = new DriveDistanceCommand(0, 60 - gyroOffset);
				else if(getRawAngle() <= -30)
					currentCommand = new DriveDistanceCommand(0, -60 - gyroOffset);
				System.out.println("snapToAngle: " + this.getAngle());
				
				
			}
			else if(currentMessage instanceof MotorCommand)
				currentCommand = new MotorSubCommand(currentMessage);
			else if(currentMessage instanceof CheesyDrive)
				currentCommand = new CheesyDriveCommand(currentMessage);
			else if(currentMessage instanceof HDrive)
				currentCommand = new HDriveCommand(currentMessage);
			else if(currentMessage instanceof DrivePath)
				currentCommand = new DrivePathCommand(currentMessage);
			else if(currentMessage instanceof Stop){
				stopCurrentCommand();
				setDrive(0.0);
				setCenter(0.0);
			}
			else if(currentMessage instanceof ResetDriveAngle){
				ResetDriveAngle angleMessage = (ResetDriveAngle)currentMessage;
				setGyroAngle(angleMessage.getAngle());
			}
			else if(currentMessage instanceof DriveSideways){
				currentCommand = new DriveSidewaysCommand(currentMessage);
			}
			else if(currentMessage instanceof DriveDistance){
				currentCommand = new DriveProfilerCommand(currentMessage);
//				currentCommand = new DriveDistanceCommand(currentMessage);
			}
			else if(currentMessage instanceof TurnAngle){
				currentCommand = new DriveDistanceCommand(0,((TurnAngle)currentMessage).getAngle());
			}
			else if(currentMessage instanceof DriveIntoPegStop){
				currentCommand = new SubActor(){
					@Override
					public void update() {
						LogFactory.getInstance("Vison").print("Updatting Power!");
						setDrive(ConstantsFileReader.getInstance().get("DriveIntoPegPower"));
					}
					
					@Override
					public void stop() {
						LogFactory.getInstance("Vison").print("Done Driving into peg stop");
						setDrive(0.0);
						sendMessage(new RequestDropPeg());
					}
					
					@Override
					public boolean isDone() {
						return CommandBase.GearPlacer.isPegPresent();
					}
				};
			}
			//Reset Control loops
			resetControlLoops();
		}
					
		//LogFactory.getInstance("General").printPeriodic("Gyro: " + getAngle(), "Gyro", 200);
		
		LogFactory.getInstance("Vision").printPeriodic("Left: " + leftDist() + " Right: " + rightDist() + " Center: " + centerDist() + " Gyro: " + getAngle(), "Encoders", 200);
		step();
		
		//Send Status Update	#StayUpToDate	#Current	#inTheKnow
		sendMessage(new DriveStatusUpdate(commandFinished, currentMessage, xPos, yPos, avgLinearRate()));

		updateVelocities();
		updateAngularRate();
		updateLocation();
		updateAccelerations();
		
//		itsPerSec++;
	}
	
	public void run() {
		while(enabled){	
			iterate();
			sleep();
		}
		
		//Stop all processes
		stopCurrentCommand();
	}
	
	public void step(){
		if(currentCommand != null){
			if(currentCommand.isDone()){
				stopCurrentCommand();
			}else{
				currentCommand.update();
			}
		}
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null){
			currentCommand.stop();
			sendMessage(new DriveStatusUpdate(true, currentMessage, xPos, yPos, avgLinearRate()));
		}
		currentCommand = null;
		commandFinished = true;
	}
	
	
	private void updateVelocities(){
		if(System.currentTimeMillis()/1000.0 - lastVelTime > 0.1){
			
			leftVel = (leftDist() - lastLeftDist) / (System.currentTimeMillis()/1000.0 - lastVelTime);
			rightVel = (rightDist() - lastRightDist) / (System.currentTimeMillis()/1000.0 - lastVelTime);
			centerVel = (centerDist() - lastCenterDist) / (System.currentTimeMillis()/1000.0 - lastVelTime);
			
//			System.out.printf("Rates: %f\t%f\t%f\n", avgLinearRate(), rightVel, rightVel);
			
			lastLeftDist = leftDist();
			lastRightDist = rightDist();
			lastCenterDist = centerDist();
			lastVelTime = System.currentTimeMillis()/1000.0;
		}
	}
	
	private void updateAccelerations(){
		if(System.currentTimeMillis()/1000.0 - lastAccelTime > 0.1){
			leftAccel = (leftRate() - lastLeftVel) / (System.currentTimeMillis()/1000.0 - lastAccelTime);
			rightAccel = (rightRate() - lastRightVel) / (System.currentTimeMillis()/1000.0 - lastAccelTime);
			centerAccel = (centerRate() - lastCenterVel) / (System.currentTimeMillis()/1000.0 - lastAccelTime);
			
//			System.out.printf("Accelerations: %f\t%f\t%f\n", leftAccel, rightAccel, centerAccel);

			lastLeftVel = leftRate();
			lastRightVel = rightRate();
			lastCenterVel = centerRate();
			lastAccelTime = System.currentTimeMillis()/1000.0;
		}
	}
	
	private void updateAngularRate(){
		if(System.currentTimeMillis()/1000.0 - lastAngVelTime > 0.1){
			
			angVel = (getAngle() - lastAngle) / (System.currentTimeMillis()/1000.0 - lastAngVelTime);
//			System.out.printf("angular rates: %f\t%f\n", getAngularRate(), angVel);
			
			lastAngle = getAngle();
			lastAngVelTime = System.currentTimeMillis()/1000.0;
		}
	}
	
	private void updateLocation(){
		if(System.currentTimeMillis()/1000.0 - lastPosTime > 0.25){
			currHeading = Math.toRadians(gyro.getAngle());
			
			xPos += (Math.cos(currHeading) + Math.cos(lastHeading)) * avgDist() / 2.0;
			yPos += (Math.sin(currHeading) + Math.sin(lastHeading)) * avgDist() / 2.0;
			
			lastHeading = Math.toRadians(gyro.getAngle());
			lastPosTime = System.currentTimeMillis() / 1000;
		}
	}
	
	protected double avgLinearRate(){
		return (leftRate() + rightRate()) / 2.0;
		//return (leftEncoder.getRate() + rightEncoder.getRate())/2.0;
	}
	
	protected double getAngularRate(){
		//return gyro.getRate();
		return angVel;
	}
	
	protected double leftRate(){
		return leftVel;
	}
	
	protected double rightRate(){
		return rightVel;
	}
	
	protected double centerRate(){
		return centerVel;
	}
	
	protected double avgDist(){
		return (leftDist() + rightDist())/2.0;
	}
	
	protected double leftDist(){
		return ConstantsFileReader.getInstance().get("LeftEncoderDirection") * (leftEncoder.getRaw() / Constants.counts_per_rev * Constants.wheel_circumference);
	}
	
	protected double rightDist(){
		return ConstantsFileReader.getInstance().get("RightEncoderDirection") * (rightEncoder.getRaw() / Constants.counts_per_rev * Constants.wheel_circumference);
	}
	
	protected double centerDist(){
		return ConstantsFileReader.getInstance().get("CenterEncoderDirection") * (centerEncoder.getRaw() / Constants.center_counts_per_rev * Constants.follower_wheel_circumference);
	}
	
	protected void setDrive(double power){
		setLeft(power);
		setRight(power);
	}
	
	protected void setLeft(double power){
		if(Math.abs(power) < Constants.driveLeftDeadband){
			leftMotorA.set(0);
			leftMotorB.set(0);
		}else{
			power *= ConstantsFileReader.getInstance().get("LeftMotorDirection");
			leftMotorA.set(power);
			leftMotorB.set(power);
		}
	}
	
	protected void setRight(double power){
		if(Math.abs(power) < Constants.driveRightDeadband){
			rightMotorA.set(0);
			rightMotorB.set(0);
		}else{
			power *= ConstantsFileReader.getInstance().get("RightMotorDirection");
			rightMotorA.set(power);
			rightMotorB.set(power);
		}
	}
	
	protected void setCenter(double power){
		if(Math.abs(power) < Constants.driveCenterDeadband)
			centerMotor.set(0);
		else{
			power *= ConstantsFileReader.getInstance().get("CenterMotorDirection");
			centerMotor.set(power);
		}
	}
	
	protected void resetAngle(){
		setGyroAngle(0.0);
	}
	
	protected void setGyroAngle(double angle){
		gyroOffset = gyro.getAngle() + angle;
	}
	
	protected void resetEncoders(){
		leftEncoder.reset();
		rightEncoder.reset();
		centerEncoder.reset();
	}
	
	public String toString(){
		return "Actor:\tDrive";
	}
	
	private void resetControlLoops(){
		angularVelocity.reset();
		linearVelocity.reset();
		anglePID.reset();
		distancePID.reset();
	}
	
	protected double getAngle(){
		return gyro.getAngle() - gyroOffset;
	}
	
	public double getRawAngle(){
		return gyro.getAngle() + Constants.STARTING_HEADING;
	}
	
	/**
	 * -180 to 180 degrees
	 * @return
	 */
	public double getAngleNeg180to180(){
		double angle = gyro.getAngle();
		if(angle < 0)
			while(angle < -180)
				angle += 360;
		else
			while(angle > 180)
				angle -= 360;
		
		return angle;
	}
	
	protected double getGyroAngleInRadians(){
		return Math.toRadians(getAngle());
	}
	
	protected void switchAngleGains(boolean turn){
		if(turn)
			anglePID.setConstants(Constants.k_angularP, Constants.k_angularI, Constants.k_angularD);
		else
			anglePID.setConstants(Constants.k_driveAngularP, Constants.k_driveAngularI, Constants.k_driveAngularD);
	}
	
}
