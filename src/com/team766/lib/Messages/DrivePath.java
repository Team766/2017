package com.team766.lib.Messages;

import lib.Message;
import trajectory.Path;

import com.team766.lib.AutoPaths;

public class DrivePath implements Message{
	
	private Path path;
	private boolean flip;
	
	public DrivePath(String pathName, boolean flip){
		path = AutoPaths.get(pathName);
		this.flip = flip;
	}
	
	public Path getPath(){
		return path;
	}
	
	public boolean getFlip(){
		return flip;
	}
	
	public String toString() {
		return "Message:\tDrive Path";
	}
}
