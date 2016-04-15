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
package org.ez.log.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager 
{
	private static EventManager instance;
	
	public static EventManager getInstance()
	{
		if (instance==null)
		{
			synchronized(EventManager.class)
			{
				if (instance==null)
					instance = new EventManager();
			}
		}
		
		return instance;
	}
	
	private Map<EventType, List<EventListener> > listeners = new HashMap<EventType, List<EventListener> >(EventType.values().length);
	
	public void addListner(EventType eventType, EventListener listener)
	{
		List<EventListener> eventListeners =  listeners.get(eventType);
		
		if (eventListeners!=null)
		{
			eventListeners.add(listener);
		}		
		else
		{
			eventListeners = new ArrayList<EventListener>();
			eventListeners.add(listener);
			listeners.put(eventType, eventListeners);
		}
			
	}
	
	public void fireEvent(Event event)
	{
		List<EventType> ancestry = event.type.getAncestry();
		
		for (EventType eventType: ancestry)
		{
			List<EventListener> eventListeners = listeners.get(eventType);
			
			if(eventListeners!= null && eventListeners.size()>0)
			{
				for (EventListener listener: eventListeners)
				{
					EventTask eventTask = new EventTask(listener, event);
					eventTask.start();
				}
			}
		}
	}
	
	public void removeListner(EventType eventType, EventListener listener)
	{
		List<EventListener> eventListeners = listeners.get(eventType);
		
		if (eventListeners!=null)
			eventListeners.remove(listener);
	}
	
}
