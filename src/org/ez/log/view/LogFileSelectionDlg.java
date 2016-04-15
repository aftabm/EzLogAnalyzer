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
package org.ez.log.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ez.log.controller.LogFileOpenDlgController;
import org.ez.log.util.DefaultProperties;
import org.ez.log.util.UiUtil;

public class LogFileSelectionDlg   extends JDialog implements IDialog
{
    private static final long serialVersionUID = 1L;
	private List<File> selectedFiles;
	private LogFileOpenDlgController controller;
	private File currentFolder;
	private JFrame parent = MainView.getInstance().getMainFrame();
	
	public LogFileSelectionDlg(LogFileOpenDlgController controller)
	{
		this.controller = controller;
	}
	
	public File getFolder()
	{
		return currentFolder;
	}
	
	public List<File> getSelection()
	{
		return this.selectedFiles;
	}
		
	public void showDialog() 
	{
		File folder = controller.getCurrentFolder();
		List<File> selectedFiles = new ArrayList<File>();
		
		JFileChooser chooser= new JFileChooser();
		
		if (folder==null)
		{
			folder = DefaultProperties.getDefaultLogFolder(); 
		}
		
		chooser.setCurrentDirectory(folder);
		
		chooser.setMultiSelectionEnabled(true);
		chooser.setControlButtonsAreShown(true);		
		chooser.setDialogTitle("Log file(s) selector");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("OK");
		chooser.setFileFilter(new FileNameExtensionFilter("*.log","log"));
				
		chooser.setLocation(UiUtil.getCenterLocation(parent, chooser));
		
		int choice = chooser.showOpenDialog(parent);
		
		currentFolder = chooser.getCurrentDirectory();
		
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			selectedFiles.addAll(Arrays.asList(chooser.getSelectedFiles()));	
		}
		
		controller.onFileSelected(currentFolder, selectedFiles);
	}
	
}
