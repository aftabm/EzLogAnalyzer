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
package org.ez.log.util;

import java.awt.Component;
import java.awt.Point;

public class UiUtil 
{
	
	public static Point getCenterLocation(Component parent, Component child)
	{
		int x = parent.getLocation().x+parent.getWidth()/2;
		int y = parent.getLocation().y+parent.getHeight()/2;
		int w =child.getWidth()/2;
		int h= child.getHeight()/2;
		
		if (w > x)
			w=0;
		if (h > y)
			h=0;
	
		return new Point(x-w, y-h);
	}
	
}
