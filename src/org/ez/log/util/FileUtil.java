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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class FileUtil 
{
	public static final URL confFolderUrl = FileUtil.class.getResource("/conf");
	private static final ConsoleLogger<FileUtil> logger = ConsoleLogger.create(FileUtil.class);	
	
	public static void append(File file, String line)
    {
	 
		if (!line.contains(System.lineSeparator()))
			line = line+System.lineSeparator();
			
		writeFile(file, line, true);
    }
	
	

	public static File getFile(String filename) 
	{
		URL fileUrl = FileUtil.class.getResource("/conf/"+filename);
		File file=null;

		try 
		{
			file = new File(fileUrl.toURI());
			
			if (!file.exists())
			{
				file = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return file;
	}
	


	public static File[] getFiles(final String extension)
	{
		File[] files=null;
		File folder=null;
		try 
		{
			folder = new File(confFolderUrl.toURI());
			if(folder!=null && folder.exists() && folder.isDirectory())
			{
				files = folder.listFiles(new FileFilter() 
				{
					@Override
					public boolean accept(File pathname) 
					{
						if (pathname!=null && pathname.isFile() &&pathname.getName().contains(extension))
							return true;
						else
							return false;
					}
				});
			}
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
		
		return files;
	}



	public static List<String> readFile(File file)
	{
		if (file==null)
			throw new java.lang.IllegalArgumentException("Unable to read file. File object is null");
		
		
		DataInputStream inputStream=null;
		BufferedReader br=null;
		
		List<String> lines = new ArrayList<String>();
		
		try
		{
			   inputStream = new DataInputStream(new FileInputStream(file));
			   br = new BufferedReader(new InputStreamReader(inputStream));
			   
			   String line;
			   
			   do
			   {
				   line = br.readLine();
				   
				   if (line!=null && !line.trim().isEmpty())
					   lines.add(line);
			   }
			   while(line!=null);
			   
		}
		catch(Exception e)
		{
			logger.error("readFile", e);
		}
		finally
		{
			if(br!=null)
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
				}
			}
			if(inputStream!=null)
			{
				try 
				{
					inputStream.close();
				} 
				catch (IOException e) 
				{
				}
			}
		}
		
		return lines;
	}

	 
	 

	public static Properties readProperties(File file) 
	{
		Properties properties = new Properties();
		try 
		{
			properties.load(new FileInputStream(file));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		return properties;
	}



	public static void writeFile(File file, List<String> data, boolean append)
    {
		if (data==null || data.size()==0)
			return;
		
		try
        {
	        StringBuilder sb = new StringBuilder();
	        
	        for (String line: data)
	        {
	        	sb.append(line);
	        	
	        	if (!line.contains(System.lineSeparator()))
	        		sb.append(System.lineSeparator());
	        }
	        
	        writeFile(file, sb.toString(),append);
        }
        catch (Exception e)
        {
        	logger.error("writeFile", e);
        }
    }
	
	
	public static void writeFile(final File file, final String data, final boolean append)
    {
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				FileWriter fw=null;
				
				try
		        {
		            fw = new FileWriter(file, append);
		            fw.write(data);
		            		            
		            logger.info("writeFile", file.getAbsolutePath());
			        
		        }
		        catch (Exception e)
		        {
		        	logger.error("writeFile", e);
		        }
				finally
				{
					if(fw!=null)
					{
						try   {    fw.close();         }
		                catch (IOException e)     {      }
					}
				}
				
			}
		});
		
		thread.setName("writeFile");
		thread.setDaemon(true);
		thread.start();
	    
    }	


	public static void writeFile(String filename, String data, boolean append)
    {
		if (data==null || data.isEmpty())
			return;
		
		if (filename==null || filename.isEmpty())
			return;
		
        File file = new File(filename);
        writeFile(file, data, append);
    }



	public static void writeProperties(File file, Properties properties) 
	{
		OutputStream out=null;
		
		try
		{
			out = new FileOutputStream( file );
			properties.store(out, "");
		}
		catch(Exception e)
		{
			logger.error("writeProperties", e);
		}
		finally
		{
			if (out!=null)
			{
				try
                {
	                out.close();
                }
                catch (IOException e)
                {
                }
			}
		}
	}
}
