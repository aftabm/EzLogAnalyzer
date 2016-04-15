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
package org.ez.log.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.om.CommandSubType;
import org.ez.log.om.CommandType;
import org.ez.log.util.ConsoleLogger;

public class CommandFactory 
{
	private CommandType type;
	
	//private String[] tokens = null;
	private CommandSubType subType=null;
	private String[] arguments = new String[]{"","","", "", ""};
	private static final ConsoleLogger<CommandFactory> logger = ConsoleLogger.create(CommandFactory.class);
	
	private static final String cmdExpresion = "(^[a-z]+)\\(([^,]+),?([^,]*)?\\)";
	private static final Pattern cmdPattren= Pattern.compile(cmdExpresion, Pattern.CASE_INSENSITIVE);
	
	private static Command createCommand(CommandType type)
    {
		switch(type)
		{
			case help:
				return new HelpCommand();
				
			case filter:
				return new FilterCommand();
				
			case findall:
				return new FindAllCommand();
				
			case groupby:
				return new GroupByCommand();
				
			case recentonly:
				return new RecentOnlyCommand();
				
			case remove:
				return new RemoveCommand();
				
			case replace:
				return new ReplaceCommand();
				
			case find:
				return new FindCommand();
				
			default:
		}
		
		return null;
    }	
	
	private static String[] getTokens(String statement) 
	{
					
		if (statement.startsWith(CommandType.help.name()))
		{
			return new String[]{"",CommandType.help.name(),"",""};
		}
		
		if (statement.startsWith(CommandType.recentonly.name()))
		{
			return new String[]{"",CommandType.recentonly.name(),"",""};
		}
		
		if (statement.startsWith(CommandType.groupby.name()))
		{
			return new String[]{"",CommandType.groupby.name(),"",""};
		}
		
		if(!startsWithCommand(statement))
			statement = CommandType.filter.name()+"("+statement+")";
		
		
		Matcher m = cmdPattren.matcher(statement.trim());

		String[] out=null ;
		
		if(m.find())
		{
			out = new String[m.groupCount()+1];
			
			for (int i=0;i<=m.groupCount();i++)
			{
				out[i]=m.group(i); 
			}
		}
			
		
		return  out;
	}
	
	private static boolean startsWithCommand(String statement)
    {

		for (CommandType type: CommandType.values())
		{
			if(statement.startsWith(type.name()))
				return true;
		}
		
		return false;
    }

	public static Command parse(String expression) throws IllegalArgumentException
	{
		//Command cmd= new CommandFactory();
		CommandType type;
		int CMD_NAME=1;
		int CMD_ARG1=2;
		int CMD_ARG2=3;
		String[] arguments = new String[2];

		String[] tokens = getTokens(expression.trim());
		
		if (tokens!=null && tokens.length>0)
		{
			type= CommandType.valueOf(tokens[CMD_NAME]);
			
			if (type==null)
			{
				logger.error("parse", "Unknown command: "+expression);
				throw new java.lang.IllegalArgumentException("Unknown command: "+expression);
			}

			if (tokens.length>CMD_ARG1)
				arguments[0] = tokens[CMD_ARG1];
			
			if (tokens.length>CMD_ARG2)
				arguments[1] = tokens[CMD_ARG2];
		
			
			Command cmd = createCommand(type);
			cmd.setArguments(arguments);
			
			return cmd;
		}
		
		
		logger.error("parse", "Unable to parse command: "+expression);
		throw new java.lang.IllegalArgumentException("Unable to parse command: "+expression);
		
	}

	public String getArgument(int i) 
	{
		if (i <= arguments.length)
			return arguments[i-1];
		else
			return null;
	}
	
	public CommandSubType getSubType()
	{
		return subType;
	}
	

	public CommandType getType()
	{
		return type;
	}
	
}
