/**
 * Copyright � 2013 Aftab Mahmood
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details <http://www.gnu.org/licenses/>.
 **/
package org.ez.log.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventListener;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.Filter;
import org.ez.log.om.LogLine;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.util.LogFileReader;
import org.ez.log.util.StyleManager;
import org.ez.log.util.SystemUtil;
import org.ez.log.view.MainView;
import org.ez.log.view.MainView.ViewName;

public class LogViewController extends ViewController
{
	private static final ConsoleLogger<LogViewController> logger = ConsoleLogger.create(LogViewController.class);
	private List<File> logFiles = null;
	private boolean readingFile=false;
	private boolean noDuplicates = true;
	private boolean tailFile = false;
	private LogFileReader fileReader=null;
	private File formatFile= SystemUtil.getFormatFile();	

	/**
	 * 
	 */
	public LogViewController()
	{
		registerLogFileSelectedEvent();
		startViewUpdateThread();
	}

	
	public void doNoDuplicates()
	{
		this.noDuplicates = !this.noDuplicates;
		
		ControllerFactory.getStatusViewController().display(
		        "Setting noDuplicate = " + this.noDuplicates, true);	
	}
	
	public void doTail()
	{
		this.tailFile = !this.tailFile;
		
		ControllerFactory.getStatusViewController().display(
		        "Setting Tail = " + this.tailFile, true);

		if (this.tailFile && this.fileReader != null && !readingFile)
		{
			Runnable tailReader = new Runnable()
			{
				@Override
				public void run()
				{
					readLogFile();
				}
			};

			Thread thread = new Thread(tailReader);
			thread.setName("logview_tail");
			thread.setDaemon(true);
			thread.start();
		}
	}
	
	public void doReloadLog()
	{
		ControllerFactory.getStatusViewController().display("Reloading logs.", true);

		if (this.logFiles == null || this.logFiles.size() == 0)
		{
			String lastLogFile = SystemUtil.getLastLogFilename();

			if (lastLogFile != null && !lastLogFile.trim().isEmpty())
			{
				ControllerFactory.getStatusViewController().display("Reading Last log file: " + lastLogFile, true);
				
				this.logFiles = new ArrayList<File>();
				File logFile = new File(lastLogFile);

				if (logFile.exists())
					this.logFiles.add(logFile);
				else
					ControllerFactory.getStatusViewController().display("unable to read last log file:" + lastLogFile,  true);
			}
		}
		
		this.readLogs();
	}



	
	@Override
	public void clearView()
	{
		pause();
		
		view.getHighlighter().removeAllHighlights();
		view.setDocument(new DefaultStyledDocument());
		
		viewLineCount = 0;
		insertAt=0;
		MainView.getInstance().setViewTitle(ViewName.LOG_VIEW, ViewName.LOG_VIEW.label+" ("+viewLineCount+")");
		
		resume();
	}

	private int extractStartPosition(String line)
	{
		if (line != null)
		{
			Pattern pattren = Pattern.compile("\\{([0-9]+)\\}");
			Matcher m = pattren.matcher(line);

			if (m.find() && m.groupCount() >= 1)
			{
				return Integer.parseInt(m.group(1));
			}
		}

		return 0;
	}

	@Override
	public void finalize()
	{
		super.finalize();
		
		if (fileReader != null)
		{
			this.fileReader.dispose();
			this.fileReader = null;
		}
	}
	
	@Override
	public JTextPane getView()
	{
	    return (JTextPane)view;
	}


	@Override
    public void insert(LogLine line)
    {
	    document.add(line);
	    display(line);
    }
	

	public void onHighLiteCmd(String line)
	{
		final String methodName = "hilite";

		String textToHilite = line.trim();

		if (textToHilite == null || textToHilite.isEmpty())
			return;

		try
		{
			MainView.getInstance().setAutoScrollSelected(false);
			int startPosition = extractStartPosition(line);

			String lineFromView = this.view.getText(startPosition, textToHilite.length()).trim();

			if (lineFromView == null || lineFromView.isEmpty())
				return;

			if (textToHilite.startsWith(lineFromView))
			{
				removeHighlights(StyleManager.getInstance().getContextLineHighlightPainter());
				int endPosition = startPosition + line.length();
				
				highlight(startPosition,
				        endPosition,
				        StyleManager.getInstance().getContextLineHighlightPainter());

				MainView.getInstance().showView(ViewName.LOG_VIEW);
			}
			else
			{
				if(highlight(new String[]{line}, StyleManager.getInstance().getContextLineHighlightPainter()))
				{
					MainView.getInstance().showView(ViewName.LOG_VIEW);
				}
			}

		}
		catch (Exception e)
		{
			//logger.logInfo(methodName, "Bad position for: " + line);
			ControllerFactory.getStatusViewController().display(
			        "Unable to highlight. Text not found in Log View." + line, true);
			
			//logger.logError(methodName, e);
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
		e.consume();
	}

	private void readLogFile()
	{
		final String methodName = "readLogFile";
		
		if (fileReader == null)
		{
			logger.error(methodName, new java.lang.IllegalStateException(
			        "Unable to read log file. Reader is null"));
			return;
		}

		ControllerFactory.getStatusViewController().display("Reading file: "+fileReader.getFilePath(), true);

		try
		{
			LogLine line = null;
			LogLine lastLine = null;

			readingFile = true;
			
			insert(new LogLine("+-------------------------------------------------------------------------------------"));
			insert(new LogLine("   " +fileReader.getFilePath()));
			insert(new LogLine("-------------------------------------------------------------------------------------+"));
			
			
			while (readingFile)
			{
				if (pauseReading)
				{
					Thread.sleep(1000);
					continue;
				}
				
				line = fileReader.readLine();
				
				if (line != null)
				{
					ControllerFactory.getStatusViewController().update(line.getRawText());
					
					if(line.isEmpty())
						continue;
					
					if (this.noDuplicates == true
					        && line.hasSameMessge(lastLine))
					{
						logger.debug(methodName, "Skipping found duplicate.");
						logger.debug(methodName, "Line2:"+lastLine);
						logger.debug(methodName, "Line2:"+line);
						continue;
					}

					if (lastLine != null)
					{
						if (lastLine.isMyChild(line))
						{
							lastLine.addChild(line);
							continue;
						}
						
						document.add(lastLine);
						display(lastLine);
					}

					lastLine = line;				
				}
				else
				{
					if (lastLine != null)
					{
						document.add(lastLine);
						display(lastLine);
						lastLine = line;
					}

					if (tailFile && logFiles.size() == 1)
					{
						ControllerFactory.getStatusViewController()
						        .update("Taling log file: "+fileReader.getFilePath());
						Thread.sleep(1000);
						continue;
					}

					readingFile = false;
					break;
				}

			}

			// last line
			if (line != null)
			{
				document.add(line);
				display(line);
				lastLine = line;
			}
		}
		catch (Exception e)
		{
			logger.error(methodName, e);
		}
		
		ControllerFactory.getStatusViewController().display("Done reading log file "+fileReader.getFilePath(), true);
	}

	private void readLogs()
	{
		final String methodName = "readLogs";
		
		if (logFiles == null || logFiles.size() == 0)
		{
			ControllerFactory.getStatusViewController().display(
			        "No log file to read.", true);
			return;
		}

		reset();

		ControllerFactory.getErrorViewController().reset();
		//ControllerFactory.getFilteredViewController().reset();

		Thread readLogThread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				File lastFile=null;
				
				for (File file : logFiles)
				{
					try
					{
						if (fileReader != null)
							fileReader.dispose();

						fileReader = LogFileReader.creatInstance(formatFile).open(file);
						lastFile=file;
						
/*						ControllerFactory.getStatusViewController().display(
						        "Reading log file :" + file.getAbsolutePath(), true);*/

						readLogFile();
						MainView.getInstance().setToolTip(view.getName(), file.getAbsolutePath());
						
					}
					catch (FileNotFoundException e)
					{
						logger.error(methodName, e);
					}
				}

				SystemUtil.setLastLogFilename(lastFile);
			}
		});

		readLogThread.setName("logReaderThread");
		readLogThread.setDaemon(true);
		readLogThread.start();

	}



	/**
	 * 
	 */
	protected void registerLogFileSelectedEvent()
	{
		EventManager.getInstance().addListner(EventType.LOG_FILE_SELECTED,
		        new EventListener()
		        {
					@Override
			        public void onEvent(Event event)
			        {
						logFiles = event.getData(EventDataKey.LOG_FILES, List.class);
				        formatFile = event.getData(EventDataKey.FORMAT_FILE, File.class);
				        readLogs();
			        }
		        });
	}

/*	public void doLogFilesSelected(List<File> logFiles)
	{
	}
*/	
	@Override
    public	void reset()
	{
		super.reset();
		
		if(fileReader!=null)
			this.fileReader.dispose();
		
		fileReader=null;
	}

	public void setNoDuplicates(boolean filterDuplicates)
	{
		this.noDuplicates = filterDuplicates;

	}

	/**
	 * 
	 */
	protected void startViewUpdateThread()
	{
		// final String methodName = "startViewUpdateThread";
		
		Thread viewUpdateThread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{

					if (pauseWriting)
					{
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							logger.error("methodName", e);
						}
						continue;
					}

					try
					{
						LogLine line = displayQueue.take();

						if (line == null)
							continue;

						if (viewLineCount >= bufferSize)
						{
							pause();
							clearView();
							logger.info("startViewUpdateThread", "Line count reached bufferSize. Resting view.");
							resume();
						}
						
						
						//if (insertAt == 0 || viewLineCount == 0)
							insertAt = view.getDocument().getEndPosition().getOffset();

						if (!line.hasLineNumber())
						{
							line.setLineNumber(++totalLineCount);
							line.setPostion(insertAt);
						}

						String lineToDisplay = line.getDecoratedText();
						AttributeSet paragraphAttributes = null;

						if (line.containsError())
						{
							paragraphAttributes = StyleManager.getInstance().getErrorParagraphAttribute();
							ControllerFactory.getErrorViewController().insert(line);
						}

						if (paragraphAttributes == null)
						{
							paragraphAttributes = StyleManager.getInstance().getDefaultParagraphAttribute();
						}

						view.getDocument().insertString(insertAt, lineToDisplay, paragraphAttributes);
						viewLineCount++;
						insertAt = insertAt + lineToDisplay.length();
						
						Event event = new Event(EventType.NEW_LOG_LINE);
						event.setData(EventDataKey.LOG_LINE, line);
						EventManager.getInstance().fireEvent(event);

						for (LogLine child : line.getChildren())
						{
							lineToDisplay = child.getDecoratedText();
							view.getDocument().insertString(insertAt, lineToDisplay, paragraphAttributes);
							viewLineCount++;
							insertAt = insertAt + lineToDisplay.length();
						}
						
						if (autoScroll)
						{
							if (displayQueue.size() == 0 || viewLineCount % refreshRate == 0)
							{
								view.setCaretPosition(insertAt - lineToDisplay.length());
								MainView.getInstance().setViewTitle(ViewName.LOG_VIEW, ViewName.LOG_VIEW.label+" ("+viewLineCount+")");
							}
						}
						
						MainView.getInstance().setViewTitle(ViewName.LOG_VIEW, ViewName.LOG_VIEW.label+" ("+viewLineCount+")");

						// Thread();
					}
					catch (Exception e)
					{
						logger.error("startViewUpdateThread", e);
					}
				}
			}

		});

		viewUpdateThread.setName("log_view_updater");
		viewUpdateThread.setDaemon(true);
		viewUpdateThread.start();
	}


	@Override
    void onFoldClicked()
    {
		//ignore
    }


	@Override
    public void setFilter(Filter filter)
    {
		//ignore
    }
	
}
