package org.ez.log.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ez.log.controller.FileSaveDlgController;
import org.ez.log.util.UiUtil;

public class FileSaveDlg 
{
	private FileSaveDlgController controller;
	private JFrame parent;
	
	public FileSaveDlg(FileSaveDlgController controller,JFrame parent)
	{
		this.controller = controller;
		this.parent = parent;
	}
		
	public void show() 
	{
		File folder = controller.getFolder();
		
		JFileChooser chooser= new JFileChooser();
		
		chooser.setCurrentDirectory(folder);
		
		chooser.setMultiSelectionEnabled(false);
		chooser.setControlButtonsAreShown(true);		
		chooser.setDialogTitle("Folder Selector");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setApproveButtonText("OK");
		chooser.setFileFilter(new FileNameExtensionFilter("*.log","log"));
				
		chooser.setLocation(UiUtil.getCenterLocation(parent, chooser));
		
		int choice = chooser.showSaveDialog(parent);
		
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			folder = chooser.getSelectedFile();
		}
		
		controller.onFolderSelected(folder);
	}
	
}
