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

import org.ez.log.om.Filter;
import org.ez.log.om.LogLine;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;
import org.ez.log.view.MainView.ViewName;

public class ErrorViewController extends  ViewController
{
	private static final ConsoleLogger<ErrorViewController> logger = ConsoleLogger.create(ErrorViewController.class);

	
	public ErrorViewController()
	{
		startViewUpdateThread();
		//registerForNewLog();
	}
	
	
/*	public void registerForNewLog()
    {
	    EventManager.getInstance().addListner(EventType.NEW_LOG_LINE, new EventListener()
		{
			@Override
			public void onEvent(Event event)
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
    }*/
	

	@Override
	public void clearView()
	{
		pauseWriting=true;
		view.setDocument(new PlainDocument());
		viewLineCount=0;
		
		MainView.getInstance().setViewTitle(ViewName.ERROR_VIEW, ViewName.ERROR_VIEW.label+" ("+viewLineCount+")");
		
		pauseWriting=false;
	}
	
	
	@Override
	public JTextArea getView()
	{
		return (JTextArea)view;
	}

	@Override
    public void insert(LogLine line)
    {
		//if (line!=null && line.containsError())
		{
			document.add(line);		
			display(line);
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
	protected void startViewUpdateThread() 
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

						MainView.getInstance().setViewTitle(ViewName.ERROR_VIEW, ViewName.ERROR_VIEW.label+" ("+viewLineCount+")");

					}
					catch(Exception e)
					{
						logger.error("viewUpdateThread", e);
					}
				}
			}
		});
		
		viewUpdateThread.setName("Error_view_updater");
		viewUpdateThread.setDaemon(true);
		viewUpdateThread.start();
		
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

	@Override
    public void setFilter(Filter filter)
    {
		//ignore for now.
    }
	
}
