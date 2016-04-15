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
package org.ez.log.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.om.FilterOperator;

public class FilterValueParser 
{
	
	private static final String OPERAND_EXP="(.*)(\\{or\\}|\\{and\\}|\\{not\\})(.*)";
	private static final int LEFT_OPERAND_GROUP=1;
	private static final int OPERATOR_GROUP=2;
	private static final int RIGHT_OPERAND_GROUP=3;
	private static final String OPERATOR_EXP="(\\{or\\}|\\{and\\}|\\{not\\})";
	private static final String OPERATOR_NAME_EXP="(\\{)(and|or|not)(\\})";	
	private static int OPERATOR_NAME_GROUP=2;
	
	private static final String STATEMENT_EXP="[^;]*";
	
	private static final Pattern operandPattren= Pattern.compile(OPERAND_EXP);
	private static final Pattern operatorPattren= Pattern.compile(OPERATOR_EXP, Pattern.CASE_INSENSITIVE);
	private static final Pattern operatorNamePattren= Pattern.compile(OPERATOR_NAME_EXP, Pattern.CASE_INSENSITIVE);
	private static final Pattern statementPattren= Pattern.compile(STATEMENT_EXP);
	
	public static final String FILTER_NAME_TOKEN="filtername="; 
	public static final String FILTER_VALUE_TOKEN="filterValue=";
	
	
	public static boolean containsOperator(String data)
	{
		
		Matcher m = operatorPattren.matcher(data);
		return m.find();
	}
	
	public static String getFilterValue(String data)
	{
		
		if (data!=null && !data.trim().isEmpty())
		{
			String[] tokens=data.split("filterValue=");
			
			if (tokens.length!=2)
				throw new java.lang.IllegalStateException("Unable to parse filter data");
			
			return tokens[1].trim();
			
		}
		
		return null;
	}
	
	
	public static String getOperator(String data)
	{
		String out = null;
		Matcher m = operatorPattren.matcher(data);
		
		if(m.find())
		{
			out = m.group().trim();
		}
		
		return out;		
	}
	
	public static String getOperatorName(String data)
	{
		String out = null;
		Matcher m = operatorNamePattren.matcher(data);
		
		if(m.find())
		{
			out = m.group(OPERATOR_NAME_GROUP).toLowerCase().trim();
		}
		
		return out;
	}
	
	public static String[] getOprands(String statement, String operator) 
	{	
		String[] out = statement.split(operator);
		
		return out;
	}

	public static String[] getPreFixTokens(String statement) 
	{
		List<String> result = new ArrayList<String>();
		
/*		Matcher m = operandPattren.matcher(statement);
		
		while(m.find())
		{
			if (m.groupCount()==3)
			{
				String token = m.group(OPERATOR_GROUP);
				if (token!=null && !token.trim().isEmpty())
				{
					result.add(getOperatorName(token.trim()));
					
					token = m.group(LEFT_OPERAND_GROUP);
					if (token!=null && !token.trim().isEmpty())
						result.add(token.trim());
					
					token = m.group(RIGHT_OPERAND_GROUP);
					if (token!=null && !token.trim().isEmpty())
						result.add(token.trim());					
				}
			}
		}
*/		
		
		
//		if (result.size()==0)
		{
			String operator = getOperatorName(statement);
			
			if(operator!=null && !operator.isEmpty())
			{
				result.add(operator);
				String[] tokens = operatorPattren.split(statement);
			
				for (String token:tokens)
				{
					if (!token.isEmpty())
						result.add(token.trim());
				}
			}
		}
			
		if (result.size()==0)
		{			
			result.add(FilterOperator.NONE.toString());
			result.add(statement);
	    }
		
		return result.toArray(new String[result.size()]);
	}

	public static String[] getStatements(String data)
	{
		List<String> out = new ArrayList<String>();
		
		Matcher m = statementPattren.matcher(data);
		while(m.find())
		{
				String token = m.group();
				if (token!=null && !token.trim().isEmpty())
					out.add(token.trim());
		}
		
		if (out.size()==0)
		{
			out.add(Pattern.quote(data));
		}
			
		return out.toArray(new String[out.size()]);
	}
	
	
	public static String[] getStatementsWithoutOperator(String filterValue)
	{
		String[] statements = getStatements(filterValue);
		List<String> out = new ArrayList<String>();
		
		for (String statement: statements)
		{
			String[] tokens = getPreFixTokens(statement);
			
			for (int i =1; i<tokens.length;i++)
			{
				out.add(tokens[i]);
			}
		}
		
		return out.toArray(new String[out.size()]);
	}
	

}


/*	private static final String AND_EXP="\\{and\\}";
private static final String OR_EXP="\\{or\\}";
private static final String NOT_EXP="\\{not\\}";
private static final String COMMA_EXP="\\,s\\";*/

//private static final String TOKEN_EXP="(\\w*\\s\\{or\\}\\s\\w*)|(\\w*\\s\\{and\\}\\s\\w*)|(\\{not\\}\\s\\w*)";
//private static final String TOKEN_EXP="[\\w]+\\s(\\{and\\}|\\{or\\}|\\{not\\}|)\\s([\\w]+)";
//private static final Pattern[] patterns = new Pattern[]{Pattern.compile(AND_EXP),Pattern.compile(OR_EXP), Pattern.compile(NOT_EXP)};
//public static final Pattern COMMA_PATTERN = Pattern.compile(COMMA_EXP);
//private static final String OPERAND_EXP="((.*)(\\{or\\}|\\{and\\}|\\{not\\})(.*),)*((.*)(\\{or\\}|\\{and\\}|\\{not\\})(.*))"
//private static final String OPERATOR_NAME_EXP="[\\w*]+";;
