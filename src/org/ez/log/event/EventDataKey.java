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

import java.awt.List;
import java.io.File;

import org.ez.log.om.Filter;
import org.ez.log.om.LogLine;

public enum EventDataKey 
{
	LOG_FILES(List.class), FILTER_FILE(String.class), FILTER(Filter.class), SELECTED(Boolean.class), 
	LOG_LINE(LogLine.class), DISPOSED(Object.class), FORMAT_FILE(File.class);
	
	public final Class clazz;

	EventDataKey(Class clazz)
	{
		this.clazz = clazz;
	}
}
