/**
 *   Copyright � 2013 Aftab Mahmood
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details <http://www.gnu.org/licenses/>.
 **/
package org.ez.log.controller;

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.ez.log.util.ConsoleLogger;

//Classic example of benefit of interface. I cannot drive this class from abstract viewcontroller but I want the same interface.   
public class StatusViewController
{
	private static final ConsoleLogger<ViewController> logger = ConsoleLogger.create(ViewController.class);
	private LinkedBlockingQueue<String> displayQueue=null;
	private JTextField view;
	
	private boolean pasued=false;
	
	private String[] waitChars= {"|","/","-","\\","|","/","-","\\"};
	private int nextWaitCharIndex=0;

	public StatusViewController()
	{
		this.displayQueue = new LinkedBlockingQueue<String>();
		startViewUpdateThread();
	}
	

	public void clearView()
    {
		this.view.setText("");
    }
	

	/*
	 * Appends to document and display queue
	 * */
	public void display(String line, boolean recordAsHistory) 
	{
		if(line==null)
			return;
		
		this.displayQueue.offer(line);	
		
		if(recordAsHistory)
			ControllerFactory.getHistoryViewController().insert(line);
	}

	public void setView(JTextComponent view) 
	{
		this.view = (JTextField) view;		
	}	


    protected void startViewUpdateThread() 
	{
		Thread statusBarUpdateThread = new Thread(new Runnable() 
		{
			String line;
			
			@Override
			public void run() 
			{
				while(true)
				{
					try 
					{
						if (pasued)
						{
	                        Thread.sleep(1000);
							continue;
						}
						
						line = displayQueue.take();
						view.setText(line);
					} 
					catch (InterruptedException e) 
					{
						logger.error("startViewUpdateThread", e);
					}
				}
			}
		});
		
		statusBarUpdateThread.setName("statusBarUpdateThread");
		statusBarUpdateThread.setDaemon(true);
		statusBarUpdateThread.start();
	}

	public void update(String line)
	{
		if (line!=null)
		{
			display(waitChars[nextWaitCharIndex++]+"   "+line, false);
			
			if (nextWaitCharIndex==waitChars.length)
				nextWaitCharIndex=0;
		}
	}

}
