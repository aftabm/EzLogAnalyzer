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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Event 
{
	public static Event createEvent(EventType type)
	{
		return new Event(type);
	}
	EventType type=null;
	Map<EventDataKey, Object> data = new HashMap<EventDataKey, Object>();
	
	private String stringDump;
	
	public Event(EventType type)
	{
		this.type = type;
	}
	
	public <T> T getData(EventDataKey key, Class<T> t)
	{
		T out=null;
		
		try
		{
			if (data!=null && data.containsKey(key))
			{
				Object o = data.get(key);
				out =  t.cast(o	);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        return out;
	}

	
	public EventType getType()
    {
	    return this.type;
    }
	
	
	public void setData(EventDataKey key, final Object value)
	{
		this.stringDump=null;
		
		if (data==null)
			data = new HashMap<EventDataKey, Object>();
		
		data.put(key, value);
	}

	public String toString()
	{
		if (stringDump==null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Event[ ");
			sb.append("TYPE=").append(this.type).append(" ");
			
			sb.append("DATA=");
		
			for (Entry entry: this.data.entrySet())
			{
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
			}
		
			sb.append("]]");
			stringDump=sb.toString().replace(", ]", " ]");
		
		}
		
		return stringDump;
		
	}
}
