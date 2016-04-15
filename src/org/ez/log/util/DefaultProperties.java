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

import java.io.File;
import java.net.URL;
import java.util.Properties;

public class DefaultProperties 
{
	public static enum PropertyName{log_folder, tail_enabled, tail_refresh_rate_millisecond, remove_duplicates, wrap_line, refresh_rate,buffer_size,filter, format, logging_level, 
		font_name, font_style, font_size, 
		filter_view_show_detail, error_view_show_detail}
	
	public static final URL propertyFileUrl = DefaultProperties.class.getResource("/conf/default.properties");
	private static final ConsoleLogger<DefaultProperties> logger = ConsoleLogger.create(DefaultProperties.class);
	private static Properties defaultProperties=null;
	
	private static File propertyFile=null;;
	
	public static String get(PropertyName propertyName)
	{
		
		if (defaultProperties==null)
		{
			readProperties();
		}

		return defaultProperties.getProperty(propertyName.toString());
		
	}


	
	public static String get(PropertyName propertyName, String defaultValue) 
	{
		
		try
		{
			if (defaultProperties==null)
			{
				readProperties();
			}

			String out = defaultProperties.getProperty(propertyName.toString());
			
			if (out!=null && !out.trim().isEmpty())
				return out;
				
		}
		catch(Exception e)
		{
			logger.error("get", e);
		}
		
		return defaultValue;
	}
	
	public static Properties getAll()
	{
		if (defaultProperties==null)
		{
			readProperties();
		}
		
		return defaultProperties;
	}
	
	
	public static File getDefaultLogFolder() 
	{
		File folder = null;
		try
		{
			String folderName = DefaultProperties.get(PropertyName.log_folder);
			
			if (folderName!=null && !folderName.trim().isEmpty())
			{
				folder = new File(folderName);
			
				if (!folder.exists() || !folder.isDirectory() )
				{
					logger.error("getDefaultLogFolder", "Invalid folder name:"+folderName);
					folder = null;
				}
			}
		}
		catch(Exception e)
		{
			logger.error("getDefaultLogFolder", e);
		}
		
		return folder;
	}
	
/*	public static void setLastLogFilename(File file)
	{
		if (defaultProperties==null)
		{
			readProperties();
		}
		
		String lastLogFile = defaultProperties.getProperty(PropertyName.last_log_file.toString());
		
		if (!file.getAbsolutePath().equalsIgnoreCase(lastLogFile))
		{
			defaultProperties.setProperty(PropertyName.last_log_file.toString(), file.getAbsolutePath());
			saveProperties();
		}
	}
*/	
/*	private static void saveProperties()
	{
		FileUtil.writeProperties(propertyFile,defaultProperties);
	}*/

	public static boolean isFilterDuplicate() 
	{
		boolean filterDuplicate = false;
		try
		{
			String value = get(PropertyName.remove_duplicates);
			
			if (value!=null)
				filterDuplicate = Boolean.parseBoolean(value);
		}
		catch(Exception e)
		{
			logger.error("isFilterDuplicate", e);
		}
		
		return filterDuplicate;
	}
	
	
	public static boolean isWrapLine() 
	{
		boolean out = false;
		try
		{
			String value = get(PropertyName.wrap_line);
			
			if (value!=null)
				out = Boolean.parseBoolean(value);
		}
		catch(Exception e)
		{
			logger.error("isWrapLine", e);
		}
		
		return out;
	}	
	
	
	private static void readProperties()
	{
		try 
		{
			propertyFile = new File(propertyFileUrl.toURI());
			
			if (propertyFile.exists())
				defaultProperties = FileUtil.readProperties(propertyFile);
			else
				logger.error("readProperties","Unable to read default properties from "+propertyFile.getAbsolutePath());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}	
	
	
}


/*log_folder=C:/Program Files (x86)/Zenprise/Zenprise Device Manager/tomcat/logs
log_filename=zdm.log
tail_enabled=true
tail_refresh_rate=1000
tail_refresh_rate_unit=millisecond
remove_duplicates=false*/