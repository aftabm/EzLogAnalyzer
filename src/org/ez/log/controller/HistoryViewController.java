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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.ez.log.util.DateUtil;
import org.ez.log.view.MainView;
import org.ez.log.view.MainView.ViewName;

public class HistoryViewController extends  Controller
{
	private JTextArea view;
	private LinkedBlockingQueue<String> displayQueue=null;
	
	private boolean autoScroll=false;
	private int lineCount;
	private boolean pauseWriting=false;	

	public HistoryViewController()
	{
		this.displayQueue = new LinkedBlockingQueue<String>();
		startViewUpdateThread();
	}
	
		
	public void clear()
    {
		this.pauseWriting=true;
	    this.displayQueue.clear();
	    view.getHighlighter().removeAllHighlights();
	    view.setDocument(new PlainDocument());
	    this.lineCount=0;

	    MainView.getInstance().setViewTitle(ViewName.HISTORY_VIEW, ViewName.HISTORY_VIEW.label+" ("+lineCount+")");
	    this.pauseWriting=false;
    }

	/*Adds to display queue*/
	public void insert(String message)
    {
		if (message==null)
			return;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(DateUtil.getCurrentDateTime());
		sb.append("   ");
		sb.append(message);
		
		if (!message.endsWith(System.lineSeparator()))
			sb.append(System.lineSeparator());
		
		display(sb.toString());
	    
    }
	
	
	public void display(String message) 
	{
		try
        {
	        this.displayQueue.offer(message, 1000, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
	        e.printStackTrace();
        }

	}

	public String getDocument()
    {
	    return this.view.getText();
    }

	public JTextComponent getView()
    {
	    return this.view;
    }

	public void onAutoScrollClicked()
    {
	    this.autoScroll=!autoScroll;
    }

	public void onWrapClicked()
    {
		this.view.setLineWrap(!this.view.getLineWrap());
    }

	public void setView(JTextArea view) 
	{
		this.view = view;
	}

	public void showView() 
	{
		MainView.getInstance().showView(ViewName.HISTORY_VIEW);
	}

	protected void startViewUpdateThread() 
	{
		Thread textAreaUpdateThread = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				while(true)
				{
					try 
					{
						if(pauseWriting)
						{
							Thread.sleep(1000);
							continue;
						}
						
						String line = displayQueue.take();
						
						if (line!=null && !line.trim().isEmpty())
						{
							lineCount++;
							view.append(line);
						}
						
						if (displayQueue.size() == 0)
						{
							MainView.getInstance().setViewTitle(ViewName.HISTORY_VIEW, ViewName.HISTORY_VIEW.label+" ("+lineCount+")");
						}						

					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		});
		
		textAreaUpdateThread.setName("textAreaUpdateThread");
		textAreaUpdateThread.setDaemon(true);
		textAreaUpdateThread.start();
	}

}
