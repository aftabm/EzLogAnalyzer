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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ez.log.om.LogLine;
import org.ez.log.parser.LogLineParser;

public class LogFileReader 
{
	private static final ConsoleLogger<LogFileReader> logger = ConsoleLogger.create(LogFileReader.class);
	private DataInputStream inputStream=null;
	private BufferedReader reader=null;
	private boolean disposed=false;
	private int lineNumber=1;
	private File logFile=null;
	private LogLineParser parser;
	
	
	public static LogFileReader creatInstance(File formatFile) throws FileNotFoundException
	{
		if (formatFile==null)
			throw new IllegalArgumentException("Format file is null");
		
		if (formatFile.exists() && formatFile.isFile())
		{
			LogFileReader reader = new LogFileReader();
			reader.parser = new LogLineParser(formatFile);
			return reader;
		}
		
		throw new FileNotFoundException(formatFile.toString());
	}
	
	private LogFileReader()
	{
		
	}
	
	public String getFilePath()
	{
		return logFile.getAbsolutePath();
	}
	
	public void open(String filename) throws FileNotFoundException
	{
		File file = new File(filename);
		
		if (file.exists() && file.isFile())
			open(file);
	}
	
	public void dispose()
	{
		if (reader!=null)
		{
			try 
			{
				reader.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			reader=null;
		}
		
		if (inputStream!=null)
		{
			try 
			{
				inputStream.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = null;
		}
		
		disposed=true;
	}
	
	
	@Override
	protected void finalize() throws Throwable 
	{
		super.finalize();
		dispose();
	}
	
	public LogFileReader open(File file) throws FileNotFoundException
	{
		inputStream = new DataInputStream(new FileInputStream(file));
		reader = new BufferedReader(new InputStreamReader(inputStream));
		this.logFile = file;
		disposed=false;
		return this;
	}
	
	public String readAll(boolean includeLineNumber)
	{
		StringBuilder sb = new StringBuilder();
		
		String line=null;
		do 
		{
			line = readLineAsString();
				
			if (line!=null)
			{
				if (includeLineNumber)
					sb.append("[").append(lineNumber++).append("]").append("\t");
				
				sb.append(line).append(System.lineSeparator());
			}
				
		}while (line!=null);
		
		return sb.toString();
	}
	
	public List<String> readAllAsList()
	{
		List<String> lines = new ArrayList<String>();
		
		String line=null;
		do 
		{
			line = readLineAsString();
				
			if (line!=null)
				lines.add(line);
				
		}while (line!=null);
		
		return lines;
	}
	
	public LogLine readLine()
	{
	   if (disposed) throw new java.lang.IllegalStateException("Reader is disposed.");
		
		try 
		{
			String line = reader.readLine();
			//logger.logDebug("readLine","FROM-FILE: "+line);
			
			if (line!=null)
			{
				return parser.parse(line);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	
	
	public String readLineAsString()
	{
		if (disposed) throw new java.lang.IllegalStateException("Reader is disposed.");
		
		try 
		{
			String line = reader.readLine(); 
			return line;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public List<LogLine> readLines(int maxLines)
	{
		List<LogLine> logLines = new ArrayList<LogLine>();
		LogLine line;
		
		do 
		{
			line = readLine();
				
			if (line!=null)
			{
				logLines.add(line);
			}
				
		}while (line!=null && logLines.size() < maxLines);
		
		return logLines;
	}
	
	
	public String readLinesAsString(int maxLines, boolean includeLineNumber)
	{
		StringBuilder sb = new StringBuilder();
		int counter=0;

		String line;
		
		do 
		{
			line = readLineAsString();
				
			if (line!=null)
			{
				if (includeLineNumber)
					sb.append("[").append(lineNumber++).append("]").append("\t");
				
				sb.append(line).append(System.lineSeparator());				
			}
			
			counter++;
				
		}while (line!=null && counter < maxLines);
		
		return sb.toString();
	}
	
	public List<String> readRawLines(int maxLines)
	{
		String line;
		List<String> logLines = new ArrayList<String>();
		
		do 
		{
			line = readLineAsString();
				
			if (line!=null)
			{
				logLines.add(line);
			}
				
		}while (line!=null && logLines.size() < maxLines);
		
		return logLines;
	}
	
}
