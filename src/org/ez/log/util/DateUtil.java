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
package org.ez.log.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil 
{
	public static final String DEFAULT_DATE_FORMAT =  "yyyy-MM-dd HH:mm:ss,SSS";
	private static DateFormat defaultFormater = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	
	public static String dateToString(Date date, String pattren) 
	{
		if (pattren!=null && !pattren.trim().isEmpty())
		{
			DateFormat formater = new SimpleDateFormat(pattren);
			return formater.format(date);
		}
			
		return defaultFormater.format(date) ;
	}

	
	public static String getCurrentDateTime() 
	{
		return defaultFormater.format(new Date(System.currentTimeMillis()));
	}
	
	
	public static Date stringToDate(String date, String pattren) 
	{
		if (date==null || date.trim().isEmpty())
			return null;
		
		if (pattren!=null && !pattren.trim().isEmpty())
		{
			DateFormat formater = new SimpleDateFormat(pattren);
			try
            {
	            return formater.parse(date);
            }
            catch (Exception e)
            {
	            e.printStackTrace();
            }
		}
		
		return null;
	}
	
	
	public static Date stringToDate(String date, String time, String pattren) throws ParseException 
	{
		if(time==null || time.trim().isEmpty())
			time="00:00:00,000";
		
		return stringToDate(date+" "+time, pattren);
		
	}	

}
