package com.team766.lib.Messages;

import lib.Message;

public class UpdateClimber implements Message{
	
	//true is forward, false is backwards
	private boolean climb;
	
	public UpdateClimber(boolean climb){
		this.climb = climb;
	}
	
	public boolean getClimb(){
		return climb;
	}
	
	public String toString(){
		return "Message:\tUpdate Climber";
	}
}
