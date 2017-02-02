package com.team766.lib.Messages;

import lib.Message;
import trajectory.Path;

import com.team766.lib.AutoPaths;

public class DrivePath implements Message{
	
	private Path path;
	
	public DrivePath(String pathName){
		path = AutoPaths.get(pathName);
	}
	
	public Path getPath(){
		return path;
	}
	
	public String toString() {
		return "Message:\tDrive Path";
	}
}
