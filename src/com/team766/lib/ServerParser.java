package com.team766.lib;

import java.util.Arrays;

import lib.LogMessage.Level;
import lib.MessageServer;
import lib.ServerMessage;

import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.MotorCommand.Motor;
import com.team766.lib.Messages.TurnAngle;

public class ServerParser extends MessageServer {
	
	private ServerMessage currentMessage;
	
	public ServerParser(int port){
		super(port);
	}
	
	@Override
	public void init() { 
		acceptableMessages = new Class[]{};
	}

	@Override
	public void iterate(){
		currentMessage = grabData();
		
		if(currentMessage != null){

			//System.out.println("Parsing: " + currentMessage.getMessageName() + "\t" + Arrays.toString(currentMessage.getValues()));
			log("Parsing: " + currentMessage.getMessageName() + "\t" + Arrays.toString(currentMessage.getValues()));
			
			switch(currentMessage.getMessageName()){
				case "TurnAngle":
					System.out.println("Turning to angle: " + currentMessage.getValues()[0]);
					sendMessage(new TurnAngle(Double.parseDouble(currentMessage.getValues()[0])));
					break;
				case "MotorCommand":
					switch(currentMessage.getValues()[1]){
						case "leftDrive":
							sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]), Motor.leftDrive));
							break;
						case "rightDrive":
							sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]), Motor.rightDrive));
							break;
						case "centerDrive":
							sendMessage(new MotorCommand(Double.parseDouble(currentMessage.getValues()[0]), Motor.centerDrive));
							break;
					}
													
					break;
				default:
					log(Level.ERROR, "Message name not found: " + currentMessage.getMessageName());
					break;
			}
			
		}
		
		step();
	}
	
	public void run() {
		connect();
		
		while(enabled){
			iterate();
			sleep();
		}
		
		closeSockets();
	}

	public String toString() {
		return "Actor: \tServerParser";
	}

	@Override
	public void step() {
	}
	
}
