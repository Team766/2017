package com.team766.lib.Messages;

import lib.Message;

public class HopperSetRoller implements Message{
	
	//true is forward, false is backward
	private boolean forward;
	
	public HopperSetRoller(boolean forward){
		this.forward = forward;
	}
	
	public boolean getForward(){
		return forward;
	}
	
	
	public String toString(){
		return "Message:\tHopperSetRollerMessage";
	}


}
