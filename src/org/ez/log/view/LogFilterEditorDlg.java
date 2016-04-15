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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ez.log.controller.ControllerFactory;
import org.ez.log.controller.FilterDlgController;
import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.Filter;
import org.ez.log.om.FilterType;
import org.ez.log.util.StyleManager;
import org.ez.log.util.SystemUtil;
import javax.swing.JTextArea;
import java.awt.Color;

public class LogFilterEditorDlg extends JDialog 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField[] textFields = new JTextField[10];
	private FilterDlgController controller=null;
	private JFrame parent = MainView.getInstance().getMainFrame();
	
	private Filter filter = null;
	private JTextField textField_1;
	private JTextField tfKeyword;
	
	
	/**
	 * Create the dialog.
	 */
	public LogFilterEditorDlg(JFrame parent, FilterDlgController controller)
	{
		super(parent);
		this.controller=controller;
		
		this.readDefaultFilterFile(); 
		
		setTitle("Create Filter");
		setBounds(100, 100, 480, 356);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{46, 366, 0};
		gbl_contentPanel.rowHeights = new int[]{20, 20, 20, 20, 20, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		contentPanel.setLayout(gbl_contentPanel);
		
		JLabel lblFromDate;
		JLabel lblToDate;
		JLabel lblThread;
		JLabel lblClass;
		{
			JTextArea txtrNote1 = new JTextArea();
			txtrNote1.setForeground(Color.BLUE);
			txtrNote1.setWrapStyleWord(true);
			txtrNote1.setRequestFocusEnabled(false);
			txtrNote1.setOpaque(false);
			txtrNote1.setEditable(false);
			txtrNote1.setFont(StyleManager.consolas10);
			
			StringBuilder sb = new StringBuilder();
			sb.append("This filter will ignore log line that does not match with parser format.").append(" "); 
			sb.append("If your log file format is diffent then you should create “keyword” based filter only.").append(System.lineSeparator());			
			sb.append("This filter is applicable to new data only. Create this filter before reading log file(s).").append(" ");
			sb.append("Make sure you have selected correct format file. Default format file is 'conf/default.format'.");
			txtrNote1.setText(sb.toString());
			txtrNote1.setLineWrap(true);
			GridBagConstraints gbc_txtrNote1 = new GridBagConstraints();
			gbc_txtrNote1.fill = GridBagConstraints.BOTH;
			gbc_txtrNote1.insets = new Insets(0, 0, 5, 0);
			gbc_txtrNote1.gridx = 1;
			gbc_txtrNote1.gridy = 0;
			contentPanel.add(txtrNote1, gbc_txtrNote1);
		}
		
		{
			lblFromDate = new JLabel("From Date:");
			lblFromDate.setToolTipText("");
			GridBagConstraints gbc_lblFromDate = new GridBagConstraints();
			gbc_lblFromDate.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
			gbc_lblFromDate.insets = new Insets(0, 0, 5, 5);
			gbc_lblFromDate.gridx = 0;
			gbc_lblFromDate.gridy = 1;
			contentPanel.add(lblFromDate, gbc_lblFromDate);
		}
		
		{
			JTextField tfFromDate = new JTextField();
	//		tfFromDate.setToolTipText(FilterValueParser.dateFormat);
			GridBagConstraints gbc_tfFromDate = new GridBagConstraints();
			gbc_tfFromDate.fill = GridBagConstraints.BOTH;
			gbc_tfFromDate.insets = new Insets(0, 0, 5, 0);
			gbc_tfFromDate.gridx = 1;
			gbc_tfFromDate.gridy = 1;
			contentPanel.add(tfFromDate, gbc_tfFromDate);
			tfFromDate.setColumns(10);
			this.textFields[FilterType.FromDate.ordinal()]=tfFromDate;
			tfFromDate.setFont(StyleManager.consolas12);
		}
		{
			lblToDate = new JLabel("To Date:");
			GridBagConstraints gbc_lblToDate = new GridBagConstraints();
			gbc_lblToDate.anchor = GridBagConstraints.EAST;
			gbc_lblToDate.insets = new Insets(0, 0, 5, 5);
			gbc_lblToDate.gridx = 0;
			gbc_lblToDate.gridy = 2;
			contentPanel.add(lblToDate, gbc_lblToDate);
		}
		{
			JTextField tfToDate = new JTextField();
			//tfToDate.setToolTipText(FilterValueParser.dateFormat);
			GridBagConstraints gbc_tfToDate = new GridBagConstraints();
			gbc_tfToDate.anchor = GridBagConstraints.NORTH;
			gbc_tfToDate.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfToDate.insets = new Insets(0, 0, 5, 0);
			gbc_tfToDate.gridx = 1;
			gbc_tfToDate.gridy = 2;
			contentPanel.add(tfToDate, gbc_tfToDate);
			tfToDate.setColumns(10);
			this.textFields[FilterType.ToDate.ordinal()]=tfToDate;
			tfToDate.setFont(StyleManager.consolas12);
		}
		{
			JLabel lblLogLevel = new JLabel("Log level:");
			GridBagConstraints gbc_lblLogLevel = new GridBagConstraints();
			gbc_lblLogLevel.anchor = GridBagConstraints.EAST;
			gbc_lblLogLevel.insets = new Insets(0, 0, 5, 5);
			gbc_lblLogLevel.gridx = 0;
			gbc_lblLogLevel.gridy = 3;
			contentPanel.add(lblLogLevel, gbc_lblLogLevel);
		}
		{
			JTextField tfLogLevel = new JTextField();
			tfLogLevel.setToolTipText("trace | debug | error | warn | info");
			GridBagConstraints gbc_tfLogLevel = new GridBagConstraints();
			gbc_tfLogLevel.insets = new Insets(0, 0, 5, 0);
			gbc_tfLogLevel.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfLogLevel.gridx = 1;
			gbc_tfLogLevel.gridy = 3;
			contentPanel.add(tfLogLevel, gbc_tfLogLevel);
			tfLogLevel.setColumns(10);
			this.textFields[FilterType.LogLevel.ordinal()]=tfLogLevel;
			tfLogLevel.setFont(StyleManager.consolas12);
		}
		{
			lblThread = new JLabel("Thread(s):");
			GridBagConstraints gbc_lblThread = new GridBagConstraints();
			gbc_lblThread.anchor = GridBagConstraints.EAST;
			gbc_lblThread.insets = new Insets(0, 0, 5, 5);
			gbc_lblThread.gridx = 0;
			gbc_lblThread.gridy = 4;
			contentPanel.add(lblThread, gbc_lblThread);
		}
		{
			JTextField tfThreadName = new JTextField();
			tfThreadName.setToolTipText("more than one thread names are OK, provided that they are seperated by {or}. e.g. main {or} http-8443");
			GridBagConstraints gbc_tfThreadName = new GridBagConstraints();
			gbc_tfThreadName.anchor = GridBagConstraints.NORTH;
			gbc_tfThreadName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfThreadName.insets = new Insets(0, 0, 5, 0);
			gbc_tfThreadName.gridx = 1;
			gbc_tfThreadName.gridy = 4;
			contentPanel.add(tfThreadName, gbc_tfThreadName);
			tfThreadName.setColumns(10);
			this.textFields[FilterType.ThreadName.ordinal()]=tfThreadName;
			tfThreadName.setFont(StyleManager.consolas12);
		}
		{
			lblClass = new JLabel("Class(es):");
			lblClass.setToolTipText("{and}, {or}, {not}");
			GridBagConstraints gbc_lblClass = new GridBagConstraints();
			gbc_lblClass.anchor = GridBagConstraints.EAST;
			gbc_lblClass.insets = new Insets(0, 0, 5, 5);
			gbc_lblClass.gridx = 0;
			gbc_lblClass.gridy = 5;
			contentPanel.add(lblClass, gbc_lblClass);
		}
		{
			JTextField tfClassName = new JTextField();
			tfClassName.setToolTipText("full classname including pacakge. Package name only is supported. {or} {and} {not} supported. ");
			GridBagConstraints gbc_tfClassName = new GridBagConstraints();
			gbc_tfClassName.anchor = GridBagConstraints.NORTH;
			gbc_tfClassName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfClassName.insets = new Insets(0, 0, 5, 0);
			gbc_tfClassName.gridx = 1;
			gbc_tfClassName.gridy = 5;
			contentPanel.add(tfClassName, gbc_tfClassName);
			tfClassName.setColumns(10);
			this.textFields[FilterType.ClassName.ordinal()]=tfClassName;
			tfClassName.setFont(StyleManager.consolas12);
		}
		{
			JLabel lblMessage = new JLabel("Message:");
			GridBagConstraints gbc_lblMessage = new GridBagConstraints();
			gbc_lblMessage.anchor = GridBagConstraints.EAST;
			gbc_lblMessage.insets = new Insets(0, 0, 5, 5);
			gbc_lblMessage.gridx = 0;
			gbc_lblMessage.gridy = 6;
			contentPanel.add(lblMessage, gbc_lblMessage);
		}
		{
			JTextField tfMessage = new JTextField();
			tfMessage.setToolTipText("To findall word(s) in a a log message. Each work seperated by {or} | {and} {not}. Each statment seperated by ;");
			GridBagConstraints gbc_tfMessage = new GridBagConstraints();
			gbc_tfMessage.anchor = GridBagConstraints.NORTH;
			gbc_tfMessage.insets = new Insets(0, 0, 5, 0);
			gbc_tfMessage.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfMessage.gridx = 1;
			gbc_tfMessage.gridy = 6;
			contentPanel.add(tfMessage, gbc_tfMessage);
			tfMessage.setColumns(10);
			this.textFields[FilterType.Message.ordinal()]=tfMessage;
			tfMessage.setFont(StyleManager.consolas12);
		}
		{
			JTextField testField3 = new JTextField();
			testField3.setBorder(null);
			testField3.setEnabled(false);
			testField3.setEditable(false);
			testField3.setToolTipText("To findall word(s) in a a log rawData. Each work seperated by {or} | {and} {not}. Each statment seperated by ;");
			GridBagConstraints gbc_testField3 = new GridBagConstraints();
			gbc_testField3.insets = new Insets(0, 0, 5, 0);
			gbc_testField3.fill = GridBagConstraints.HORIZONTAL;
			gbc_testField3.anchor = GridBagConstraints.NORTH;
			gbc_testField3.gridx = 1;
			gbc_testField3.gridy = 7;
			contentPanel.add(testField3, gbc_testField3);
			testField3.setColumns(10);
			
			
			testField3.setFont(StyleManager.consolas12);
		}
		{
			JLabel label = new JLabel("Keyword(s):");
			label.setToolTipText("To findall word(s) in a a log rawData");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.gridx = 0;
			gbc_label.gridy = 8;
			contentPanel.add(label, gbc_label);
		}
		{
			tfKeyword = new JTextField();
			tfKeyword.setToolTipText("To findall word(s) in a a log rawData. Each work seperated by {or} | {and} {not}. Each statment seperated by ;");
			tfKeyword.setFont(StyleManager.consolas12);
			tfKeyword.setColumns(10);
			GridBagConstraints gbc_tfKeyword = new GridBagConstraints();
			gbc_tfKeyword.insets = new Insets(0, 0, 5, 0);
			gbc_tfKeyword.fill = GridBagConstraints.HORIZONTAL;
			gbc_tfKeyword.gridx = 1;
			gbc_tfKeyword.gridy = 8;
			contentPanel.add(tfKeyword, gbc_tfKeyword);
			this.textFields[FilterType.Keyword.ordinal()]=tfKeyword;
		}
		{
			JLabel label = new JLabel("Hint:");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 0, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 9;
			contentPanel.add(label, gbc_label);
		}
		{
			textField_1 = new JTextField();
			textField_1.setBorder(null);
			textField_1.setText("word {and}|{or}|{not} word;");
			textField_1.setFont(new Font("Consolas", Font.PLAIN, 12));
			textField_1.setEditable(false);
			textField_1.setColumns(10);
			GridBagConstraints gbc_textField_1 = new GridBagConstraints();
			gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_1.gridx = 1;
			gbc_textField_1.gridy = 9;
			contentPanel.add(textField_1, gbc_textField_1);
		}
		
		
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new GridLayout(0, 6, 0, 0));
	
			
			{
				JButton btnReadFromFile = new JButton("Load");
				btnReadFromFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
				buttonPane.add(btnReadFromFile);
				
				btnReadFromFile.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						onReadFromFile(e);
						
					}
				});
				{
					JButton btnSave = new JButton("Save");
					btnSave.setFont(new Font("Tahoma", Font.PLAIN, 11));
					btnSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							onSave(ae);
						}
					});
					buttonPane.add(btnSave);
				}
				{
					JSeparator separator = new JSeparator();
					buttonPane.add(separator);
				}
			}
			
			{
				JButton btnClear = new JButton("Clear");
				btnClear.setFont(new Font("Tahoma", Font.PLAIN, 11));
				btnClear.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						onClear(e);
						
					}
				});
				buttonPane.add(btnClear);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
				okButton.setActionCommand("OK");				
				okButton.addActionListener(this.controller);
				
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
			//cancelButton.setDefaultCapable(true);
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					onCancel(e);
					
				}
			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
			
						
						{
						}			
		}
	}
	
	

	public Filter getData() 
	{
		updateFilterObject();
		
		return this.filter.clone();
	}

	protected void onCancel(ActionEvent e) 
	{
		
		updateFileds();
		
		this.setVisible(false);
	}

	protected void onClear(ActionEvent e) {
		
		for (JTextField textField:textFields)
		{
			textField.setText("");	
		}
	}

	protected void onOk(ActionEvent e) 
	{
		updateFilterObject();
		
		Event event = new Event(EventType.FILTER_SELECTED);
		event.setData(EventDataKey.FILTER, filter.clone());
		
		EventManager.getInstance().fireEvent(event);

		
		this.setVisible(false);
	}

	protected void onReadFromFile(ActionEvent e) 
	{
		JFileChooser chooser= new JFileChooser();
		
		try 
		{
			chooser.setCurrentDirectory(new File(SystemUtil.confFolderUrl.toURI()));
		
			chooser.setMultiSelectionEnabled(false);
			chooser.setControlButtonsAreShown(true);		
			chooser.setDialogTitle("Filter file selector");
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);		
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setApproveButtonText("OK");
			chooser.setFileFilter(new FileNameExtensionFilter("*.filter","filter"));
		
			int choice = chooser.showOpenDialog(this);
		
			if (choice != JFileChooser.APPROVE_OPTION) return;
		
			File file = chooser.getSelectedFile();
			this.filter = new Filter(file);
			//this.filter = FilterFilerReader.read(file);
			
			updateFileds();
			
			//update(FilterFileReader.getFilters(file));
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}		
	}

	protected void onSave(ActionEvent ae)
    {
	    
		updateFilterObject();
		
		JFileChooser chooser= new JFileChooser();
		
		try 
		{
			chooser.setCurrentDirectory(new File(SystemUtil.confFolderUrl.toURI()));
		
			chooser.setMultiSelectionEnabled(false);
			chooser.setControlButtonsAreShown(true);		
			chooser.setDialogTitle("Save Filter");
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);		
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setApproveButtonText("OK");
			chooser.setFileFilter(new FileNameExtensionFilter("*.filter","filter"));
		
			int choice = chooser.showSaveDialog(this);
		
			if (choice != JFileChooser.APPROVE_OPTION) return;
		
			File file = chooser.getSelectedFile();
			
			if (!file.getName().endsWith(".filter"))
				file = new File(file.getAbsolutePath()+".filter");

			Filter newFilter = new Filter(file);
			this.filter = newFilter;
			this.updateFilterObject();
			newFilter.save();
			
			
			
			if(file.exists())
				ControllerFactory.getStatusViewController().display("Filter saved: "+file.getAbsolutePath(), true);
			else
				ControllerFactory.getStatusViewController().display("Unable to save Filter: "+file.getAbsolutePath(), true);
			
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		
    }

	private void readDefaultFilterFile() 
	{
		this.filter = new Filter(SystemUtil.getDefaultFilterFile());
		
	}
	
	
	public void setLogLine(String line)
	{
		if (line==null || line.trim().isEmpty())
			return;
		
		if (line.startsWith("["))
		{
			String[] lineNumberTokens = line.split(" ", 2);
			line = lineNumberTokens[1];
		}
		
	}

	private void updateFileds() 
	{
		for (FilterType fieldName: FilterType.values())
		{
			textFields[fieldName.ordinal()].setText(filter.get(fieldName)) ;
		}	
	}

	private void updateFilterObject() 
	{
		for (FilterType fieldName: FilterType.values())
		{
			this.filter.set(fieldName, textFields[fieldName.ordinal()].getText());
		}
		
	}	

}
