package org.ez.log.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ez.log.controller.ViewController.ControllerState;
import org.ez.log.om.CommandType;
import org.ez.log.om.LogLine;

public class RecentOnlyCommand extends Command
{

	protected RecentOnlyCommand()
    {
	    super(CommandType.recentonly);
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

		Map<String, LogLine> msgMap = new HashMap<String, LogLine>();
		
		for(LogLine line: data)
		{
			if (line.hasTokens())
				msgMap.put(line.getMessage(), line);
		}
		
		outputController.reset();
		outputController.setState(ControllerState.PAUSE_READING);
		
/*	    outputController.insert(LogLine.parse("+-------------------------------------------------------------------------------------"));
	    outputController.insert(LogLine.parse("   recentonly(message)"));
	    outputController.insert(LogLine.parse("-------------------------------------------------------------------------------------+"));*/
		
		
		if(msgMap.size() > 0)
		{
			List<LogLine> out = new ArrayList<LogLine>(msgMap.values()); 
			
			Collections.sort(out);
			

			
		    for(LogLine line: out)
		    {
		        outputController.insert(line);
		    }		
		    
		}
		else
		{
			logger.info("onMostRecentOnlyCmd","Ignoring command. No applicable data found.");
		}

		
	    outputController.setState(ControllerState.RESUME_READING);
		outputController.showView();
		
		return true;
	}

}
