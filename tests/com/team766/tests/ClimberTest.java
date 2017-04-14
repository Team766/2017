package com.team766.tests;

import lib.Scheduler;

import com.team766.lib.ConfigFile;
import com.team766.lib.Messages.ClimbDeploy;
import com.team766.lib.Messages.UpdateClimber;

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
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getClimberMotor()).get() == 0;}, 2);
	}
	
	public void testClimberDeploy() throws Exception {
		//Test climberDeploy
		//true
		Scheduler.getInstance().sendMessage(new ClimbDeploy(true));
		//Check climberSolenoid is true
		assertTrueTimed(() -> {return !instance.getSolenoid(ConfigFile.getClimberDeployOut()).get() && instance.getSolenoid(ConfigFile.getClimberDeployIn()).get();}, 2);
	}
	
	public void testClimberSolenoid() throws Exception {
		//false
		Scheduler.getInstance().sendMessage(new ClimbDeploy(false));
		//Check climberSolenoid is false
		assertTrueTimed(() -> {return  instance.getSolenoid(ConfigFile.getClimberDeployOut()).get() && !instance.getSolenoid(ConfigFile.getClimberDeployIn()).get();}, 2);	
	}
	
	public void testClimberToggle() throws Exception {		
		boolean previousState = instance.getSolenoid(ConfigFile.getClimberDeployOut()).get();
		
		Scheduler.getInstance().sendMessage(new ClimbDeploy());
		//Check climberSolenoid is false
		System.out.println("Solenoid: " + instance.getSolenoid(ConfigFile.getClimberDeployOut()).get() + "\tpre: " + previousState);
		assertTrueTimed(() -> {return instance.getSolenoid(ConfigFile.getClimberDeployOut()).get() != previousState;}, 2);	
		
	}

}
