package com.team766.tests;

import lib.Scheduler;

import org.junit.Test;

import tests.DigitalInput;
import tests.Encoder;
import tests.Gyro;
import tests.AnalogInput;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.SetHopperState;
import com.team766.robot.Actors.Hopper.Hopper;

public class HopperTest extends RobotTestCase{

	@Test
	public void testHopperIntaking() throws Exception{
		//intaking ball
		((AnalogInput)(instance.getAnalogInput(ConfigFile.getHopperSensor()))).set(5.0);
		
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Intake));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == true;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == true;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == false;}, 2);

		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() > 0;}, 2);
		
		((AnalogInput)(instance.getAnalogInput(ConfigFile.getHopperSensor()))).set(0.0);
		
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getHopperMotor()).get() == 0;}, 2.5);
	}
	
	public void testHopperExhausting() throws Exception{
		//exhausting ball
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Exhaust));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == true;}, 2);
	}
	
	public void testHopperStoring() throws Exception{	
		//storing
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Store));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == false;}, 2);
		
	}
}
