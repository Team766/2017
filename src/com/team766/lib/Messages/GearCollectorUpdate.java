package com.team766.lib.Messages;

import lib.Message;
import lib.StatusUpdateMessage;

public class GearCollectorUpdate extends StatusUpdateMessage{
		
	public GearCollectorUpdate(boolean done, Message currentMessage){
		super(done, currentMessage);
	}
	
	public String toString(){
		return "Message:\tGearCollectorUpdate";
	}
}
