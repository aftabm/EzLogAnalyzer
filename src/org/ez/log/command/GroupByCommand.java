package org.ez.log.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ez.log.controller.ViewController.ControllerState;
import org.ez.log.om.CommandType;
import org.ez.log.om.LogLine;

public class GroupByCommand extends Command
{

	protected GroupByCommand()
    {
	    super(CommandType.groupby);
    }

	@Override
    public boolean execute()
    {
	    
	    List<LogLine> data  = inputController.getDocument();
	    
		if(data==null || data.size()==0)
		{
			logger.info("execute", "No data to operate on. Ignoring command");
			return false;
		}
		
		List<String> threadNames = new ArrayList<String>();
		
	    for (LogLine line: data)
	    {
	    	String threadName = line.getThreadName();
	    	
	    	if(threadName!=null && !threadName.isEmpty())
	    	{
		    	if (!threadNames.contains(threadName))
		    	{
		    		threadNames.add(threadName);
		    	}
	    	}
	    }	    

	    outputController.reset();
	    outputController.setState(ControllerState.PAUSE_READING);
	    
/*	    outputController.insert(LogLine.parse("+-------------------------------------------------------------------------------------"));
	    outputController.insert(LogLine.parse("   groupby(thread)"));
	    outputController.insert(LogLine.parse("-------------------------------------------------------------------------------------+"));
*/	    
	    if(threadNames.size()>0)
	    {
		    Collections.sort(threadNames);
		    
		    for (String threadName: threadNames)
		    {
		    	List<LogLine> tempLines  = new ArrayList<LogLine>(data.size());
		    	
			    for (LogLine logLine :data)
			    {
			    	if(logLine!=null && threadName.equals(logLine.getThreadName()))
			    	{
			    		outputController.insert(logLine);
			    	}
			    	else
			    	{
			    		tempLines.add(logLine);
			    	}
			    }
			    
			    data = tempLines;
		    }
	    }
	    else
	    {
			logger.info("onGroupByCmd","Ignoring command. No applicable data found.");
	    }

		outputController.setState(ControllerState.RESUME_READING);
    	outputController.showView();
    	return true;
    }

}
