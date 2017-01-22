package com.team766.robot;

import interfaces.MyRobot;
import lib.HTTPServer;
import lib.LogFactory;
import lib.Scheduler;
import trajectory.AutoPaths;

import com.team766.lib.CommandBase;
import com.team766.robot.Actors.OperatorControl;
import com.team766.robot.Actors.Auton.AutonSelector;
import com.team766.robot.Actors.Drive.Drive;

/**
 * 2017 Robot code
 * 
 * @author Brett Levenson
 */
public class Robot implements MyRobot {
	private long prevTime;
	private boolean autonDone = false;
	private boolean teleopDone = false;
	
	private final long RUN_TIME = 30;
	private long lastSleepTime = 0;
	
	public enum GameState{
		Teleop,
		Disabled,
		Test,
		Auton
	}
	
	public static GameState gameState = GameState.Disabled;

    public static GameState getState() {
        return gameState;
    }

    public static void setState(GameState state) {
        gameState = state;
    }
	
	public void robotInit() {
		CommandBase.init();
		LogFactory.createInstance("General");
		
		Scheduler.getInstance().add(new Drive());
		
		AutoPaths.loadPaths();
		System.out.println("IM ALIVE!!");
		
		new Thread(new HTTPServer(Constants.AUTONS)).start();
		
		prevTime = System.currentTimeMillis();
    }
    
    public void autonomousInit() {
    	LogFactory.getInstance("General").print("Auton Init");
    	setState(GameState.Auton);
    	emptyInboxes();
    	Scheduler.getInstance().add(new AutonSelector(Constants.getAutonMode()));
    	
    	autonDone = true;
    }

    public void autonomousPeriodic() {
//    	if(System.currentTimeMillis() - prevTime >= 1000){
////    		System.out.println(Scheduler.getInstance().getCountsPerSecond());
//    		prevTime = System.currentTimeMillis();
//    	}
    	sleep();
    }
    
    public void teleopInit() {
    	LogFactory.getInstance("General").print("Teleop Init");
    	setState(GameState.Teleop);
    	emptyInboxes();
    	
    	Scheduler.getInstance().remove(AutonSelector.class);
    	
		Scheduler.getInstance().add(new OperatorControl());
		teleopDone = true;
	}

    public void teleopPeriodic() {
    	sleep();
    }
    
    public void disabledInit() {
    	LogFactory.getInstance("General").print("Robot Disabled");
    	
    	Scheduler.getInstance().remove(OperatorControl.class);
    	
    	if(autonDone && teleopDone){
			LogFactory.closeFiles();
		}
	}
    
    public void disabledPeriodic() {
	}
    
	public void testInit() {
	}
	
	public void testPeriodic() {
    }
	
	public void startCompetition(){
		System.out.println("Wrong one...close enough? lmao");
	}
	
	private void emptyInboxes(){
		Scheduler.getInstance().getActor(Drive.class).clearInbox();
	}
	
	private void sleep(){
		//Run loops at set speed
		try {
			//System.out.println("Curr: " + System.currentTimeMillis() + "\tLast: " + lastSleepTime);
			Thread.sleep(RUN_TIME - (System.currentTimeMillis() - lastSleepTime));
		} catch (Exception e) {
			System.out.println(toString() + "\tNo time to sleep, running behind schedule!!");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {}
		}
		
		lastSleepTime = System.currentTimeMillis();
	}
}