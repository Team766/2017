package com.team766.lib.Messages;

import lib.Message;

public class ClimbDeploy implements Message{
	
	private boolean deploy;
	
	public ClimbDeploy(boolean deploy){
		this.deploy = deploy;
	}
	
	public boolean getClimbDeploy(){
		return deploy;
	}
	
	public String toString(){
		return "Message:\tClimbDeploy";
	}

}
