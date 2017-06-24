package com.team766.tests;

import org.junit.Test;

import com.team766.lib.ConfigFile;

import tests.Joystick;
import tests.Gyro;

public class LagTest extends TestCase {
	
	
	private final int SLEEP_TIME = 500;
	private final double THRESH = 0.01;
	private final double WAIT_TIME = 0.001;
	private final int TOTAL_RUN = 120; //Seconds
	private final int RUN_TIME = (int)(TOTAL_RUN / (SLEEP_TIME / 1000.0));
	
	private double currentJoystickValue;
	@Test
	public void testNoAuton() throws Exception {
		
		teleopInit();
		
		((Joystick)(instance.getJoystick(ConfigFile.getLeftJoystick()))).setAxisValue(1, 0);
		((Joystick)(instance.getJoystick(ConfigFile.getLeftJoystick()))).setAxisValue(0, 0);
		((Joystick)(instance.getJoystick(ConfigFile.getRightJoystick()))).setAxisValue(0, 0);
		
		((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(0.0);
		
		for(int i = 0; i < RUN_TIME; i++){
			currentJoystickValue = randomJoystickValue();
			
			//Joystick axis 1 is the throttle
			((Joystick)(instance.getJoystick(ConfigFile.getLeftJoystick()))).setAxisValue(1, currentJoystickValue);
			
			//Account for the joystick value being manipulated
			currentJoystickValue = joystickCorrections(currentJoystickValue);
			Thread.sleep(100);
//			System.out.printf("i: %d\tExpecting: %f Motor: %f\n", i, currentJoystickValue, instance.getMotor(ConfigFile.getLeftMotor()[0]).get());
			
			//Robot drives power from joystick.  Assume no change in heading
			assertTrueTimed(() -> {return Math.abs(instance.getMotor(ConfigFile.getLeftMotor()[0]).get() - currentJoystickValue) <= THRESH;}, WAIT_TIME);
			assertTrueTimed(() -> {return Math.abs(instance.getMotor(ConfigFile.getRightMotor()[0]).get() - currentJoystickValue) <= THRESH;}, WAIT_TIME);
			assertTrueTimed(() -> {return Math.abs(instance.getMotor(ConfigFile.getCenterMotor()).get()) <= THRESH;}, WAIT_TIME);
			
			Thread.sleep(SLEEP_TIME);
		}
	}
	
	@Test
	public void testServer() throws Exception {
		double angle = 0;
		while(true){
			angle = instance.getGyro(ConfigFile.getGyro()).getAngle();
			angle = (angle + 10) % 360;
			((Gyro)(instance.getGyro(ConfigFile.getGyro()))).setAngle(angle);
			Thread.sleep(SLEEP_TIME);
		}
	}
	
	private double joystickCorrections(double in){
		return -in * Math.abs(in);
	}
	
	private double randomJoystickValue(){
		return -1 + 2*Math.random();
	}
}
