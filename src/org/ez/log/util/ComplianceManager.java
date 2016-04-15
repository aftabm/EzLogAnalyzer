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


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.om.Filter;
import org.ez.log.om.FilterOperator;
import org.ez.log.om.FilterType;
import org.ez.log.om.LogLine;
import org.ez.log.parser.FilterValueParser;

public class ComplianceManager 
{
	private ComplianceManager()
	{
		
	}
	
	public static ComplianceManager create(Filter filter)
	{
			ComplianceManager instance = new ComplianceManager();
			instance.filter = filter;
		
		return instance;
	}
	private Filter filter=null;
	
	
	private LogLine previousLine;
//	private LogLine lastCompliantLine;
	
	private WeakHashMap<String, Map<FilterOperator, List<String>>> filterCache = new WeakHashMap<String, Map<FilterOperator, List<String>>>();

	public Filter getFilter() 
	{
		return this.filter;
	}
	
	
	public boolean isChildCompliant(LogLine line)
	{
		if (line==null || !line.hasChild())
			return false;
		
		for(LogLine child: line.getChildren())
		{
			if (isCompliant(child))
				return true;
		}
		
		return false;
	}
	
	
	public boolean isCompliant(LogLine line)
	{
		
		if (filter==null || filter.isEmpty())
			return true;
		
		if (line==null || line.isEmpty())
			return false;
		
		boolean compliant = true; 
		
		if (line.hasTokens())
		{
			if(filter.get(FilterType.FromDate)!=null && !filter.get(FilterType.FromDate).isEmpty())
				compliant = compliant && fromDateFilterCompliant(filter.get(FilterType.FromDate), line);
			
			if(filter.get(FilterType.ToDate)!=null && !filter.get(FilterType.ToDate).isEmpty())
				compliant = compliant && toDateFilterCompliant(filter.get(FilterType.ToDate),line);
			
			if(filter.get(FilterType.ThreadName)!=null && !filter.get(FilterType.ThreadName).isEmpty())
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.ThreadName), line.getThreadName());
			
			if(filter.get(FilterType.LogLevel)!=null && !filter.get(FilterType.LogLevel).isEmpty())			
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.LogLevel), line.getLevel());
			
			if(filter.get(FilterType.ClassName)!=null && !filter.get(FilterType.ClassName).isEmpty())			
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.ClassName), line.getClassName());
			
			if(filter.get(FilterType.Message)!=null && !filter.get(FilterType.Message).isEmpty())			
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.Message), line.getMessage());
			
			if(filter.get(FilterType.Keyword)!=null && !filter.get(FilterType.Keyword).isEmpty())			
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.Keyword), line.getRawText());
		}
		else
		{
			if (filter.get(FilterType.Keyword)==null)
			{
				if (previousLine!=null)
					compliant = previousLine.isCompliant(); 
				else
					compliant = compliant && true;
			}
			else
			{
				compliant = compliant && isCompliantWithFilter(filter.get(FilterType.Keyword), line.getRawText());
			}
		}
		
		line.setCompliant(compliant);
		previousLine = line;
		
/*		if (compliant)
			lastCompliantLine=line;
*/		
		return compliant;
	}
	
	
	public boolean isCompliant(String line)
	{
		if (filter==null || filter.isEmpty())
			return false;
		
		if (line==null || line.isEmpty())
			return false;
		
		if (filter.get(FilterType.Keyword)==null)
			return false;
		
		boolean compliant = true; 
		compliant = isCompliantWithFilter(filter.get(FilterType.Keyword), line);
	
		return compliant;
	}
	
	private boolean isCompliantWithFilter(String filterValue, String data)
	{
		
		if (data==null || data.trim().isEmpty())
			return false;
		
		Map<FilterOperator, List<String>> literalMap=null;
		
		if (!filterCache.containsKey(filterValue))
		{
			String[] statements = FilterValueParser.getStatements(filterValue);
		
			literalMap = new HashMap<FilterOperator, List<String>>();
		
		    //converts this {and} that, foo {and} bar to "and, this, that, foo, bar".
			for (String statement: statements)
			{
				String[] result = FilterValueParser.getPreFixTokens(statement);
				updateLiteralMap(result, literalMap);
			}
			
			filterCache.put(filterValue, literalMap);
		}
		else
		{
			literalMap = filterCache.get(filterValue);
		}
		
		boolean result=true;
		//must must be true to select data.
		for(Entry<FilterOperator, List<String>> entry:literalMap.entrySet())
		{
			switch(entry.getKey())
			{
				case AND:
					result = result & matchWithAnd(entry.getValue(), data);//all keywords should exists in data.
					break;
				case NOT:
					result=  result & matchWithNot(entry.getValue(), data);//non of keywords should exists in data.
					break;
				case OR:
					result = result & matchWithOr(entry.getValue(), data);//any of keywords should exists
					break;
				case NONE:
					result= result & matchWithAnd(entry.getValue(), data);//like and, keywords without operator, regular expresion. 
			}
		}
		
		return result;
	}
	
	
	/*all of keywords should exists in data.*/
	private boolean matchWithAnd(List<String> filterValues, String data) 
	{
		boolean result=true;
		
		for(String filterValue: filterValues)
		{
			Matcher m = Pattern.compile(Pattern.quote(filterValue), Pattern.CASE_INSENSITIVE).matcher(Pattern.quote(data));
			result = result & m.find();
		}
		return result;		
	}
	
	
	/*non of keywords should exists in data.*/
	private boolean matchWithNot(List<String> filters, String data) 
	{
		boolean result=true;
		
		for(String expression: filters)
		{
			Matcher m = Pattern.compile(Pattern.quote(expression),Pattern.CASE_INSENSITIVE).matcher(Pattern.quote(data));
			result = result & !m.find();
		}
		return result;	
	}

	/*Any of keywords should exists in data.*/
	private boolean matchWithOr(List<String> filters, String data) 
	{
		boolean result=false;
		
		for(String expression: filters)
		{
			Matcher m = Pattern.compile(Pattern.quote(expression),Pattern.CASE_INSENSITIVE).matcher(Pattern.quote(data));
			result = result | m.find();
		}
		return result;	
	}
	
	
	
	private boolean fromDateFilterCompliant(String fromDate, LogLine logLine)
	{
		if(fromDate==null || fromDate.trim().isEmpty() )
			return true;		

		Date filterDate = DateUtil.stringToDate(fromDate, logLine.getDateFormat());

		//if filter is applicable		
		if (filterDate!=null)
		{
			if(logLine.getDate()==null )
				return false;
			
			if( ! ( filterDate.equals(logLine.getDate()) || filterDate.before(logLine.getDate()) ) )
					return false;
					
		}
		
		return true;
	}
	

	private boolean toDateFilterCompliant(String toDate,LogLine logLine)
	{
		if(toDate==null || toDate.trim().isEmpty() )
			return true;		
		
		// if filter is defined for this field and this field has data.		
		Date filterDate	 = DateUtil.stringToDate(toDate, logLine.getDateFormat());
			
		if (filterDate!=null)
		{
			if(logLine.getDate()==null)
				return false;
			
			if( ! ( filterDate.equals(logLine.getDate()) || filterDate.after(logLine.getDate()) ) )
				return false;
		}
 
		return true;
	}

	private void updateLiteralMap(final String[] result,
			Map<FilterOperator, List<String>> literalMap) 
	{
		
		FilterOperator operator = FilterOperator.fromString(result[0]);
		List<String> operands = literalMap.get(operator);
		
		if (operands==null)
		{
			operands = new ArrayList<String>();
			literalMap.put(operator, operands);
		}
		
		
		for (int i =1; i<result.length;i++)
		{
			operands.add(result[i]);
		}
	
	}	
	
	
}
