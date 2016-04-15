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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.ez.log.util.FileUtil;


public class Filter 
{

	private String name;
	private Map<FilterType, String> data = new HashMap<FilterType, String>();
	private File file=null;

	private String toString=null;

	public Filter(File file) 
	{
		this.file = file;
		
		if (file!=null)
		{
			this.name = file.getName();
			
			if (file.exists())
				load();
		}
	}
	
	public Filter(String name) 
	{
		this.name = name;
	}	
	
	public Filter clone()
	{
		Filter out = new Filter(this.name);
		
		for (Entry<FilterType, String> entry: data.entrySet())
		{
			out.set(entry.getKey(), entry.getValue());
		}
		
		return out;
	}
	
	
	public String get(FilterType type) 
	{
		return this.data.get(type);
	}	


	public boolean isCompliant(LogLine line) 
	{
		return false;
	}

	
	public boolean isEmpty() 
	{
		for(Entry<FilterType, String> entry: this.data.entrySet())
		{
			if (entry.getValue()!=null && !entry.getValue().isEmpty())
			{
				return false;
			}
		}
		
		return true;
	}

	
	private void  load() 
	{
		Properties props = FileUtil.readProperties(file);
		
		for (Entry entry:props.entrySet())
		{
			data.put(FilterType.valueOf((String)entry.getKey()), (String)entry.getValue());
		}
	}
	
	public void  save() 
	{
		Properties props = new Properties();
		
		for (Entry<FilterType, String> entry:data.entrySet())
		{
			props.put(entry.getKey().name(), entry.getValue());
		}
		
		FileUtil.writeProperties(file, props);
	}

	public void  saveAs(File newFile) 
	{
		Properties props = new Properties();
		
		for (Entry<FilterType, String> entry:data.entrySet())
		{
			props.put(entry.getKey().name(), entry.getValue());
		}
		
		FileUtil.writeProperties(newFile, props);
	}

	public void set(FilterType type, String expression) 
	{
		toString=null;
		this.data.put(type, expression.trim());
	}

	@Override
	public String toString() 
	{
		if (toString==null)
		{
			StringBuilder sb = new StringBuilder();
		
			sb.append("Filter[ ");
		
			for (Entry entry : data.entrySet())
			{
				String value = (String)entry.getValue();
				
				if (value!=null && !value.trim().isEmpty())
				{
					sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
				}
			}
		
			sb.append("]");
			
			toString=sb.toString().replace(", ]", " ]");
			
		}
		
		//return "Filter [name=" + name + ", data=" + data + "]";
		return toString;
	}

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((data == null) ? 0 : data.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Filter other = (Filter) obj;
	    if (data == null)
	    {
		    if (other.data != null)
			    return false;
	    }
	    else
		    if (!data.equals(other.data))
			    return false;
	    return true;
    }

}
