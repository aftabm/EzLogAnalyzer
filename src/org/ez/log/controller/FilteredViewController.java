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
package org.ez.log.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventListener;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.Filter;
import org.ez.log.om.LogLine;
import org.ez.log.util.ComplianceManager;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;

public class FilteredViewController extends  ViewController
{
	private static final ConsoleLogger<FilteredViewController> logger = ConsoleLogger.create(FilteredViewController.class);
	private ComplianceManager complianceManager = ComplianceManager.create(new Filter("Dummy"));
	
	
	public void setFilter(Filter filter)
	{
		complianceManager = ComplianceManager.create(filter);
		MainView.getInstance().setToolTip(getView().getName(), filter.toString());
		display(new LogLine("+-------------------------------------------------------------------------------------"));
		display(new LogLine("+" +filter.toString()));
		display(new LogLine("-------------------------------------------------------------------------------------+"));
	}
	
	public FilteredViewController()
	{
		startViewUpdateThread();
	}
	
	public void registerForNewLog()
    {
	    EventManager.getInstance().addListner(EventType.NEW_LOG_LINE, new EventListener()
		{
			@Override
			public synchronized void onEvent(Event event)
			{
				while(pauseReading)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						logger.warn("insert", e.getMessage());
					}
				}
				
				LogLine line =event.getData(EventDataKey.LOG_LINE, LogLine.class);
				insert(line);
			}
		});
    }


/*	public void append(LogLine line) 
	{
		    document.add(line);
			display(line);
	}*/
	
	
	public void clearView()
	{
		pause();
		this.displayQueue.clear();
		this.view.getHighlighter().removeAllHighlights();
		this.view.setDocument(new PlainDocument());
		this.viewLineCount=0;
		resume();
		
		MainView.getInstance().setViewTitle(view.getName(), view.getName()+" ("+viewLineCount+")");		
	}

	@Override
	public JTextArea getView()
	{
	    return (JTextArea)view;
	}
	
	@Override
    public void insert(LogLine line)
    {
		if(complianceManager==null)
			return;
				
		if (complianceManager.isCompliant(line) || complianceManager.isChildCompliant(line))
		{
			if (!document.contains(line))
			{
				document.add(line);
				display(line);
			}
		}
	    
    }
	
	@Override
	protected void onMouseClicked(MouseEvent e) 
	{
		JTextComponent jText = (JTextComponent) e.getSource();
	    Point pt = new Point(e.getX(), e.getY());
	    int pos = jText.viewToModel(pt);
	    view.setCaretPosition(pos);
		
		Action selectLineAction = view.getActionMap().get(DefaultEditorKit.selectLineAction);
		selectLineAction.actionPerformed(null);
		String selectedLine = view.getSelectedText();
		ControllerFactory.getStatusViewController().display(selectedLine, false);
		
		if (e.getClickCount() == 2) 
		{
			ControllerFactory.getLogViewController().onHighLiteCmd(selectedLine.trim());
		}
		
		e.consume();
	}



	
	@Override
    void startViewUpdateThread()
	{
		displayQueue = new LinkedBlockingQueue<LogLine>(bufferSize);
		
		Thread viewUpdateThread = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				while (true)
				{
					try 
					{
						if (pauseWriting)
						{
							Thread.sleep(1000);
							continue;
						}
						
						LogLine line = displayQueue.take();
						
						StringBuilder sb = new StringBuilder();
						sb.append(line.getDecoratedText());
						
						viewLineCount++;
						
						if(!fold)
						{
							for(LogLine child: line.getChildren())
							{
								sb.append(child.getDecoratedText());
								viewLineCount++;
							}
						}
						
						getView().append(sb.toString());
						
						if(autoScroll && displayQueue.size()==0)
						{
							view.setCaretPosition(view.getDocument().getLength());
						}
						
						MainView.getInstance().setViewTitle(view.getName(), view.getName()+" ("+viewLineCount+")");
					}
					catch(Exception e)
					{
						logger.error("viewUpdateThread", e);
					}
				}
			}
		});
		
		viewUpdateThread.setName("filtered_view_updater");
		viewUpdateThread.setDaemon(true);
		viewUpdateThread.start();
	}

	public void setToolTip(String toolTipText)
    {
		MainView.getInstance().setToolTip(view.getName(), toolTipText);
    }

	@Override
    void onFoldClicked()
    {
		clearView();
		
		setState(ControllerState.PAUSE);
		
		for(LogLine line : document)
		{
			display(line);
		}
		
		setState(ControllerState.RESUME);		
    }


}
