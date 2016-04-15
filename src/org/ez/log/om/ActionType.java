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
package org.ez.log.om;

import java.util.HashMap;
import java.util.Map;

public enum ActionType 
{
	OPEN_LOG("Open", "Select log file(s) for reading"), 
	RELOAD_LOG("Reload","Reload last log file"),
	FILTER("Filter", "Create or load filter from file"),  HELP("Help",""), 
	OPEN_LOGS("Open Logs", "Select log files"), 
	EXPORT("Export","Save Filter and  Errors view to file."),
	
	
	FIND("Find", "Find text in current view."),
	CLEAR_VIEW("Clear","Clear current view"), 
	REFRESH_VIEW("Refresh","Removes all highlights and re reads data from buffer"),
	
		 
	
	PAUSE("Pause","Halts file reading."), 
	TAIL("Tail","Keep scanning log file for new updates."), 
	WRAP("Wrap","Wrap line"),EXECUTE(" Execute ",""),
	NO_DUPLICATES("No Duplicates","Removes line containg same message"),	
	AUTO_SCROLL("Auto Scroll","Enable or disable auto scrolling of the view."),	
	CLOSE("Close", "Close"), 
	FOLD("Fold", "Fold associated lines"),
	CLOSE_VIEW("Close","Close current view"),
	
	COPY("Copy", "Copy selection to clipboard"),
	
	OK("OK",""), CANCEL("CANCEL",""), 
	
	 
	/*
	BOOKMARK("Bookmark",""),	
	PREFERENCES("Preferences",""), 
	EMAIL("Email",""),
	ERRORS("Errors",""), 
	RUN("Run",""),
	HISTORY(" History ",""),
	APPROVE_SELECTION("ApproveSelection",""), 
	CANCEL_SELECTION("CancelSelection",""),
	DISABLE_FILTER("Disable Filter","Stop applying filter to new data"),
	ADD_VIEW("Add View","Add a filter view"),*/
	
	
	UNDEFINED("undefined","");
	
	
	public final String label; 
	public final String tooltip;
	private static Map<String, ActionType> label2EnumMap=null;
	
	public static ActionType parse(String value)
	{
		if (label2EnumMap==null)
		{
			label2EnumMap = new HashMap<String, ActionType>();
				
			for (ActionType actionType:values())
				label2EnumMap.put(actionType.label, actionType);
		}
		
		ActionType out = null;
		
		try
		{
			out = ActionType.valueOf(value);
		}
		catch (Exception e)
		{
			//ignore
		}
		
		if (out==null)
		{
			if (!label2EnumMap.containsKey(value))
				return UNDEFINED;
			
			out = label2EnumMap.get(value);
		}
		
		return out;
	}
	
	ActionType(String label, String tooltip)
	{
		this.label = label;
		this.tooltip=tooltip;
	}
	
	@Override
	public String toString()
	{
		return this.label;
	}
}