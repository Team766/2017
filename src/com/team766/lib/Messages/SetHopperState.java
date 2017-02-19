package com.team766.lib.Messages;

import lib.Message;

public class SetHopperState implements Message{

	private State state;
	
	public enum State{
		Store,
		Intake,
		Exhaust	
	}
	
	public SetHopperState(State state){
		this.state = state;
	}
	
	public State getHopperState(){
		return state;
	}
	
	
	public String toString(){
		return "Message:\tSetHopperState";
	}
}
