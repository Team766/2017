package com.team766.tests;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.UpdateGearCollector;

import lib.Scheduler;
import tests.RobotTestCase;

public class GearCollectorTest extends RobotTestCase{

	public void testGearSolenoids() throws Exception{
		try {
			Scheduler.getInstance().sendMessage(new UpdateGearCollector(false, false));
		} catch (InterruptedException e) {
			System.out.println("Failed to send UpdateGearCollector() in Test");
			e.printStackTrace();
		}
		
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacerOpener()).get() == true;}, 2); 
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getGearPlacer()).get() == true;}, 2); 

	}
}
