package com.team766.robot.Actors.Drive;

import lib.LogFactory;
import lib.Message;
import trajectory.Path;
import trajectory.Trajectory;
import trajectory.TrajectoryFollower;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DrivePath;

/**
 * DrivePathAction causes the robot to drive along a Path
 *
 */
public class DrivePathCommand extends CommandBase {

	private DrivePath command;
	
	double angleDiff = 0;
	
	boolean done;
	
	double heading;
	Path path;

	TrajectoryFollower followerLeft = new TrajectoryFollower("left");
	TrajectoryFollower followerRight = new TrajectoryFollower("right");
	private double direction;
	double kTurn = 0.004;//-3.0 / 80.0;

	public DrivePathCommand(Message m) {
		done = false;
		LogFactory.getInstance("General").print("Drive: DrivePathCommand");
		command = (DrivePath) m;
		path = command.getPath();
	
		start();
	}

	public void start() {

//		followerLeft.configure(1.5, 0, 0, 1.0 / 15.0, 1.0 / 34.0);
//		followerRight.configure(1.5, 0, 0, 1.0 / 15.0, 1.0 / 34.0);
		
		followerLeft.configure(0.05, 0, 0, 0, 0.005);
		followerRight.configure(0.05, 0, 0, 0, 0.005);
		
		Drive.resetEncoders();
		Drive.resetAngle();

		loadProfile(path.getLeftWheelTrajectory(),
				path.getRightWheelTrajectory(), 1.0, 0.0);

	}

	//Values: {avgLinearRate(), leftRate(), rightRate(), avgDist(), leftDist(), rightDist()}
	public void update() {
		// We need to set the Trajectory each update as it may have been flipped
		// from under us
		loadProfileNoReset(path.getLeftWheelTrajectory(),
				path.getRightWheelTrajectory());

		if (onTarget()) {
			Drive.setDrive(0.0);
			done = true;
		} else {
			double distanceL = direction * Drive.leftDist();
			double distanceR = direction * Drive.rightDist();
			System.out.println("DistanceL:" + distanceL);
			System.out.println("DistanceR:" + distanceR + "\n");

			double speedLeft = direction * followerLeft.calculate(distanceL);
			double speedRight = direction * followerRight.calculate(distanceR);
			 System.out.println("SpeedLeft:" + speedLeft);
			 System.out.println("SpeedRight:" + speedRight + "\n");

			double goalHeading = followerLeft.getHeading();
			double observedHeading = Drive.getGyroAngleInRadians();

			double angleDiffRads = getDifferenceInAngleRadians(observedHeading,
					goalHeading);

			angleDiff = Math.toDegrees(angleDiffRads);
			double turn = kTurn * angleDiff;
			
			Drive.setLeft(speedLeft + turn);
			Drive.setRight(speedRight - turn);
		}
	}

	public void loadProfileNoReset(Trajectory leftProfile,
			Trajectory rightProfile) {
		followerLeft.setTrajectory(leftProfile);
		followerRight.setTrajectory(rightProfile);
	}

	public boolean onTarget() {
		return followerLeft.isFinishedTrajectory();
	}

	public void loadProfile(Trajectory leftProfile, Trajectory rightProfile,
			double direction, double heading) {
		reset();
		followerLeft.setTrajectory(leftProfile);
		followerRight.setTrajectory(rightProfile);
		this.direction = direction;
		this.heading = heading;
	}

	public void reset() {
		followerLeft.reset();
		followerRight.reset();
		Drive.resetEncoders();
	}

	public double getDifferenceInAngleRadians(double from, double to) {
		return boundAngleNegPiToPiRadians(to - from);
	}

	public double boundAngleNegPiToPiRadians(double angle) {
		// Naive algorithm
		while (angle >= Math.PI) {
			angle -= 2.0 * Math.PI;
		}
		while (angle < -Math.PI) {
			angle += 2.0 * Math.PI;
		}
		return angle;
	}

	public void stop() {
		Drive.setDrive(0.0);
	}

	@Override
	public boolean isDone() {
		return done;
	}
}
