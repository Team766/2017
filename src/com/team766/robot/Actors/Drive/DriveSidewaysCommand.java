package com.team766.robot.Actors.Drive;

import lib.ConstantsFileReader;
import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveSideways;	
import com.team766.robot.Constants;

public class DriveSidewaysCommand extends CommandBase{

	DriveSideways command;
	
	double kMaxVel = 30; //ft/sec
	double kMaxAccel = 20; //ft/sec^2
	final double kDt = 0.010;
	final double STOP_THRESH = 0.2;
	final double AngleP = 0.02;

	double velocity;
	double goal = 0;
	double position = 0;
	double idealPosition = 0;
	double direction;
	double currentAngle;
	
	private boolean done;
	
	enum State{
		RAMP_UP, MAX_VEL, RAMP_DOWN, LOCK
	}
	State state_;
	
	public DriveSidewaysCommand(Message command){
		this.command = (DriveSideways)command;
		
		state_ = State.RAMP_UP;
		goal = this.command.getDistance();
		
		direction = (goal < 0) ? -1 : 1;
		velocity = 0;
		currentAngle = Drive.getAngle();
		
		kMaxVel =  ConstantsFileReader.getInstance().get("maxVel");
		kMaxAccel = ConstantsFileReader.getInstance().get("maxAccel");
		
		done = false;
	}
	
	@Override
	public void update() {
		position += Drive.avgLinearRate() * kDt;
		
		switch(state_){
			case RAMP_UP:
				velocity += direction * kMaxAccel * kDt;
				
				if(Math.abs(velocity) >= kMaxVel){
					state_ = State.MAX_VEL;
				}
				break;
			case MAX_VEL:
				velocity = kMaxVel * direction;
				break;
			case RAMP_DOWN:
				velocity -= direction * kMaxAccel * kDt;
				if (Math.abs(position) >= Math.abs(goal)){
					state_ = State.LOCK;
				}
				break;
			case LOCK:
				velocity = 0;
				done = true;
				break;
		}
		
		if((Math.abs(goal) - STOP_THRESH <= Math.abs(position) + distToStop(Drive.avgLinearRate())) && (state_ != State.LOCK)){
			state_ = State.RAMP_DOWN;
		}
	

		idealPosition += velocity * kDt;
	
		Drive.linearVelocity.setSetpoint(velocity);
		Drive.linearVelocity.calculate(Drive.avgLinearRate(), false);
		
		Drive.setLeft((currentAngle - Drive.getAngle()) * AngleP);
		Drive.setRight(-(currentAngle - Drive.getAngle()) * AngleP);
	}

	@Override
	public void stop() {
		Drive.setDrive(0.0);
	}
	
	private double distToStop(double vel){
		return (vel * vel) / (2 * kMaxAccel);
	}
	
	public String toString(){
		return "Drive Sideways Command" ;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
