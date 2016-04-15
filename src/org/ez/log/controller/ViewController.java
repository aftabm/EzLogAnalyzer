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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventListener;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.Filter;
import org.ez.log.om.LogLine;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.util.DefaultProperties;
import org.ez.log.util.DefaultProperties.PropertyName;
import org.ez.log.view.MainView;

public abstract class ViewController extends Controller
{
	public static enum ControllerState{PAUSE, PAUSE_READING, PAUSE_WRITING, RESUME, RESUME_READING, RESUME_WRITING, NORMAL}
	
	protected List<LogLine> document=null;
	private static final ConsoleLogger<ViewController> logger = ConsoleLogger.create(ViewController.class);
	protected LinkedBlockingQueue<LogLine> displayQueue = null;
	protected int totalLineCount = 0;
	protected int viewLineCount = 0;

	protected int bufferSize = 1000000;
	protected boolean pauseReading = false;
	protected boolean pauseWriting = false;
	protected boolean autoScroll = false;
	protected boolean wrap=false;
	protected int insertAt=0;
	protected int refreshRate=100;	
	protected boolean fold=true;
	protected List<EventListener> disposeListners = new ArrayList<EventListener>();
	
	protected JTextComponent view;
	private Stack<ControllerState> stateStack= null;

	abstract public void insert(LogLine line);
	abstract void onMouseClicked(MouseEvent e);
	abstract void onFoldClicked();
	abstract public void clearView();
	abstract public void setFilter(Filter filter);
		
	public final void showView()
	{
		 MainView.getInstance().showView(view.getName());
	}
	
	abstract void startViewUpdateThread();
	
	protected ViewController()
	{
		stateStack = new Stack<ControllerState>();
		stateStack.push(ControllerState.NORMAL);
		
		this.bufferSize = Integer.parseInt(DefaultProperties.get(PropertyName.buffer_size, "100000"));
		this.refreshRate = Integer.parseInt(DefaultProperties.get(PropertyName.refresh_rate, "100"));
		
		document = new ArrayList<LogLine>();
		displayQueue = new LinkedBlockingQueue<LogLine>(bufferSize);
		
		EventManager.getInstance().addListner(EventType.OPTION_SELECTED, new org.ez.log.event.EventListener()
		{
			@Override
			public void onEvent(Event event)
			{
				ControllerFactory.getStatusViewController().display("Setting "+event.getType().leafName+" = "+event.getData(EventDataKey.SELECTED, Boolean.class), false);
				
				switch(event.getType())
				{
					case AUTOSCROLL_CLICKED:
						autoScroll = event.getData(EventDataKey.SELECTED, Boolean.class);
						break;
						
					case PAUSE_CLICKED:
						pauseReading= event.getData(EventDataKey.SELECTED, Boolean.class);
						break;
						
					case WRAP_CLICKED:
						if (view instanceof JTextArea)
						{
							((JTextArea)view).setLineWrap(event.getData(EventDataKey.SELECTED, Boolean.class));
						}
						break;
						
					case FOLD_CLICKED:
						fold = event.getData(EventDataKey.SELECTED, Boolean.class);
						onFoldClicked();
						break;
						
					default:
						break;
				}
				
			}
		});
	}
	
	
	/*
	 * Appends to display queue
	 * */
	public void display(LogLine line)
	{
		if (line!=null)
		{
			try
            {
	            this.displayQueue.offer(line, 1000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
            	logger.error("insert", e);
            }
		}
		else
		{
			logger.warn("insert", "null was passed");
		}
	}
	
	@Override
	public void finalize()
	{
		pause();
		
		Event event = new Event(EventType.DISPOSED);
		event.setData(EventDataKey.DISPOSED, this);
		for(EventListener listner: disposeListners)
		{
			listner.onEvent(event);
		}
		
		disposeListners.clear();
		
		if (displayQueue != null)
		{
			this.displayQueue.clear();
		}
		
		if(document!=null)
		{
			this.document.clear();
		}
		
		if(view!=null)
		{
			this.view.setText("");
		}
	}
	
	
	public final List<LogLine> getDocument()
	{
		setState(ControllerState.PAUSE_READING);
		List<LogLine> out = new ArrayList<LogLine>(document);
		setState(ControllerState.RESUME_READING);
		
		return out; 
	}
	
	public JTextComponent getView()
	{
		return this.view;
	}
	
	public final boolean highlight(String[] keywords, HighlightPainter highlightPainter)
    {
		boolean result = false;
		
		if (keywords==null || keywords.length==0)
			return false;
		
		String data = getText();
		
		if(data==null || data.isEmpty())
		{
			return false;
		}
		
		
		setState(ControllerState.PAUSE_WRITING);
		removeHighlights(highlightPainter);

		StringBuilder sb = new StringBuilder();
		
		for (String keyword: keywords)
		{
			sb.append(Pattern.quote(keyword)).append("|");
		}
		
		sb.delete(sb.length()-1, sb.length());
		
		{ 
			Pattern pattern = Pattern.compile(sb.toString(),Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(data);

			while (matcher.find())
			{
				int start = matcher.start();
				int end = matcher.end();
	
				highlight(start, end, highlightPainter);
				
				result=true;
			}
		}

		setState(ControllerState.RESUME_WRITING);
		
		return result;
    }
	
	
	public final  void highlight(final int start, final int end, final HighlightPainter painter)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					view.getHighlighter().addHighlight(start, end, painter);
					view.setCaretPosition(end);
					
					if (!view.getCaret().isVisible())
						view.getCaret().setVisible(true);

				}
				catch (Exception e)
				{
					logger.error("highlight", e);
				}
				
			}
		});

	}
	
	//NOTE: This locking mechanism will fail with nested transaction
	protected void pause()
    {
		this.pauseWriting=true;
		this.pauseReading=true;
    }
	protected void pauseReading()
    {
		this.pauseReading=true;
    }
	protected void pauseWriting()
    {
		this.pauseWriting=true;
    }
	
	public final void removeHighlights(HighlightPainter painter)
	{
		Highlighter highliter = view.getHighlighter();
		Highlight[] highlights = highliter.getHighlights();

		if (highlights == null || highlights.length == 0)
			return;

		for (Highlight highlite : highlights)
		{
			if (highlite.getPainter().equals(painter))
				highliter.removeHighlight(highlite);
		}
	}
	public void reset()
	{
		pause();
		
		this.displayQueue.clear();
		this.document.clear();
		this.totalLineCount=0;
		this.view.getHighlighter().removeAllHighlights();
		this.view.setText("");

		clearView();
		
		resume();
	}
	protected void resume()
    {
		this.pauseWriting=false;
		this.pauseReading=false;
    }
	
	protected void resumeReading()
    {
		this.pauseReading=false;
    }

	
	
	protected void resumeWriting()
    {
		this.pauseWriting=false;
    }
	
	public void setState(ControllerState newState)
	{
		switch(newState)
		{
		case PAUSE:
			pause();
			break;
		case PAUSE_READING:
			pauseReading();
			break;
		case PAUSE_WRITING:
			pauseWriting();
			break;
			
		case RESUME:
			resume();
			break;
		case RESUME_READING:
			resumeReading();
			break;
		case RESUME_WRITING:
			resumeWriting();
			break;
			
			default:
		}
	}
	
		
	public void setView(JTextComponent view)
	{
		this.view = view;
		
		this.view.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				onMouseClicked(e);
			}

			@Override
			public void mouseEntered(MouseEvent e){	}

			@Override
			public void mouseExited(MouseEvent e){}

			@Override
			public void mousePressed(MouseEvent e){	}

			@Override
			public void mouseReleased(MouseEvent e)	{}
		});		
	}

	public String getText()
	{
		pause();
		
		String data = null;
		
		if(view instanceof JTextArea)
		{
			data = this.view.getText();
		}
		else if (view instanceof JTextPane)
		{
			data = this.view.getText().replace(System.lineSeparator(), "\n");
		}
		else
		{
			data="";
		}
		
		resume();
		
		return data;
	}
	public void refreshView()
    {
		clearView();
		
		setState(ControllerState.RESUME_WRITING);
		
		for (LogLine line:document)
			display(line);
		
		setState(ControllerState.RESUME);
	    
    }
	
	
	public void addDisposeListner(EventListener newListener)
    {
	    this.disposeListners.add(newListener);
	    
    }
	
	public void removeDisposeListner(EventListener newListener)
    {
	    this.disposeListners.remove(newListener);
    }
	
	public void doCopy()
	{
		view.copy();
	}


}
