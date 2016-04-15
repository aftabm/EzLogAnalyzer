/**
 *   Copyright © 2013 Aftab Mahmood
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

public enum FilterOperator 
{
	AND, OR, NOT, NONE;
	
	public static FilterOperator fromString(String value)
	{
		if(value!=null && !value.trim().isEmpty())
		{
			try
			{
				FilterOperator out = FilterOperator.valueOf(value.toUpperCase());
				
				if(out==null) //should never happend
					out = NONE;
				
				return out;			
			}
			catch(Exception e)
			{
			
			}
		}
		
		return NONE;
	}
}
