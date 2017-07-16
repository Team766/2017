package com.team766.robot.Actors;

import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.MotorCommand.Motor;
import com.team766.lib.Messages.TurnAngle;
import lib.Actor;
import lib.DashboardMessage;
import lib.LogMessage.Level;

import java.util.List;

/**
 * @author Quinn Tucker
 */
public class DashboardDrive extends Actor {
	
	@Override
	public void init() {
		acceptableMessages = new Class[] {DashboardMessage.class};
	}
	
	@Override
	public void run() {
		while (enabled) {
			iterate();
			sleep();
		}
	}
	
	@Override
	public void iterate() {
		if (newMessage()) {
			DashboardMessage message = (DashboardMessage)readMessage();
			List<String> args = message.getArgs();
			switch (message.getType()) {
				case "TurnAngle":
					double angle = Double.parseDouble(args.get(0));
					sendMessage(new TurnAngle(angle));
					break;
				case "MotorCommand":
					double value = Double.parseDouble(args.get(1));
					Motor motor = null;
					switch (args.get(0)) {
						case "left":   motor = Motor.leftDrive;   break;
						case "right":  motor = Motor.rightDrive;  break;
						case "center": motor = Motor.centerDrive; break;
						default: log(Level.WARNING, "Bad MotorCommand motor: \""+args.get(0)+"\"");
					}
					sendMessage(new MotorCommand(value, motor));
					break;
			}
		}
	}
	
	@Override
	public void step() {}
}
