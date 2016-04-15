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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.ActionType;
import org.ez.log.util.SystemUtil;
import org.ez.log.util.UiUtil;
import org.ez.log.view.IDialog;
import org.ez.log.view.LogFileAndFormatSelectionDlg;
import org.ez.log.view.LogFileSelectionDlg;
import org.ez.log.view.MainView;

public class LogFileOpenDlgController extends  DialogController 
{

	private List<File> logFiles=null;
	private File currentFolder=null;
	private LogFileSelectionDlg logFileSelectionDlg=null;
	private LogFileAndFormatSelectionDlg logFileAndFormatSelectionDlg=null;
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ActionType actionType = ActionType.parse(e.getActionCommand());
		IDialog dlg = null;
		
		switch(actionType)
		{
			case OPEN_LOG:
				
				if(SystemUtil.isDefaultFormatExists())
				{
					if (logFileSelectionDlg ==null)
						logFileSelectionDlg = new LogFileSelectionDlg(this);
					
					dlg=logFileSelectionDlg;					
				}
				else
				{
				
					if (logFileAndFormatSelectionDlg ==null)
					{
						logFileAndFormatSelectionDlg = new LogFileAndFormatSelectionDlg(this);
						logFileAndFormatSelectionDlg.setLocation(UiUtil.getCenterLocation(MainView.getInstance().getMainFrame(), (Component) logFileAndFormatSelectionDlg));
					}
					
					dlg=logFileAndFormatSelectionDlg;
				}
				
				dlg.showDialog();
			break;
			
			default:
				//ERROR
		}
	}
	
	
	public File getCurrentFolder() 
	{
		return this.currentFolder;
	}


	public List<File> getSelectedLogFiles()
	{
		return logFiles;
	}

	public void onFileSelected(File logFolder, List<File> selectedFiles) 
	{
		this.logFiles = selectedFiles;
		this.currentFolder = logFolder;
		
		if (logFiles!=null && logFiles.size()>0)
		{
			Event event = Event.createEvent(EventType.LOG_FILE_SELECTED);
			event.setData(EventDataKey.LOG_FILES, logFiles);
			event.setData(EventDataKey.FORMAT_FILE, SystemUtil.getFormatFile());
			EventManager.getInstance().fireEvent(event);
		}
	}
	
	
	public void onFileAndFormatSelected(File logFolder, List<File> selectedFiles, File formatFile) 
	{
		this.logFiles = selectedFiles;
		this.currentFolder = logFolder;
		
		if (logFiles!=null && logFiles.size()>0)
		{
			Event event = Event.createEvent(EventType.LOG_FILE_SELECTED);
			event.setData(EventDataKey.LOG_FILES, logFiles);
			event.setData(EventDataKey.FORMAT_FILE, formatFile);
			EventManager.getInstance().fireEvent(event);
		}
	}
	



}
