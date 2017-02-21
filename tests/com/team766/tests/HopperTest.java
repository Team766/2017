package com.team766.tests;

import lib.Scheduler;
import org.junit.Test;

import tests.DigitalInput;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.SetHopperState;
import com.team766.robot.Actors.Hopper.Hopper;

public class HopperTest extends RobotTestCase{

	@Test
	public void testHopperMotor() throws Exception{
		//intaking ball
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Intake));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == true;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == true;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == false;}, 2);
		
		
		//exhausting ball
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Exhaust));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == true;}, 2);
		
		
		//storing
		Scheduler.getInstance().sendMessage(new SetHopperState(SetHopperState.State.Store));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperOpener()).get() == false;}, 2);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getHopperCloser()).get() == false;}, 2);//
		
		
		
		
	}
}
