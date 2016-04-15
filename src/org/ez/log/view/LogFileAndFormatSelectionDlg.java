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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.ez.log.controller.LogFileOpenDlgController;
import org.ez.log.util.DefaultProperties;
import org.ez.log.util.SystemUtil;
import org.ez.log.util.UiUtil;

import javax.swing.JList;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JSplitPane;

public class LogFileAndFormatSelectionDlg extends JDialog implements IDialog 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		try 
		{
			LogFileAndFormatSelectionDlg dialog = new LogFileAndFormatSelectionDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	private final JPanel contentPanel = new JPanel();
	private JTextField tfFormatFile;
	
	private JList<File> logFileList=null;
	private Set<File> selectedFiles = new HashSet<File>();;
	private File formatFile = SystemUtil.getConfFolder();
	private LogFileOpenDlgController controller=null;
	
	
	public LogFileAndFormatSelectionDlg(LogFileOpenDlgController controller) 
	{
		this.controller = controller;
		init();
	}
	
	
	LogFileAndFormatSelectionDlg()
	{
		init();
	}
	
	/**
	 * Create the dialog.
	 */
	public void init() 
	{
		selectedFiles.clear();
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Select Log File");
		setBounds(100, 100, 521, 266);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{339, 65, 0};
		gbl_contentPanel.rowHeights = new int[]{14, 99, 14, 23, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		JLabel lblNewLabel = new JLabel("Log File(s):");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		logFileList = new JList<File>();
		logFileList.setFont(new Font("Calibri", Font.PLAIN, 12));
		logFileList.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_logFileList = new GridBagConstraints();
		gbc_logFileList.fill = GridBagConstraints.BOTH;
		gbc_logFileList.insets = new Insets(0, 0, 5, 5);
		gbc_logFileList.gridx = 0;
		gbc_logFileList.gridy = 1;
		contentPanel.add(logFileList, gbc_logFileList);
		logFileList.setEnabled(true);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 1;
		gbc_splitPane.gridy = 1;
		contentPanel.add(splitPane, gbc_splitPane);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setBorder(null);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		
		JButton btnClearLogList = new JButton("Clear");
		btnClearLogList.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selectedFiles.clear();
				logFileList.setListData(new File[0]);
				
			}
		});
		
		splitPane_1.setLeftComponent(btnClearLogList);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		splitPane_1.setRightComponent(panel);
		
		JButton btnSelectLogFile = new JButton("Open");
		btnSelectLogFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onLogFileOpen();
			}
		});
		splitPane.setLeftComponent(btnSelectLogFile);
		
		JLabel lblFormat = new JLabel("Format Definition File:");
		GridBagConstraints gbc_lblFormat = new GridBagConstraints();
		gbc_lblFormat.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblFormat.insets = new Insets(0, 0, 5, 5);
		gbc_lblFormat.gridx = 0;
		gbc_lblFormat.gridy = 2;
		contentPanel.add(lblFormat, gbc_lblFormat);
		
		tfFormatFile = new JTextField();
		tfFormatFile.setBorder(new LineBorder(new Color(171, 173, 179)));
		tfFormatFile.setFont(new Font("Calibri", Font.PLAIN, 12));
		tfFormatFile.setText(formatFile.getAbsolutePath());
		GridBagConstraints gbc_tfFormatFile = new GridBagConstraints();
		gbc_tfFormatFile.anchor = GridBagConstraints.NORTH;
		gbc_tfFormatFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfFormatFile.insets = new Insets(0, 0, 0, 5);
		gbc_tfFormatFile.gridx = 0;
		gbc_tfFormatFile.gridy = 3;
		contentPanel.add(tfFormatFile, gbc_tfFormatFile);
		tfFormatFile.setColumns(10);
		
		JButton btnFormatFile = new JButton("Open");
		btnFormatFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onFormatFileOpen();
			}
		});

		GridBagConstraints gbc_btnOpenFormat = new GridBagConstraints();
		gbc_btnOpenFormat.anchor = GridBagConstraints.NORTH;
		gbc_btnOpenFormat.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpenFormat.gridx = 1;
		gbc_btnOpenFormat.gridy = 3;
		contentPanel.add(btnFormatFile, gbc_btnOpenFormat);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						onOkClicked();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) 
					{
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}

	protected void onOkClicked()
    {
		
		if (selectedFiles==null || selectedFiles.size()==0)
		{
			setVisible(false);
			return;
		}
		
		if (this.formatFile==null || !this.formatFile.isFile() || !this.formatFile.exists())
		{
			JOptionPane.showConfirmDialog (MainView.getInstance().getMainFrame(), 
					"Please select a valid format file to parse log. ",
					"Error",JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			
			return;
			
		}
		
		setVisible(false);
		controller.onFileAndFormatSelected(this.currentFolder, new ArrayList<File>(selectedFiles), this.formatFile);
    }

	protected void onFormatFileOpen()
    {
		JFileChooser chooser= new JFileChooser();
		
		File  confFolder = SystemUtil.getConfFolder(); 
		
		chooser.setCurrentDirectory(confFolder);
		
		chooser.setMultiSelectionEnabled(false);
		chooser.setControlButtonsAreShown(true);		
		chooser.setDialogTitle("Format file selector");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("OK");
		chooser.setFileFilter(new FileNameExtensionFilter("*.format","format"));
		//chooser.setLocation(UiUtil.getCenterLocation(parent, chooser));

		int choice = chooser.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			formatFile = chooser.getSelectedFile();
			tfFormatFile.setText(formatFile.getAbsolutePath());
		}

    }

	private File folder=null;
	private File currentFolder=null;
	private JFrame parent = MainView.getInstance().getMainFrame();
	
	
	protected void onLogFileOpen()
    {
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

		int choice = chooser.showOpenDialog(this);
		currentFolder = chooser.getCurrentDirectory();
		
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			selectedFiles.addAll(Arrays.asList(chooser.getSelectedFiles()));
			Vector<File> vt = new Vector<File>(selectedFiles);
			logFileList.setListData(vt);
		}
    }
	
	
	public void showDialog()
	{
		selectedFiles.clear();
		onLogFileOpen();
		setVisible(true);
	}
}
