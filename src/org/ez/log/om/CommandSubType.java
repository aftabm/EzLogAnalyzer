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

public enum CommandSubType   
{
	word, line, thread_name, log_level, class_name, message, keyword, Null;

	public static CommandSubType parse(String subType) 
	{
		CommandSubType type =Null;
		try
		{
			type = CommandSubType.valueOf(subType.toLowerCase());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			//ignore
		}
		
		return type;
	}
}
