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

import org.ez.log.controller.ControllerFactory;
import org.ez.log.util.DefaultProperties.PropertyName;

public class ConsoleLogger<T>
{
	public static  enum LoggingLevel{INFO, DEBUG, WARN, ERROR, TRACE, STD_OUT, NONE}
	private static LoggingLevel loggingLevel=LoggingLevel.INFO;
	
	public static <T> ConsoleLogger<T> create(Class<T> contextClazz)
	{
		return create(contextClazz, null);
	}

	public static <T> ConsoleLogger<T> create(Class<T> contextClazz, String subContext)
	{
		ConsoleLogger<T> logger = null;
		logger = new ConsoleLogger<T>(contextClazz, subContext);
		
		return logger;
	}
	
	public static void setLogLevel(LoggingLevel newLogLevel)
	{
		loggingLevel = newLogLevel;
	}
	
	
	private String className;

	private  ConsoleLogger(Class<T> clazz, String subContext)
    {
		if (subContext!=null && !subContext.isEmpty())
			this.className = clazz.getSimpleName()+"-"+subContext;
		else
			this.className = clazz.getSimpleName();
	    
	    try
	    {
	    	loggingLevel=LoggingLevel.valueOf(DefaultProperties.get(PropertyName.logging_level, LoggingLevel.INFO.name()));
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
    }
	
	
		private  void printStack(String location, StackTraceElement[] stackTraces)
	    {
		    for (StackTraceElement stackTrace: stackTraces)
		    {
		    	error("", stackTrace.toString());
		    }
		    
	    }
		
		public  void debug(String location, String message)
		{
			
			String out = formatMessage(LoggingLevel.DEBUG, location, message);
			
			switch(loggingLevel)
			{
				case STD_OUT:
					System.out.println(out);
				break;
				
				case TRACE:
				case DEBUG:
					ControllerFactory.getHistoryViewController().display(out);
				break;

				default:
					break;
			}
		}	
		
		public void error(String location, Exception e)
	    {
			
			if (loggingLevel.toString().startsWith("STD"))
			{
				e.printStackTrace();
				return;
			}
			
			error(location, e.toString());
		    printStack(location, e.getStackTrace());
	    }

		public  void error(String location, String message)
		{
			
			String out = formatMessage(LoggingLevel.ERROR, location, message);
			
			switch(loggingLevel)
			{
			case STD_OUT:
				System.err.println(out);
			break;
			
			case INFO:
			case DEBUG:
			case TRACE:
			case ERROR:
				ControllerFactory.getHistoryViewController().display(out);
				break;
			
			default:
				break;
			}
		}

		public  void info(String location, String message)
		{
			String out = formatMessage(LoggingLevel.INFO, location, message);
			
			switch(loggingLevel)
			{
			
			case STD_OUT:
				System.out.println(out);
			break;

			case TRACE:
			case DEBUG:
			case INFO:
				ControllerFactory.getHistoryViewController().display(out);
				break;
			default:
				break;
			}
		}

		public void trace(String location, String message)
        {
			String out = formatMessage(LoggingLevel.TRACE, location, message);
			
			switch(loggingLevel)
			{
			case STD_OUT:
				System.out.println(out);
			break;

			
			case TRACE:
				ControllerFactory.getHistoryViewController().display(out);
				break;
				
			default:
				break;
				
			}

        }
		
		
		public void warn(String location, String message)
        {
			
			String out = formatMessage(LoggingLevel.WARN, location, message);
			
			switch(loggingLevel)
			{
			case STD_OUT:
				System.out.println(out);
			break;

			case TRACE:
			case DEBUG:
				ControllerFactory.getHistoryViewController().display(out);
				break;
			default:
				break;
			}

        }		
		
		
		private String formatMessage(LoggingLevel level, String locationIdentifier, String message)
		{
			StringBuilder sb = new StringBuilder();
			
			if (locationIdentifier!=null && !locationIdentifier.isEmpty())
			{
				sb.append(DateUtil.getCurrentDateTime());
				sb.append(" ").append(level.name()).append(" ");
				sb.append(" ").append(this.className).append(":");
				sb.append(locationIdentifier).append(" ");
			}
			else
			{
				sb.append("     ");
			}
			
			sb.append(message);
			
			if(!message.endsWith(System.lineSeparator()))
				sb.append(System.lineSeparator());
			
			return sb.toString();
		}
		
}
