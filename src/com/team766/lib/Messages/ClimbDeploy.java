package com.team766.lib.Messages;

import lib.Message;

public class ClimbDeploy implements Message{
	
	private boolean deploy, toggle;
	
	
	public ClimbDeploy(){
		toggle = true;
	}
	
	public ClimbDeploy(boolean deploy){
		this.deploy = deploy;
		toggle = false;
	}
	
	public boolean getClimbDeploy(){
		return deploy;
	}
	
	public boolean isToggle(){
		return toggle;
	}
	
	public String toString(){
		return "Message:\tClimbDeploy";
	}

}
