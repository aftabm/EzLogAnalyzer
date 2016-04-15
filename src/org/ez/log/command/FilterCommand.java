package org.ez.log.command;

import java.util.List;

import org.ez.log.controller.FilteredViewController;
import org.ez.log.om.CommandType;
import org.ez.log.om.Filter;
import org.ez.log.om.FilterType;
import org.ez.log.om.LogLine;
import org.ez.log.parser.FilterValueParser;
import org.ez.log.util.StyleManager;

public class FilterCommand extends Command
{
	protected FilterCommand()
    {
	    super(CommandType.filter);
    }

	@Override
	public boolean execute()
	{
		if(!isValidState())
		{
			logger.error("execute", "Invalid state");
			return false;
		}
		
	    List<LogLine> data  = inputController.getDocument();
	    
		if(data==null || data.size()==0)
		{
			logger.info("execute", "No data to operate on. Ignoring command");
			return false;
		}
		
		outputController.reset();
		
		Filter filter = new Filter("keyword");
		filter.set(FilterType.Keyword, arguments[0]);
		
		outputController.setFilter(filter);
		
		for (LogLine line : data)
		{
			outputController.insert(line);
		}

		outputController.showView();
		
		String filterValue = filter.get(FilterType.Keyword);
		String[] keywords = FilterValueParser.getStatementsWithoutOperator(filterValue);
		outputController.highlight(keywords, StyleManager.getInstance().getFilterHighlightPainter());
		
		((FilteredViewController)outputController).registerForNewLog();
		
		return true;
	}
	
	@Override
	protected boolean isValidState()
	{
	    return (outputController instanceof FilteredViewController) && super.isValidState();
	}

}
