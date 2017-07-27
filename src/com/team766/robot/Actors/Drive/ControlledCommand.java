package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import lib.control.Controller;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * @author Quinn Tucker
 */
public class ControlledCommand extends CommandBase {
	
	private Controller controller;
	private DoubleSupplier posFunc;
	private DoubleConsumer outFunc;
	private Runnable stopFunc;
	
	public ControlledCommand(Controller controller, DoubleSupplier posFunc, DoubleConsumer outFunc) {
		this(controller, posFunc, outFunc, null);
	}
	
	public ControlledCommand(Controller controller, DoubleSupplier posFunc, DoubleConsumer outFunc, Runnable stopFunc) {
		this.controller = controller; 
		this.posFunc = posFunc;
		this.outFunc = outFunc;
		this.stopFunc = stopFunc;
	}
	
	public Controller getController() {
		return controller;
	}
	
	@Override
	public void update() {
		outFunc.accept(controller.update(posFunc.getAsDouble()));
	}
	
	@Override
	public void stop() {
		if (stopFunc != null)
			stopFunc.run();
	}
	
	@Override
	public boolean isDone() {
		return controller.isDone();
	}
	
}
