package com.team766.tests;

import lib.Message;
import lib.Scheduler;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.UpdateClimber;
import com.team766.lib.Messages.ClimbDeploy;
import com.team766.robot.Constants;

import tests.RobotTestCase;

public class ClimberTest extends TestCase{
	
	public void testUpdateClimber() throws Exception{
		//Test updateClimber
		//true
		Scheduler.getInstance().sendMessage(new UpdateClimber(true));
		//Check climber motor positive
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getClimberMotor()).get() > 0;}, 2);	
	}
	
	public void testClimberFalse() throws Exception {
		//false
		Scheduler.getInstance().sendMessage(new UpdateClimber(false));
		//Check climber motor negative
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getClimberMotor()).get() < 0;}, 2);
	}
	
	public void testClimberDeploy() throws Exception {
		//Test climberDeploy
		//true
		Scheduler.getInstance().sendMessage(new ClimbDeploy(true));
		//Check climberSolenoid is true
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getClimberDeploy()).get() == true;}, 2);
	}
	
	public void testClimberSolenoid() throws Exception {
		//false
		Scheduler.getInstance().sendMessage(new ClimbDeploy(false));
		//Check climberSolenoid is false
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getClimberDeploy()).get() == false;}, 2);	
		
	}

}
