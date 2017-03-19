package com.team766.lib.Messages;

import lib.Message;

public class HopperSetRoller implements Message{
	
	//true is forward, false is backward
	private boolean forward;
	private boolean off;
	
	public HopperSetRoller(){
		off = true;
	}
	
	public HopperSetRoller(boolean forward){
		this.forward = forward;
		off = false;
	}
	
	public boolean getForward(){
		if(!off)
			return forward;
		return false;
	}
	
	public boolean getOff(){
		return off;
	}
	
	
	public String toString(){
		return "Message:\tHopperSetRollerMessage";
	}


}
