package com.team766.robot.Actors.Drive;

import lib.ConstantsFileReader;
import lib.Message;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveSideways;

public class DriveSidewaysCommand extends CommandBase{

	DriveSideways command;
	
	double kMaxVel = 30; //ft/sec
	double kMaxAccel = 20; //ft/sec^2
	final double kDt = 0.010;
	final double STOP_THRESH = 0.2;
	final double AngleP = 0.05;

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
				//System.out.println("ramp-up");
				break;
			case MAX_VEL:
				velocity = kMaxVel * direction;
				//System.out.println("max-vel");
				break;
			case RAMP_DOWN:
				velocity -= direction * kMaxAccel * kDt;
				//System.out.println("ramp-down");
				if (Math.abs(position) >= Math.abs(goal)){
					state_ = State.LOCK;
				}
				break;
			case LOCK:
				velocity = 0;
				done = true;
				//System.out.println("lock");
				break;
		}
		
		if((Math.abs(goal) - STOP_THRESH <= Math.abs(position) + distToStop(Drive.avgLinearRate())) && (state_ != State.LOCK)){
			state_ = State.RAMP_DOWN;
		}
	

		idealPosition += velocity * kDt;
	
		Drive.linearVelocity.setSetpoint(velocity);
		
//		System.out.println("direction: " + direction);
//		System.out.println("velocity:" + velocity);
		Drive.linearVelocity.calculate(Drive.centerRate(), false);
	
//		System.out.println("linearVelocity:  " + Drive.linearVelocity.getOutput());
		Drive.setCenter(Drive.linearVelocity.getOutput());
		
		Drive.setLeft((currentAngle - Drive.getAngle()) * AngleP);
		Drive.setRight(-(currentAngle - Drive.getAngle()) * AngleP);
		
	}

	public static String getState() {
		if(State.RAMP_UP != null) 
			return "RAMP_UP";
		if(State.MAX_VEL != null) 
			return "MAX_VEL";
		if(State.RAMP_DOWN != null) 
			return "RAMP_DOWN";
		if(State.LOCK != null) 
			return "LOCK";
		
		return "no state!";
	}
	
	
	public void stop() {
		Drive.setDrive(0.0);
		Drive.setCenter(0.0);
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
