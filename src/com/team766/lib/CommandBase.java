package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;

/**
 * Create instances of the subsystems here
 * Later in commands import this class to
 * gain access to the systems presets
 *
 * @author Brett Levenson
 */
public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;
	
	public static void init(){
		Drive = new Drive();
	}

}
