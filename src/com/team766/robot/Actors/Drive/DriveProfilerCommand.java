package com.team766.robot.Actors.Drive;

import lib.ConstantsFileReader;
import lib.LogFactory;
import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveDistance;
import com.team766.robot.Constants;

public class DriveProfilerCommand extends CommandBase{

	DriveDistance command;
	
	double kMaxVel = 30; //ft/sec
	double kMaxAccel = 20; //ft/sec^2
	final double kDt = 0.010;
	final double STOP_THRESH = 0.1;

	double velocity;
	double goal = 0;
	double position = 0;
	double idealPosition = 0;
	double direction;
	double offset;
	double startingAngle;
	
	boolean done;
	int count = 0;
	//States
	enum State{
		RAMP_UP, MAX_VEL, RAMP_DOWN, LOCK
	}
	State state_;
		
	public DriveProfilerCommand(Message command){
		this.command = (DriveDistance)command;
		
		state_ = State.RAMP_UP;
		goal = this.command.getDistance();
		
		direction = (goal < 0) ? -1 : 1;
		velocity = 0;
		
		kMaxVel =  ConstantsFileReader.getInstance().get("maxVel");
		kMaxAccel = ConstantsFileReader.getInstance().get("maxAccel");
		
		LogFactory.getInstance("Vision").print("DRIVE PROFILER!");
		
		startingAngle = Drive.getAngle();
		
		done = false;
		offset = Drive.avgDist();
	}
	
	//Values: {avgLinearRate(), leftRate(), rightRate(), avgDist(), leftDist(), rightDist()}
	@Override
	public void update() {
		//position += Drive.avgLinearRate() * kDt;
		
		switch(state_){
			case RAMP_UP:
				velocity += direction * kMaxAccel * kDt;
				
				if(Math.abs(Drive.avgLinearRate()) >= kMaxVel){
					state_ = State.MAX_VEL;
				}
				break;
			case MAX_VEL:
				velocity = kMaxVel * direction;
				break;
			case RAMP_DOWN:
				velocity -= direction * kMaxAccel * kDt;
				if(Math.abs(getDist()) + STOP_THRESH >= Math.abs(goal)){
					state_ = State.LOCK;
				}
				break;
			case LOCK:
				velocity = 0.0;
				done = true;
				break;
		}
		
		if((Math.abs(goal) - STOP_THRESH <= Math.abs(getDist()) + distToStop(Drive.avgLinearRate())) && (state_ != State.LOCK)){
			state_ = State.RAMP_DOWN;
		}
		LogFactory.getInstance("Vision").print("goal: " + goal + " distToStop: " + distToStop(Drive.avgLinearRate()) + " position: " + Drive.avgDist() + " - " + offset + " state_: " + state_ + " vel: " + velocity + " realVel: " + Drive.avgLinearRate() + "\n");
//		System.out.println("Vel: " + values[0] + " pos: " + values[3]);
		if(count > 25){
			System.out.println("goal: " + goal + " distToStop: " + distToStop(Drive.avgLinearRate()) + " position: " + Drive.avgDist() + " - " + offset + " state_: " + state_ + " vel: " + velocity + " realVel: " + Drive.avgLinearRate());
			System.out.println("Output: " + Drive.linearVelocity.getOutput());
//			LogFactory.getInstance("Vision").print("goal: " + goal + " distToStop: " + distToStop(Drive.avgLinearRate()) + " position: " + Drive.avgDist() + " - " + offset + " state_: " + state_ + " vel: " + velocity + " realVel: " + Drive.avgLinearRate() + "\n");
			count = 0;
		}
		count++;
		idealPosition += velocity * kDt;
	
		Drive.linearVelocity.setSetpoint(velocity);
		Drive.linearVelocity.calculate(Drive.avgLinearRate(), false);
//		Drive.setDrive(Drive.linearVelocity.getOutput());
		Drive.setLeft(Drive.linearVelocity.getOutput() + (startingAngle - Drive.getAngle()) * ConstantsFileReader.getInstance().get("AngleP"));
		Drive.setRight(Drive.linearVelocity.getOutput() - (startingAngle - Drive.getAngle()) * ConstantsFileReader.getInstance().get("AngleP"));
	}
	
	@Override
	public void stop() {
		Drive.setDrive(0.0);
	}
	
	private double distToStop(double vel){
		return (vel * vel) / (2 * kMaxAccel);
	}
	
	public String toString(){
		return "Drive Profiler Command";
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	private double getDist(){
		return Drive.avgDist() - offset;
	}

}
