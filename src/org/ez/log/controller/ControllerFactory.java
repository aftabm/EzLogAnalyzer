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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import org.ez.log.view.MainView;

public class ControllerFactory 
{

	private static LogFileOpenDlgController logFileOpenDlgController;
	private static FilterDlgController filterDlgController;
	private static LogViewController logViewController;
	private static HistoryViewController historyViewController;
	private static CommandController commandController;
	private static ErrorViewController errorViewController=null;
	private static Map<String, ViewController> viewToControllerMap = new HashMap<String, ViewController>();
	private static FileSaveDlgController fileSaveDlgController;
	private static StatusViewController statusViewController;
	private static MenuController menuController;
	private static OptionsController optionsController;
		
	public static CommandController getCommandController() 
	{
		if (commandController==null)
		{
			synchronized(ControllerFactory.class)
			{
				commandController = new CommandController();
			}
		}
		
		return commandController;

	}

	public static ErrorViewController getErrorViewController() 
	{

				return errorViewController;
	}

	public static FileSaveDlgController getFileSaveDlgController() 
	{
		if (fileSaveDlgController==null)
		{
			synchronized(ControllerFactory.class)
			{
				fileSaveDlgController = new FileSaveDlgController();
			}
		}
		
		return fileSaveDlgController;
	}

	public static FilterDlgController getFilterDlgController() 
	{
		if (filterDlgController==null)
		{
			synchronized(ControllerFactory.class)
			{
				filterDlgController = new FilterDlgController();
			}
		}
		
		return filterDlgController;
	}
	
	
	public static HistoryViewController getHistoryViewController() 
	{

		if (historyViewController==null)
		{
			synchronized(ControllerFactory.class)
			{
				historyViewController = new HistoryViewController();
			}
		}
		
		return historyViewController;
		
	}

	
	
	public static LogViewController getLogViewController() 
	{
		return logViewController;
	}

	
	public static LogFileOpenDlgController getLogFileOpenDlgController() 
	{
		if (logFileOpenDlgController==null)
		{
			synchronized(ControllerFactory.class)
			{
				logFileOpenDlgController = new LogFileOpenDlgController();
			}
		}
		
		return logFileOpenDlgController;
	}
	
	public static StatusViewController getStatusViewController()
    {
		if (statusViewController==null)
		{
			synchronized(ControllerFactory.class)
			{
				statusViewController = new StatusViewController();
			}
		}
		
		return statusViewController;
    }

	
	public static ViewController getViewController(String viewName)
	{
		return viewToControllerMap.get(viewName);
	}

	public static MenuController getMenuBarController()
    {
		if (menuController==null)
		{
			synchronized(ControllerFactory.class)
			{
				menuController = new MenuController();
			}
		}
		
		return menuController;
    }	
	
	
	public static OptionsController getOptionBarController()
    {
		if (optionsController==null)
		{
			synchronized(ControllerFactory.class)
			{
				optionsController = new OptionsController();
			}
		}
		
		return optionsController;
    }

	
	public static FilteredViewController creatFilteredViewController()
    {
		FilteredViewController controller = new FilteredViewController();
		JTextComponent view = MainView.getInstance().creatFilterView();
		controller.setView(view);
		viewToControllerMap.put(view.getName(), controller);
				
	    return controller;
    }

	public static void removeController(String viewname)
    {
		ViewController controller = viewToControllerMap.get(viewname);
		
		if (controller!=null)
		{
			controller.finalize();
			viewToControllerMap.remove(viewname);
			MainView.getInstance().removeTab(viewname);
		}
    }

	public static LogViewController creatLogViewController(JTextPane view)
    {
		if (logViewController==null)
		{
			synchronized(ControllerFactory.class)
			{
				logViewController = new LogViewController();
				logViewController.setView(view);
				viewToControllerMap.put(view.getName(),logViewController );
			}
		}
		
		return logViewController;
	    
    }

	public static ErrorViewController createErrorViewController(JTextArea view)
    {
		if (errorViewController==null)
		{
			synchronized(ControllerFactory.class)
			{
				errorViewController = new ErrorViewController();
				errorViewController.setView(view);
				viewToControllerMap.put(view.getName(),errorViewController );
			}
		}
		
		return errorViewController;
    }

	public static List<ViewController> getViewControllers()
    {
		return new ArrayList<ViewController>(viewToControllerMap.values());
    }	

}
