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
import java.util.List;

public enum EventType
{
	ROOT(null, "root"),
	
	DISPOSED(ROOT, "disposed"),
	
	MENU_CLICKED(ROOT, "menu"),
	LOG_FILE_SELECTED(MENU_CLICKED, "logFileSelected"), 
	FILTER_SELECTED(MENU_CLICKED, "filterSelected"),
	
	OPTION_SELECTED(ROOT, "checkbox"), 
	AUTOSCROLL_CLICKED(OPTION_SELECTED, "autoScrollClicked"), 
	WRAP_CLICKED(OPTION_SELECTED,"wrapClicked"), 
	PAUSE_CLICKED(OPTION_SELECTED, "pauseClicked"), 
	TAIL_CLICKED(OPTION_SELECTED, "tailClicked"), 
	FOLD_CLICKED(OPTION_SELECTED, "foldClicked"), 
	
	DATA_CHANGED(ROOT, "dataChanged"),
	NEW_LOG_LINE(DATA_CHANGED, "newLogLine"), 
	;
	
	public final EventType parent;
	public final String leafName;
	public final String namespace;

	private List<EventType> children=null;
	

	
    private List<EventType> ancestry=null;

    private EventType(EventType parent, String leafName)
	{
		this.parent=parent;
		this.leafName = leafName;
		
		StringBuilder sb = new StringBuilder();

        if (parent!=null)
        	sb.append(parent.namespace).append(".");
        if (leafName!=null)
        	sb.append(leafName);
        else
        	throw new IllegalStateException("Invalid format for: " +this.name());

        this.namespace=sb.toString();
	}

    public List<EventType> getAncestry()
    {
    	if(ancestry==null)
    	{
    		synchronized(EventType.class)
    		{
    			ancestry = new ArrayList<EventType>();    		
    			EventType eventType= this;

    			while (eventType!=null)
    			{
    				ancestry.add(eventType);
    				eventType = eventType.parent;
    			}
    		}
    	}
    	
    	return ancestry;
    }

    public List<EventType> getChildren()
    {
    	if (children==null)
    	{
    		synchronized(EventType.class)
    		{
    			children = new ArrayList<EventType>();

    			for (EventType eventType:values())
    			{
    				if (eventType.parent != null && eventType.parent.equals(this))
    					children.add(eventType);
    			}
    		}
    	}

    	return children;
    }

    public boolean isAncestorOf(EventType other)
    {
    	return other.namespace.startsWith(this.namespace);
    }

    public boolean isChildOf(EventType other)
    {
    	return this.parent.equals(other);
    }    
    public boolean isDecendentOf(EventType other)
    {
    	return this.namespace.startsWith(other.namespace);
    }
    
    public boolean isParentOf(EventType other)
    {
    	return this.equals(other.parent);
    }    
    public boolean isSibling(EventType other)
    {
    	return this.parent.equals(other.parent);
    }
	
}
