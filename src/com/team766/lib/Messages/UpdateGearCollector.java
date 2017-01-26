package com.team766.lib.Messages;

import lib.Message;

public class UpdateGearCollector implements Message {

	private boolean top;
	private boolean bottom;
	
	
	public UpdateGearCollector(boolean to, boolean bo){
		top = to;
		bottom = bo;
	}
	
	public boolean getTop(){
		return top;
	}
	
	public boolean getBottom(){
		return bottom;
	}
	
	public String toString() {
		return "Message:\tUpdate Gear Collector";
	}

}
