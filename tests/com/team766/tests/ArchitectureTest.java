package com.team766.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import lib.ConstantsFileReader;

import org.junit.Test;

import tests.RobotTestCase;

public class ArchitectureTest extends RobotTestCase{
	@Test
	public void testReloadConstants() throws Exception{
		
		BufferedReader reader = new BufferedReader(new FileReader(this.getClass().getClassLoader().getResource(ConstantsFileReader.fileName).getPath()));
		
		//Grab first constant from a file to test with
		String[] tokens = reader.readLine().split(",");
		String constantName = tokens[0];
		double value = Double.parseDouble(tokens[1]);
		
		String restOfFile = "";
		String currLine = reader.readLine();
		while(currLine != null){
			restOfFile += currLine + "\n";
			currLine = reader.readLine();
		}
		reader.close();
		
		//Check loaded number correctly
		assertTrueTimed(() -> {return ConstantsFileReader.getInstance().get(constantName) == value;}, 2);

		//Change value and save to file
		double newValue = value + 10;	
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.getClass().getClassLoader().getResource(ConstantsFileReader.fileName).getPath()));
		writer.write(constantName + ", " + newValue + "\n" + restOfFile);
		writer.close();
		
		//Load in new value
		disableInit();
		
		//Check that new value has been added
		assertTrueTimed(() -> {return ConstantsFileReader.getInstance().get(constantName) == newValue;}, 2);

		//Change the value back
		writer = new BufferedWriter(new FileWriter(this.getClass().getClassLoader().getResource(ConstantsFileReader.fileName).getPath()));
		writer.write(constantName + ", " + value + "\n" + restOfFile);
		writer.close();
		
	}
}
