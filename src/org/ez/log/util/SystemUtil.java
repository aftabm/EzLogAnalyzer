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

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;

import org.ez.log.util.DefaultProperties.PropertyName;


public class SystemUtil 
{
	private static final ConsoleLogger<SystemUtil> logger = ConsoleLogger.create(SystemUtil.class);
	
	public static final URL exportFolderUrl = SystemUtil.class.getResource("/export");
	public static final URL confFolderUrl = SystemUtil.class.getResource("/conf");
	public static final URL filterFileUrl = SystemUtil.class.getResource("/conf/default.filter");
	public static final URL formatFileUrl = SystemUtil.class.getResource("/conf/default.format");
	public static final String logHistoryFilename = "/conf/logfile.history";
	public static final String cmdHistoryFilename = "/conf/command.history";
	
	private static File logHistoryFile=null;
	private static File cmdHistoryFile=null;
	
	public static final URL iconFileUrl = SystemUtil.class.getResource("/ezLogAnalyzer.jpg");
	
	public static File getCommandHistoryFile()
    {
		if (cmdHistoryFile==null)
		{
			try
			{
				URL fileUrl = SystemUtil.class.getResource(cmdHistoryFilename);
				cmdHistoryFile = new File(fileUrl.toURI());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	    return cmdHistoryFile;
    }

	public static File getDefaultExportOutFolder()
    {
		File file=null;
		try 
		{
			file = new File(exportFolderUrl.toURI());
			
			if (file.exists())
				return file;
		} 
		catch (Exception e) 
		{
			logger.error("getDefaultFilterFile", e);
		}
		
		return file;
    }

	public static File getDefaultFilterFile() 
	{
		File file=null;
		try 
		{
			file = new File(filterFileUrl.toURI());
			
			if (file.exists())
				return file;
		} 
		catch (Exception e) 
		{
			logger.error("getDefaultFilterFile", e);
		}
		
		return file;
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

	public static File getFormatFile()
    {
		File file=null;
		try 
		{
			if (formatFileUrl!=null)
			{
				file = new File(formatFileUrl.toURI());
			
				if (file.exists())
					return file;
			}
		} 
		catch (Exception e) 
		{
			logger.debug("getDefaultFilterFile", e.getMessage());
		}
		
		return file;
	    
    }


	public static Image getIcon()
    {
	    
		Image icon = null;
		
		try
		{
			icon = new ImageIcon(SystemUtil.iconFileUrl).getImage();
		}
		catch(Exception e)
		{
			
		}
	    return icon;
    }
	
	public static String getLastLogFilename()
	{
		List<String> result=null;
		
		if (SystemUtil.getLogHistoryFile()!=null)
			result = FileUtil.readFile(SystemUtil.getLogHistoryFile());
		
		if (result!=null && result.size()>0)
			return result.get(result.size()-1);
		
		return null;

	}	

	private static File getLogHistoryFile()
    {
		if (logHistoryFile==null)
		{
			try
			{
				URL fileUrl = SystemUtil.class.getResource(logHistoryFilename);
				logHistoryFile = new File(fileUrl.toURI());
			}
			catch(Exception e)
			{
				try
                {
					logHistoryFile = new File(confFolderUrl.getPath()+File.separator+"default.cache");
                }
                catch (Exception e1){ }
			}
		}
		
	    return logHistoryFile;
    }
	
	
	public static void setLastLogFilename(File file)
	{
		
		if (SystemUtil.getLogHistoryFile()!=null)
			FileUtil.writeFile(SystemUtil.getLogHistoryFile(), file.getAbsolutePath(), false);

	}

	public static boolean isDefaultFormatExists()
    {
		try
		{
			File file = getFormatFile();
			
			return file.isFile() && file.exists();
		}
		catch(Exception e)
		{
			
		}
		
		return false;
    }

	public static File getConfFolder()
    {
		File file=null;
		try 
		{
			file = new File(confFolderUrl.toURI());
			
			if (file.exists())
				return file;
		} 
		catch (Exception e) 
		{
			logger.error("getConfFolder", e);
		}
		
		return file;
    }	

	 
}
