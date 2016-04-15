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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import org.ez.log.om.ActionType;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.util.FileUtil;
import org.ez.log.util.SystemUtil;
import org.ez.log.view.FileSaveDlg;
import org.ez.log.view.MainView;
import org.ez.log.view.MainView.ViewName;

public class FileSaveDlgController extends DialogController
{
	private static final ConsoleLogger<FileSaveDlgController> logger = ConsoleLogger.create(FileSaveDlgController.class);
	private File currentFolder=SystemUtil.getDefaultExportOutFolder();
	private FileSaveDlg dlg;
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		ActionType actionType = ActionType.parse(e.getActionCommand());
		
		switch(actionType)
		{
			case EXPORT:
				if (dlg ==null)
					dlg = new FileSaveDlg(this, MainView.getInstance().getMainFrame());
				
				dlg.show();
				
				break;
				
			default:
		}

	}

	public File getFolder()
    {
	    return this.currentFolder;
    }

	public void onFolderSelected(File folder)
    {
	    this.currentFolder = folder;
	    
	    List<ViewController> controllers = ControllerFactory.getViewControllers();
	    controllers.remove(ControllerFactory.getLogViewController());
	    
	    for (ViewController controller: controllers)
	    {
	    	FileUtil.writeFile(currentFolder.getAbsolutePath()+File.separatorChar +
		    		controller.getView().getName()+".txt",controller.getView().getText(), false);
	    }
	    
/*	    FileUtil.writeFile(currentFolder.getAbsolutePath()+File.separatorChar+
	    		"error_view.log",ControllerFactory.getErrorViewController().getView().getText(), false);
	    
	    FileUtil.writeFile(currentFolder.getAbsolutePath()+File.separatorChar+
	    		"filter_view.log",ControllerFactory.getFilteredViewController().getView().getText(), false);
	    
	    
	    FileUtil.writeFile(currentFolder.getAbsolutePath()+File.separatorChar+
	    		"log_view.log",ControllerFactory.getLogViewController().getView().getText(), false);	    
	    
	    FileUtil.writeFile(currentFolder.getAbsolutePath()+File.separatorChar+
	    		"console_view.log",ControllerFactory.getHistoryViewController().getView().getText(), false);	    
*/	    
	    MainView.getInstance().showView(ViewName.HISTORY_VIEW);
    }

}
