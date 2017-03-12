package com.team766.tests;

import lib.Scheduler;

import org.junit.Test;

import tests.AnalogInput;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.RequestDropPeg;
import com.team766.lib.Messages.UpdateGearCollector;

public class GearCollectorTest extends RobotTestCase{

	@Test
	public void testGearSolenoidsFF() throws Exception{
		//False False
		Scheduler.getInstance().sendMessage(new UpdateGearCollector(false, false));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == false;}, 2); 
	}
	
	@Test
	public void testGearSolenoidsTF() throws Exception{
		//True False
		Scheduler.getInstance().sendMessage(new UpdateGearCollector(true, false));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == true;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == false;}, 2);
	}
	
	@Test
	public void testGearSolenoidsFT() throws Exception{
		//False True
		Scheduler.getInstance().sendMessage(new UpdateGearCollector(false, true));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == true;}, 2);
	}
	
	@Test
	public void testGearSolenoidsTT() throws Exception{
		//True True
		Scheduler.getInstance().sendMessage(new UpdateGearCollector(true, true));
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == true;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == true;}, 2);
	}
	
	@Test
	public void testRequestDropPeg() throws Exception {
		
		Scheduler.getInstance().sendMessage(new RequestDropPeg());
		
		//Peg found
		((AnalogInput)(instance.getAnalogInput(ConfigFile.getGearSensor()))).set(0.0);
		
		//Check dropping peg
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == false;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == true;}, 2);
		
	}
}
