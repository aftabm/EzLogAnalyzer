package org.ez.log.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.om.LogLine;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.util.FileUtil;
import org.ez.log.util.SystemUtil;

public class LogLineParser
{
	public static enum TokenName{date, thread, level, class_name, message, date_format, line_format}
	
	//example 2012-09-06 18:54:53,744 [task:executor#414-5] DEBUG org.ez.log.MainApp  - this is a test
	private static final ConsoleLogger<LogLineParser> logger = ConsoleLogger.create(LogLineParser.class);
	private static LogLineParser instance =null;
	private static String dateFormat=null;
	private static Pattern linePattren=null;	
	
	private static List<TokenName> tokens=null;
	
	/*
	 * format=date thread_name class_name message 
date=(^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}:\d{2}\,\d{3}) 
thread_name=(\[[^\s]+\])
level=([a-zA-Z]+)
class_name=([^\s]+)
message=\-\s([^\n]+$)
date_pattren=yyyy-MM-dd HH:mm:ss,SSS
	 * */
	
	
/*	public static LogLineParser getInstance() throws IllegalStateException
	{
		if (instance==null)
		{
			synchronized(LogLineParser.class)
			{
				instance = new LogLineParser();
			}
		}
		
		return instance;
	}*/
	
	
	
	public LogLineParser(File formatFile) 
	{
		Properties data= FileUtil.readProperties(formatFile);
		
		if (data==null || data.size()==0)
		{
			logger.error("LogLineParser", "Unable to read format file: "+formatFile.getAbsolutePath());
			return;
		}
		
		dateFormat = data.getProperty(TokenName.date_format.name());
		String lineFormat = data.getProperty(TokenName.line_format.name());
		
		if (lineFormat==null || lineFormat.isEmpty())
		{
			logger.error("LogLineParser", "Invalid format file. Unable to findall: "+TokenName.line_format.name());
			return;
		}
		
		String[] lineTokens = lineFormat.split("\\s");

		if (lineTokens==null || lineTokens.length==0)
		{
			logger.error("LogLineParser", "Invalid "+TokenName.line_format.name());
			return;
		}

		tokens = new ArrayList<TokenName>();
		
		for(String lineToken: lineTokens)
		{
			tokens.add(TokenName.valueOf(lineToken));
			String expression = data.getProperty(lineToken);
			lineFormat = lineFormat.replace(lineToken, expression);
		}
		
		linePattren = Pattern.compile(lineFormat.replace(" ", ""), Pattern.CASE_INSENSITIVE);
		
		logger.info("LogLineParser", "LogLine pattren:"+linePattren);
		
		return;
		
	}
	

	public String getDateTimeFormat()
	{
		return dateFormat;
	}
	
	public  LogLine parse(String line)
    {
		LogLine out = new LogLine(line);

		Map<TokenName, String> logTokens = null;;
			
		if (linePattren!=null && line!=null && !line.isEmpty())
		{
			Matcher m = linePattren.matcher(line.trim());
			
			if(m.find())
			{
				logTokens = new HashMap<TokenName, String>();
				
				for (int i=0; i< m.groupCount(); i++)
				{
					TokenName key = tokens.get(i);
					String value = m.group(i+1);
					logTokens.put(key, value);
				}
				
				logTokens.put(TokenName.date_format, getDateTimeFormat());
				
				out.setTokens(logTokens);
			}
			else
			{
				//logger.logWarn("parse", "Unable to parse: "+line);
			}
		}
		
	    return out;
    }

}
