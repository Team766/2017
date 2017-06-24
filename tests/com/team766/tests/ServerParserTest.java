package com.team766.tests;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import org.junit.Test;

import tests.Gyro;
import tests.RobotTestCase;

import com.team766.lib.ConfigFile;
import com.team766.robot.Constants;

public class ServerParserTest extends RobotTestCase {
	private static final int PORT = Constants.MESSAGE_PORT;
	private static final int TEST_ITERATIONS = 10;
	
	private PrintWriter out;
	
	private Random rand;
	
	public ServerParserTest() {
		//Seed so the tests are deterministic
		rand = new Random(100);
	}
	
	@Test
	public void testServerMotorControl() throws Exception{
		createSocketConnection();
		
		//Move right motor randomly
		for(int i = 0; i < TEST_ITERATIONS; i++){
			
			//Random num between -1 and 1
			double ranDumb = 2.0 * rand.nextDouble() - 1.0;
			
			out.println("MotorCommand " + ranDumb + " rightDrive");
			assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == ranDumb;}, 2);
		}
		
		//Move left motor randomly
		for(int i = 0; i < TEST_ITERATIONS; i++){
			
			//Random num between -1 and 1
			double ranDumb = 2.0 * rand.nextDouble() - 1.0;
			
			out.println("MotorCommand " + ranDumb + " leftDrive");
			assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == ranDumb;}, 2);
		}
		
		//Move center motor randomly
		for(int i = 0; i < TEST_ITERATIONS; i++){
			
			//Random num between -1 and 1
			double ranDumb = 2.0 * rand.nextDouble() - 1.0;
			
			out.println("MotorCommand " + ranDumb + " centerDrive");
			assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == ranDumb;}, 2);
		}
	}
	
	public void testServerTurnControl() throws Exception{
		createSocketConnection();	
		
		double currAngle;
		
		((Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(0);
		for(int i = 0; i < TEST_ITERATIONS; i++){
			
			//Current robot heading
			currAngle = instance.getGyro(ConfigFile.getGyro()).getAngle();
			
			//Random num between -90 and 90
			double ranDumbAngle = 180.0 * rand.nextDouble() - 90.0;
					
			//Tell to turn
			out.println("TurnAngle " + ranDumbAngle);
			
			System.out.printf("curr: %f\tangle: %f\n", currAngle, ranDumbAngle);
			
			//Check turning in correct direction  
			if(ranDumbAngle < currAngle){  //Turn Left
				assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 0.0;}, 2);
				assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() < 0.0;}, 2);
			}else if(ranDumbAngle > currAngle){  //Turn Right
				assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() < 0.0;}, 2);
				assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() > 0.0;}, 2);
			}else{  //At angle, don't need to turn
				checkStoppedTurning();
			}
			//Center shouldn't be moving during a turn
			assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0.0;}, 2);
			
			//Move robot to the correct angle
			((Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(ranDumbAngle);
			
			//Check that motors stop now that the robot has the correct heading
			checkStoppedTurning();
		}
	}
	
	private void checkStoppedTurning() throws Exception {
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0.0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getCenterMotor()).get() == 0.0;}, 2);
		assertTrueTimed(() -> {return instance.getMotor(ConfigFile.getLeftMotor()[0]).get() == 0.0;}, 2);
	}
	
	
	private void createSocketConnection(){
		//Check if already inited
		if(out != null)
			return;
		
		//Connect to robot
        Socket socket;
		try {
			socket = new Socket("localhost", PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
