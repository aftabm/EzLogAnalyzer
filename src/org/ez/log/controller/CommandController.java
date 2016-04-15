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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.ez.log.command.Command;
import org.ez.log.command.CommandFactory;
import org.ez.log.om.ActionType;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;

public class CommandController extends Controller implements ActionListener
{
	String command;
	private static final ConsoleLogger<CommandController> logger = ConsoleLogger.create(CommandController.class);
	private Map<String, Command> filterToCommandMap = new WeakHashMap<String, Command>(); 
	private Map<String, String> viewToFilterMap = new WeakHashMap<String, String>();
	private ViewController recentonlyController;
	private ViewController groupByThreadController;
	
	@Override
	public void actionPerformed(final ActionEvent e) 
	{
		ActionType actionType = ActionType.parse(e.getActionCommand());
		
		switch(actionType)
		{
			case EXECUTE:
				Thread cmdProcessThread=new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{   
						String command =(String)e.getSource(); 
						ControllerFactory.getStatusViewController().display("Processing: "+command, true);

						executeCommand(command);	
					}
				});
				
				cmdProcessThread.setDaemon(true);
				cmdProcessThread.start();

			break;
			
			case CLOSE:
				String viewname = ((JComponent)e.getSource()).getName();
				ControllerFactory.removeController(viewname);
				
				String filterName = viewToFilterMap.get(viewname);
				filterToCommandMap.remove(filterName);
				viewToFilterMap.remove(viewname);

				break;
			
			default:
				break;
		}
	}

	
	private void executeCommand(String statement) 
	{
		ViewController controller=null;
		
		try
		{
			Command command = CommandFactory.parse(statement);
			
			switch(command.getType())
			{
				case findall:
				case remove:
				case replace:
					controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
					
					if(controller!=null)
					{
						command.setInputController(controller);
						command.setOutputController(controller);
						
						command.execute();
					}
					break;
					
				case recentonly:
					controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
					
					if(controller!=null)
					{
						if (controller instanceof LogViewController)
						{
							if(recentonlyController ==null)
							{
								recentonlyController = ControllerFactory.creatFilteredViewController();
								MainView.getInstance().setToolTip(recentonlyController.getView().getName(), "recentonly(message)");
							}
							
							command.setInputController(ControllerFactory.getLogViewController());
							command.setOutputController(recentonlyController);	
						}
						else
						{
							command.setInputController(controller);
							command.setOutputController(controller);
						}
						
						command.execute();
					}
					
				break;

				case groupby:
					controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
					
					if(controller!=null)
					{
						if (controller instanceof LogViewController)
						{
							if(groupByThreadController == null)
							{
								groupByThreadController = ControllerFactory.creatFilteredViewController();
								MainView.getInstance().setToolTip(groupByThreadController.getView().getName(), "groupby(thread)");
							}
							
							command.setInputController(ControllerFactory.getLogViewController());
							command.setOutputController(groupByThreadController);	
						}

						else
						{
							command.setInputController(controller);
							command.setOutputController(controller);
						}
						
						command.execute();
					}
					break;
					
				case filter:
					//MainView.getInstance().showView(ViewName.FILTER_VIEW);
					
					if(filterToCommandMap.containsKey(statement))
					{
						command = filterToCommandMap.get(statement);
					}
					else
					{
						ViewController filteredViewController = ControllerFactory.creatFilteredViewController();
						
						viewToFilterMap.put(filteredViewController.getView().getName(), statement);//????
						
						command.setInputController(ControllerFactory.getLogViewController());
						command.setOutputController(filteredViewController);
						
						filterToCommandMap.put(statement, command);
					}
					
					command.execute();
					
					break;

				case help:
					command.execute();
					break;
					
				default:
					break;
			}
		}
		catch (Exception e)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to process command.").append(System.lineSeparator()).append(statement).append(System.lineSeparator());
            JOptionPane.showConfirmDialog (MainView.getInstance().getMainFrame(), sb.toString(),"Error",JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			logger.error("process", e);
		}
	}
}
