package com.team766.lib;

import lib.Actor;
import lib.ServerMessage;

import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.MotorCommand.Motor;
import com.team766.lib.Messages.TurnAngle;

public class ServerParser extends Actor {

	private ServerMessage currentMessage;
	
	public ServerParser(){
		
	}
	
	@Override
	public void init() { 
		acceptableMessages = new Class[]{ServerMessage.class};
	}

	@Override
	public void iterate(){
		if(newMessage()){

			currentMessage = (ServerMessage)readMessage();
			if(currentMessage == null)
				return;
			
			System.out.println("Parsing: " + currentMessage.getMessageName() + "\t" + currentMessage.getValues());
			log("Parsing: " + currentMessage.getMessageName() + "\t" + currentMessage.getValues());
			
			switch(currentMessage.getMessageName()){
				case "TurnAngle":
					sendMessage(new TurnAngle(Double.parseDouble(currentMessage.getValues()[0])));
					break;
				case "MotorCommand":
					if(currentMessage.getValues()[1].equals("leftDrive"))
						sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]),Motor.leftDrive));
					else if(currentMessage.getValues()[1].equals("rightDrive"))
						sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]),Motor.rightDrive));
					else if(currentMessage.getValues()[1].equals("centerDrive"))
						sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]),Motor.centerDrive));
					break;
			}
			
		}
		
		step();
	}
	
	public void run() {
		while(enabled){
			iterate();
			sleep();
		}
	}

	public String toString() {
		return "Actor: \tServerParser";
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}
	
}
