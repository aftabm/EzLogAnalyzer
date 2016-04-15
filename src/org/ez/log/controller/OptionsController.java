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

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.ActionType;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;

public class OptionsController extends Controller implements ActionListener
{
	String command;
	//private JTextField view;
	private static final ConsoleLogger<OptionsController> logger = ConsoleLogger.create(OptionsController.class);
	
	@Override
	public void actionPerformed(final ActionEvent e) 
	{
		ActionType actionType = ActionType.parse(e.getActionCommand());
		Event event = null;
		
		switch(actionType)
		{
			case AUTO_SCROLL:
				event = new Event(EventType.AUTOSCROLL_CLICKED);
				event.setData(EventDataKey.SELECTED, MainView.getInstance().isAutoScrollSelected());
				EventManager.getInstance().fireEvent(event);
				break;
			
			case WRAP:
				event = new Event(EventType.WRAP_CLICKED);
				event.setData(EventDataKey.SELECTED, MainView.getInstance().isWrapSelected());
				EventManager.getInstance().fireEvent(event);
				break;
				
			case PAUSE:
				event = new Event(EventType.PAUSE_CLICKED);
				event.setData(EventDataKey.SELECTED, MainView.getInstance().isPauseSelected());
				EventManager.getInstance().fireEvent(event);
				break;
				
			case NO_DUPLICATES:
				ControllerFactory.getLogViewController().doNoDuplicates();
				
			case TAIL:
				ControllerFactory.getLogViewController().doTail();
			break;
			
			case FOLD:				
				event = new Event(EventType.FOLD_CLICKED);
				event.setData(EventDataKey.SELECTED, MainView.getInstance().isFoldSelected());
				EventManager.getInstance().fireEvent(event);
				
				
				break;
				
			default:
				break;
		}
	}

}
