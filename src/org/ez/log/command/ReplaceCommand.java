package org.ez.log.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ez.log.controller.ViewController.ControllerState;
import org.ez.log.om.CommandType;
import org.ez.log.om.LogLine;

public class ReplaceCommand extends Command
{

	protected ReplaceCommand()
    {
	    super(CommandType.replace);
    }

	@Override
    public boolean execute()
    {

	    List<LogLine> data  = inputController.getDocument();
	    
		if(data!=null && data.size()>0)
		{
			return doLogLineRepalce(data);
		}
		else
		{
			inputController.setState(ControllerState.PAUSE);
			String text = inputController.getView().getText();
			inputController.setState(ControllerState.RESUME);
			
			if(text!=null && !text.isEmpty())
			{
				return doTextReplace(text);
			}
		}
		
		logger.info("execute", "No data to operate on. Ignoring command");
		return false;
    }

	private boolean doTextReplace(String text)
    {
		try
		{
			Pattern pattern = Pattern.compile(arguments[0],Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			
			String out = matcher.replaceAll(arguments[1]);
			
			outputController.setState(ControllerState.PAUSE);
			outputController.getView().setText(out);
			outputController.setState(ControllerState.RESUME);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
    }

	private boolean doLogLineRepalce(List<LogLine> data)
    {
		outputController.reset();
		
		try
		{
			outputController.setState(ControllerState.PAUSE_READING);
			
			for (LogLine line : data)
			{
				if (line == null || line.isEmpty())
					continue;

				if (line.contains(arguments[0]))
				{
					line.setDecoratedText(line.getDecoratedText().replace(
							arguments[0], arguments[1]));
				}
				
				for(LogLine child: line.getChildren())
				{
					if (child.getDecoratedText().contains(arguments[0]))
					{
						child.setDecoratedText(child.getDecoratedText().replace(
								arguments[0], arguments[1]));
					}
				}

				outputController.insert(line);
			}
		}
		catch (Exception e)
		{
			logger.error("onReplaceCmd", e);
			return false;
		}
		finally
		{
			inputController.setState(ControllerState.RESUME);
			outputController.setState(ControllerState.RESUME);
		}
		
		outputController.showView();
		
		return true;
	    
    }

}
