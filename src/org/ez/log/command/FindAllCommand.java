package org.ez.log.command;

import java.awt.Color;
import java.lang.reflect.Field;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.ez.log.controller.ControllerFactory;
import org.ez.log.om.CommandType;
import org.ez.log.util.StyleManager;

public class FindAllCommand extends Command
{
	protected FindAllCommand()
    {
	    super(CommandType.findall);
    }

	@Override
    public boolean execute()
    {
		if(!isValidState())
		{
			ControllerFactory.getStatusViewController().display("Unable to process command. Command state is not valid.", false);
			return false;
		}
		
		String[] keywords = new String[]{arguments[0]};
		
		
		Color highlightColor = getColor(arguments[1]);
				
		if (highlightColor==null)
		{
			outputController.removeHighlights(StyleManager.getInstance().getFindHighlightPainter());
			outputController.highlight(keywords, StyleManager.getInstance().getFindHighlightPainter());
		}
		else
		{
			HighlightPainter painter =new DefaultHighlighter.DefaultHighlightPainter(highlightColor);
			outputController.highlight(keywords, painter);
		}
		
		outputController.showView();
	    return true;
    }

	@Override
	protected boolean isValidState()
	{
		return arguments!=null &&arguments.length>0 &&  outputController!=null;
	}
	
	
	protected Color getColor(String name)
	{
		Color out=null;

		try
        {
			if(name!=null && !name.isEmpty())
			{
				Field temp = Color.class.getField(name.trim());
        	
				if(temp!=null)
					out = (Color)temp.get(null);
			}
	        
        }
        catch (Exception e)
        {
	        logger.error("getColor", e);
        }

		
		return out;
	}
}
