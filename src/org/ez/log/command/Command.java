package org.ez.log.command;

import java.util.Arrays;
import java.util.concurrent.Future;

import org.ez.log.controller.ViewController;
import org.ez.log.om.CommandType;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;

public abstract class Command
{
	
	protected static ConsoleLogger<Command> logger =null;
	
	protected final CommandType type;
	protected String[] arguments=null;
	protected MainView mainView = MainView.getInstance();
	protected ViewController inputController;
	protected ViewController outputController;
	
	protected Command(CommandType type)
	{
		logger = ConsoleLogger.create(Command.class, type.name());
		
		this.type = type;
	}
	
	public final Future<?> asyncExecute()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				execute();				
			}
		});
		
		thread.setName(type.name());
		thread.setDaemon(true);
		thread.start();

		return null;
		
	}
	
	
	abstract public boolean execute();
	
	public final CommandType getType()
	{
		return type;
	}
	
	protected boolean isValidState()
	{
		return arguments!=null &&arguments.length>0 && inputController!=null && outputController!=null;
	}
	
	public void setArguments(String[] arguments)
	{
		this.arguments = arguments;
	}
	
	public void setInputController(ViewController inController)
	{
		this.inputController = inController;
	}
	
	public void setOutputController(ViewController outController)
	{
		this.outputController = outController;
	}

	@Override
    public String toString()
    {
	    return "Command [type=" + type + ", arguments=" + Arrays.toString(arguments) + "]";
    }
	
	public String getStatement()
	{
		 return type + Arrays.toString(arguments);
	}
	
}
