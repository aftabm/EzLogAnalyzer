package org.ez.log.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.controller.ViewController.ControllerState;
import org.ez.log.om.CommandType;
import org.ez.log.om.LogLine;

public class RemoveCommand extends Command
{

	protected RemoveCommand()
    {
	    super(CommandType.remove);
    }

	@Override
	public boolean execute()
	{
		if(!isValidState())
		{
			logger.error("execute", "Invalid state for RemoveCommand.");
			return false;
		}
		
	    List<LogLine> data  = inputController.getDocument();
	    
	    if (data!=null && data.size()>0)
	    {
	    	return doLogLineRemove(data);
	    }
	    
	    String text = inputController.getView().getText();
	    
	    if (text!=null && !text.isEmpty())
	    {
	    	return doTextRemove(text);
	    }
	    
		logger.info("execute", "No data to operate on. Ignoring command");
		return false;		
		

	}

	private boolean doTextRemove(String text)
    {
		Pattern pattern = Pattern.compile(arguments[0],Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		
		String out = matcher.replaceAll("");
		
		outputController.setState(ControllerState.PAUSE);
		outputController.getView().setText(out);
		outputController.setState(ControllerState.RESUME);
		
		return true;
    }

	private boolean doLogLineRemove(List<LogLine> data)
    {
		try
		{
			outputController.reset();
			
			for (LogLine line : data)
			{
				if (line.contains(arguments[0]))
					continue;
				
				for(LogLine child: line.getChildren())
				{
					if (line.contains(arguments[0]))
						line.removeChild(child);
				}
				
				outputController.insert(line);
			}
		}
		catch(Exception e)
		{
			return false;
		}
		finally
		{
			outputController.setState(ControllerState.RESUME);
		}

			
		outputController.showView();
		return true;		
    }

}
