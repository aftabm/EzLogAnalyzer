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
package org.ez.log.om;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.text.AttributeSet;

import org.ez.log.parser.LogLineParser;
import org.ez.log.parser.LogLineParser.TokenName;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.util.DateUtil;
import org.ez.log.util.StyleManager;


public class LogLine implements Comparable<LogLine>
{
	//2012-09-06 18:54:53,744
	/*public static final String DATE_FORMAT =  "yyyy-MM-dd HH:mm:ss,SSS";*/
	private boolean compliant=false;

	//example 2012-09-06 18:54:53,744 [task:executor#414-5] DEBUG org.ez.log.MainApp  - this is a test
	private static final ConsoleLogger<LogLine> logger = ConsoleLogger.create(LogLine.class);
/*	public static LogLine parse(String line)
	{
		if (line==null)
				return null;
		
		LogLine logLine = new LogLine(line);
		
		try 
		{
			logLine.parse();
		} 
		catch (Exception e) 
		{
			logger.error("parse", e);
		}
		
		return logLine;
	}*/
	private boolean hasTokens=false;
	private String rawText=null;
	private String decoratedText="";
	private String lowerCaseRaw=null;
	private Date  date;
	private String threadName=null;
	private String className=null;
	private String level=null;
	private String message=null;
	private List<LogLine> children = new ArrayList<LogLine>();
	private AttributeSet attributes = StyleManager.getInstance().getDefaultParagraphAttribute();
	private int lineNumber=-1;
	private int insertedAt;
	private String dateFormat;
	
	public LogLine(String line)
	{	
		this.rawText = line;
		
		if (line!=null && !line.trim().isEmpty())
		{
			this.lowerCaseRaw = line.toLowerCase();
			setDecoratedText(line);
		}
	}
	
	public void addChild(LogLine line)
	{
		this.children.add(line);
	}
	@Override
    public int compareTo(LogLine that)
    {
		if (this.hasTokens && that.hasTokens)
			return this.date.compareTo(that.getDate());
		
		throw new java.lang.IllegalStateException("Unable to process compareTo.One of Log line is not parsable. LINE1: "+this.rawText+" LINE2: "+that.rawText);
			
    }
	
	
	
	public boolean contains(String text) 
	{
		return this.rawText.contains(text.trim());
	}
	
	public boolean containsError() 
	{
		if (this.level!=null)
			return "ERROR".equalsIgnoreCase(this.level) || "SEVERE".equalsIgnoreCase(this.level) || "FATAL".equalsIgnoreCase(this.level);
		else
		{
		    return this.lowerCaseRaw.contains(" error ")||this.lowerCaseRaw.contains(" severe ") ||this.lowerCaseRaw.contains(" fatal ");
		}
	}
	
	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    LogLine other = (LogLine) obj;
	    if (rawText == null)
	    {
		    if (other.rawText != null)
			    return false;
	    }
	    else
		    if (!rawText.equals(other.rawText))
			    return false;
	    return true;
    }

	public List<LogLine> getChildren()
	{
		return children;
	}
	
	
	public String getClassName() 
	{
		return className;
	}
	
	public Date getDate() 
	{
		return date;
	}
	public String getDateAsString() 
	{
		if (this.date!=null)
			return DateUtil.dateToString(date, getDateFormat());
		else
			return "";
		
	}

	public String getDecoratedText()
	{
		
		if (decoratedText==null || decoratedText.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			
			if (lineNumber>0)
			{
				sb.append("[").append(Integer.toString(lineNumber)).append("]").append(" ");
			}
			
			sb.append(this.rawText);
			
			/*if (insertedAt>0)
			{
				sb.append(" ").append("{").append(Integer.toString(insertedAt)).append("}");
			}*/
			
			sb.append(System.lineSeparator());
			
			this.decoratedText=sb.toString();
		}
		
		return this.decoratedText;
	}

	public AttributeSet getDisplayAttributes()
    {
	    return this.attributes;
    }

	public String getLevel() 
	{
		return level;
	}

	public String getMessage() 
	{
		if (hasTokens)
			return this.message;
		
		return null;

	}

	public String getRawText()
	{
		return this.rawText;
	}

	public String getThreadName() 
	{
		return threadName;
	}

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((rawText == null) ? 0 : rawText.hashCode());
	    return result;
    }
	
	public boolean hasLineNumber() 
	{
		return this.lineNumber>0;
		/*if (this.decoratedText==null || this.decoratedText.isEmpty())
			return false;
		
		Pattern pattren= Pattern.compile("^\\[([0-9]+)\\]");
		Matcher  m = pattren.matcher(this.decoratedText);
		return m.find();*/
	}

	public boolean hasSameMessge(LogLine that) 
	{
		if (that==null)
			return false;
		
		if (this.getMessage()!=null && that.getMessage()!=null)
			return this.getMessage().equals(that.getMessage());
		
		return false;
		
	}

	public boolean hasTokens()
	{
		return this.hasTokens;
	}

	public boolean isCompliant()
	{
		return this.compliant;
	}
	
	public boolean isEmpty() 
	{
		return (rawText==null || rawText.trim().isEmpty());
	}

	public boolean isMyChild(LogLine line)
	{
		if(line==null || line.hasTokens() || !this.hasTokens)
			return false;
		
		else return true;
	}

	public boolean isSameThread(LogLine that)
	{
		if (that==null)
			return false;
		
		if (this.getThreadName()!=null && that.getThreadName()!=null)
			return this.getThreadName().equals(that.getThreadName());
		
		return false;
	}

/*	private void parse()
    {
		//logger.logDebug("parse", "IN>>>: "+this.raw);
		
		Map<LogLineParser.TokenName, String> tokens = LogLineParser.getInstance().parse(this.lowerCaseRaw);
		
		if (tokens==null || tokens.size()==0)
		{
			this.hasTokens=false;
			return;
		}
	    
	    this.hasTokens=true;
	    this.date = DateUtil.stringToDate(tokens.get(TokenName.date), LogLineParser.getInstance().getDateTimeFormat());
	    this.threadName = tokens.get(TokenName.thread);
	    this.level = tokens.get(TokenName.level);
	    this.className=tokens.get(TokenName.class_name);
	    this.message= tokens.get(TokenName.message);
	    
	    //logger.logDebug("parse", "<<<OUT: "+this.toString());
    }*/

	public void removeChild(LogLine child)
    {
	    this.children.remove(child);
    }

	public void setCompliant(boolean value) 
	{
		this.compliant=value;
	}


	public void setDecoratedText(String line)
	{
		String lineSeparator="";
		
		if (!line.endsWith(System.lineSeparator()))			
			lineSeparator = System.lineSeparator();
		
		this.decoratedText=line+lineSeparator;
	}

	public void setDisplayAttributes(AttributeSet attributes)
    {
	    this.attributes = attributes;
    }
	
	public void setLineNumber(int lineNumber)
    {
		this.decoratedText=null;
	    this.lineNumber = lineNumber;
    }

	@Override
    public String toString()
    {
	    return this.decoratedText+", compliant=" + compliant + ", hasTokens=" + hasTokens + "]";
    }

	public void setPostion(int insertedAt)
    {
		//this.decoratedText=null;
	    this.insertedAt=insertedAt;
    }

	public boolean hasChild()
    {
	    return this.children==null? false: this.children.size() > 0;
    }
	
	public String getDateFormat()
	{
		return this.dateFormat;
	}

	public void setTokens(Map<TokenName, String> tokens)
    {
		if (tokens!=null && tokens.size()>0)
		{
			this.hasTokens=true;
			this.date =DateUtil.stringToDate(tokens.get(TokenName.date), tokens.get(TokenName.date_format)) ;
			this.threadName = (String) tokens.get(TokenName.thread);
			this.level = (String) tokens.get(TokenName.level);
			this.className=(String) tokens.get(TokenName.class_name);
			this.message= (String) tokens.get(TokenName.message);
			this.dateFormat = tokens.get(TokenName.date_format);
		}
    }	

}
