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

//import java.awt.EventQueue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import org.ez.log.controller.ControllerFactory;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventManager;
import org.ez.log.event.EventType;
import org.ez.log.om.ActionType;
import org.ez.log.util.DefaultProperties;
import org.ez.log.util.FileUtil;
import org.ez.log.util.StyleManager;
import org.ez.log.util.SystemUtil;

public class MainView 
{
	//private static final ConsoleLogger<LogViewController> logger = ConsoleLogger.create(LogViewController.class);
	private static final String title = "ezLogAnalyzer 3.0 (Build:131205)";
	private static final String contactInfo="For question, bugs and enhancments, email to: aftab.mahmood@citrix.com";
	private JFrame mainFrame;
	private JTextField textFieldStatus;
	private JTabbedPane mainTab;
	private JCheckBox cbAutoScroll;
	protected List<String> commandHistory;
	private JComboBox<String> comboBoxCommand;
	
	private JCheckBox cbPause;
	private JCheckBox cbWrap;
	private JCheckBox cbFold;
	
	private static MainView instance=null;
	private static Map<String, JTextComponent> tabViews;
	private static Map<String, Component> tabComponents;
	private static int tabId=0;	
	
	public static enum ViewName
	{
		LOG_VIEW(0, "Log","MAIN_TAB"), FILTER_VIEW(1, "Filter", "MAIN_TAB"),  ERROR_VIEW(2, "Error(s)","MAIN_TAB"), HISTORY_VIEW(3, "History", "MAIN_TAB"), NONE(-1, "","");
		
		public static ViewName parse(int id)
		{
			
			for (ViewName viewName: values())
			{
				if (viewName.id==id)
					return viewName;
			}
			
			return NONE;
		}
		public final int id;
		public final String groupName;
		
		public final String label;
		
		private ViewName(int id, String label, String groupName)
		{
			this.id = id;
			this.groupName=groupName;
			this.label = label;
		}
	}

	/**
	 * Launch the application.
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) 
	{
		final List<File> files=new ArrayList<File>(0);
		
		if (args!=null && args.length>0)
		{
			for (String filename: args)
			{
				File file = new File(filename);
				if (file.exists())
					files.add(file);
				
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					MainView window = MainView.getInstance();					
					window.mainFrame.setVisible(true);
					
					if (files.size()>0)
					{
						org.ez.log.event.Event event = org.ez.log.event.Event.createEvent(EventType.LOG_FILE_SELECTED);
						event.setData(EventDataKey.LOG_FILES, files);
						event.setData(EventDataKey.FORMAT_FILE, SystemUtil.getFormatFile());
						EventManager.getInstance().fireEvent(event);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 */
	public static MainView getInstance()
	{
		if (instance==null)
		{
			synchronized(MainView.class)
			{
				if (instance==null)
					instance = new MainView();
			}
		}
		return instance;
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
	}

	/*public ViewName getActiveViewName()
	{
		return ViewName.parse(this.mainTab.getSelectedIndex());
	}*/
	
	public String getCurrentViewName()
	{
		return this.mainTab.getSelectedComponent().getName();
	}
	
	public JFrame getMainFrame()
	{
		return instance.mainFrame;
	}

	/**
	 * Initialize the contents of the mainFrame.
	 */
	private void initialize() {
		
		commandHistory = new ArrayList<String>();
		//viewNameMap = new HashMap<String, Component>();
		//tabNames=new ArrayList<String>();
		tabViews = new HashMap<String, JTextComponent>();
		tabComponents = new HashMap<String, Component>();
		
		mainFrame = new JFrame();
		mainFrame.setTitle(title);		
		mainFrame.setBounds(100, 100, 450, 300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPaneBottom = new JSplitPane();
		splitPaneBottom.setBorder(BorderFactory.createEmptyBorder());
		splitPaneBottom.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainFrame.getContentPane().add(splitPaneBottom, BorderLayout.SOUTH);
		
		JToolBar toolBarCommand = new JToolBar();
		toolBarCommand.setFloatable(false);
		//toolBarCommand.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		toolBarCommand.setBorder(BorderFactory.createEmptyBorder());
		splitPaneBottom.setLeftComponent(toolBarCommand);
		
		
		JLabel lblCommand = new JLabel("Command: ");
		toolBarCommand.add(lblCommand);
		
		
		comboBoxCommand = new JComboBox<String>();
		comboBoxCommand.setEditable(true);
		
		toolBarCommand.add(comboBoxCommand);
		comboBoxCommand.setFont(StyleManager.consolas12);
		
		readCommandHistory(comboBoxCommand);
		
		comboBoxCommand.getEditor().getEditorComponent().addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyPressed(KeyEvent e)
			{
			}
			
			@Override
			public void keyReleased(KeyEvent e){	}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
			    if(e.getKeyChar() == KeyEvent.VK_ENTER)
			    {
			    	processCommand();
			    	e.consume();
			    }
			}
		});
		
		
		final JButton btnExecute = new JButton(ActionType.EXECUTE.label);
		toolBarCommand.add(btnExecute);
		btnExecute.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		btnExecute.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				processCommand();
			}
		});
		
		
		JToolBar toolBarStatus = new JToolBar();
		toolBarStatus.setFloatable(false);
		splitPaneBottom.setRightComponent(toolBarStatus);
		
		JLabel lblStatus = new JLabel(" Status:  ");
		toolBarStatus.add(lblStatus);
		
		textFieldStatus = new JTextField();
		textFieldStatus.setEditable(false);
		textFieldStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textFieldStatus.setFont(StyleManager.calibri12);
		toolBarStatus.add(textFieldStatus);
		ControllerFactory.getStatusViewController().setView(textFieldStatus);
		
		/*Toolbars*/
		JToolBar tbMainToolbar = new JToolBar();
		tbMainToolbar.setFloatable(false);
		mainFrame.getContentPane().add(tbMainToolbar, BorderLayout.NORTH);		
		
	
		/*-----------Main Menu----------------*/
		JToolBar tbMainMenu = new JToolBar();
		tbMainMenu.setFloatable(false);
		tbMainToolbar.add(tbMainMenu);
		tbMainMenu.setToolTipText("Main Menu");
		
		JButton btnOpenLogFile = new JButton(ActionType.OPEN_LOG.label);
		tbMainMenu.add(btnOpenLogFile);	 
		btnOpenLogFile.addActionListener( ControllerFactory.getLogFileOpenDlgController());
		btnOpenLogFile.setToolTipText(ActionType.OPEN_LOG.tooltip);
		
/*		JButton btnOpenFiles = new JButton(ActionType.OPEN_LOGS.label);
		tbMainMenu.add(btnOpenFiles);	 
		btnOpenFiles.addActionListener( ControllerFactory.getLogFilesOpenDlgController());
		btnOpenFiles.setToolTipText(ActionType.OPEN_LOGS.tooltip);*/
		
		
		JButton btnReloadLog = new JButton(ActionType.RELOAD_LOG.label);
		btnReloadLog.addActionListener(ControllerFactory.getMenuBarController());
		tbMainMenu.add(btnReloadLog);
		btnReloadLog.setToolTipText(ActionType.RELOAD_LOG.tooltip);
		
		
		JButton btnFilter = new JButton(ActionType.FILTER.label);
		tbMainMenu.add(btnFilter);
		btnFilter.addActionListener(ControllerFactory.getFilterDlgController());
		btnFilter.setToolTipText(ActionType.FILTER.tooltip);
		
	
		JButton btnExport = new JButton(ActionType.EXPORT.label);
		tbMainMenu.add(btnExport);
		btnExport.addActionListener(ControllerFactory.getFileSaveDlgController());
		btnExport.setToolTipText(ActionType.EXPORT.tooltip);
		
		
		/*------------Actions-----------------*/		
		JToolBar tbActions = new JToolBar();
		tbActions.setFloatable(true);
		tbMainToolbar.add(tbActions);
		tbActions.setToolTipText("Actions toolbar");
		
		{
			JButton btnFind = new JButton(ActionType.FIND.label);
			btnFind.addActionListener(ControllerFactory.getMenuBarController());
			tbActions.add(btnFind);
			btnFind.setToolTipText(ActionType.FIND.tooltip);
		}
		
/*		JButton btnAddView = new JButton(ActionType.ADD_VIEW.label);		
		tbActions.add(btnAddView);
		btnAddView.setToolTipText(ActionType.ADD_VIEW.tooltip);
		btnAddView.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				addFilterView();
			}
		});*/
		
		{
			JButton btnClear = new JButton(ActionType.CLEAR_VIEW.label);
			btnClear.addActionListener(ControllerFactory.getMenuBarController());
			tbActions.add(btnClear);
			btnClear.setToolTipText(ActionType.CLEAR_VIEW.tooltip);
		}
		
		{
			JButton btnRefresh = new JButton(ActionType.REFRESH_VIEW.label);
			btnRefresh.addActionListener(ControllerFactory.getMenuBarController());
			tbActions.add(btnRefresh);
			btnRefresh.setToolTipText(ActionType.REFRESH_VIEW.tooltip);
		}

		{
			JButton btnClose = new JButton(ActionType.CLOSE_VIEW.label);
			btnClose.addActionListener(ControllerFactory.getMenuBarController());
			tbActions.add(btnClose);
			btnClose.setToolTipText(ActionType.CLOSE_VIEW.tooltip);
		}
		
		{
			JToolBar tbHelp = new JToolBar();
			tbHelp.setFloatable(true);
			tbMainToolbar.add(tbHelp);
			tbHelp.setToolTipText("Help");
		
			JButton btnHelp = new JButton(ActionType.HELP.label);
			tbHelp.add(btnHelp);
			btnHelp.addActionListener(ControllerFactory.getMenuBarController());
		}
		
		

		/*VIEW TABS***************************************/
		mainTab = new JTabbedPane(JTabbedPane.TOP);
		mainFrame.getContentPane().add(mainTab, BorderLayout.CENTER);
		
		JPopupMenu mainPopup = new JPopupMenu();
		{
			JMenuItem menuItem = new JMenuItem(ActionType.COPY.label);
			menuItem.addActionListener(ControllerFactory.getMenuBarController());
			mainPopup.add(menuItem);
			
			menuItem = new JMenuItem(ActionType.FIND.label);
			menuItem.addActionListener(ControllerFactory.getMenuBarController());
			mainPopup.add(menuItem);
		}

		/*LOG VIEW***************************************/
		JTextPane textPaneLogView = new JTextPane();
		textPaneLogView.setName(ViewName.LOG_VIEW.label);
		addTab(textPaneLogView);
		ControllerFactory.creatLogViewController(textPaneLogView);
		textPaneLogView.setFont(StyleManager.getInstance().getDefaultFont());
		textPaneLogView.setEditable(false);
		textPaneLogView.setEnabled(true);
		textPaneLogView.getCaret().setVisible(true);
		textPaneLogView.setComponentPopupMenu(mainPopup);
		
		
		/*ERROR VIEW*****************************************/
		JTextArea textAreaError = new JTextArea();
		textAreaError.setName(ViewName.ERROR_VIEW.label);
		textAreaError.setEditable(true);
		addTab(textAreaError);
		ControllerFactory.createErrorViewController(textAreaError);
		textAreaError.setFont(StyleManager.getInstance().getDefaultFont());
		textAreaError.setComponentPopupMenu(mainPopup);
		
		/*CONSOLE VIEW****************************************/
		JTextArea textAreaHistoryView = new JTextArea();
		textAreaHistoryView.setName(ViewName.HISTORY_VIEW.label);
		textAreaHistoryView.setEditable(true);
		addTab(textAreaHistoryView);
		ControllerFactory.getHistoryViewController().setView(textAreaHistoryView);
		textAreaHistoryView.setFont(StyleManager.getInstance().getDefaultFont());
		
		
		
		/*-------options----------------*/
		JToolBar tbOptions = new JToolBar();
		tbOptions.setFloatable(true);
		tbMainToolbar.add(tbOptions);
		tbOptions.setToolTipText("Options toolbar");
		
		cbPause = new JCheckBox(ActionType.PAUSE.label);
		cbPause.setSelected(false);
		tbOptions.add(cbPause);
		cbPause.addActionListener(ControllerFactory.getOptionBarController());
		cbPause.setToolTipText(ActionType.PAUSE.tooltip);		

		
		JCheckBox cbTail = new JCheckBox(ActionType.TAIL.label);
		cbTail.addActionListener(ControllerFactory.getOptionBarController());
		tbOptions.add(cbTail);
		cbTail.setToolTipText(ActionType.TAIL.tooltip);
		
		cbWrap = new JCheckBox(ActionType.WRAP.label);
		cbWrap.setSelected(DefaultProperties.isWrapLine());
		tbOptions.add(cbWrap);
		cbWrap.addActionListener(ControllerFactory.getOptionBarController());
		cbWrap.setToolTipText(ActionType.WRAP.tooltip);
		
		cbAutoScroll = new JCheckBox(ActionType.AUTO_SCROLL.label);
		cbAutoScroll.addActionListener(ControllerFactory.getOptionBarController());
		tbOptions.add(cbAutoScroll);
		cbAutoScroll.setToolTipText(ActionType.TAIL.tooltip);
		
		
		JCheckBox cbNoDup = new JCheckBox(ActionType.NO_DUPLICATES.label);
		cbNoDup.setSelected(DefaultProperties.isFilterDuplicate());
		tbOptions.add(cbNoDup);
		ControllerFactory.getLogViewController().setNoDuplicates(cbNoDup.isSelected());
		cbNoDup.addActionListener(ControllerFactory.getOptionBarController());
		cbNoDup.setToolTipText(ActionType.NO_DUPLICATES.tooltip);
		
		
		cbFold = new JCheckBox(ActionType.FOLD.label);
		cbFold.setSelected(true);
		tbOptions.add(cbFold);
		cbFold.addActionListener(ControllerFactory.getOptionBarController());
		cbFold.setToolTipText(ActionType.FOLD.tooltip);		
		
       /****************************/		
		mainFrame.setExtendedState(java.awt.Frame.MAXIMIZED_HORIZ);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		showView(ViewName.LOG_VIEW);
		mainFrame.setIconImage(SystemUtil.getIcon());//
		
		JToolBar tbContect = new JToolBar();
		tbContect.setFloatable(true);
		tbMainToolbar.add(tbContect);
		JTextField txtField = new JTextField(contactInfo);
		txtField.setEditable(false);
		txtField.setHorizontalAlignment(JTextField.RIGHT);
		tbContect.add(txtField);
	}
	
	
	private void addTab(JTextComponent view)
	{
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setName(view.getName());
		scrollPane.setViewportView(view);
		scrollPane.setEnabled(true);
		Component tabComponent = mainTab.add(scrollPane);
		//tabNames.add(scrollPane.getName());
		tabViews.put(view.getName(), view);
		tabComponents.put(view.getName(), tabComponent);
	}

	public JTextComponent creatFilterView()
    {
		StringBuilder sb = new StringBuilder();
		sb.append(ViewName.FILTER_VIEW.label)
		.append("-")
		.append(++tabId);
		
		JTextArea ta = new JTextArea();
		ta.setName(sb.toString());
		ta.setEditable(true);
		ta.setEnabled(true);
		addTab(ta);
		
		ta.setFont(StyleManager.getInstance().getDefaultFont());
		
		JPopupMenu popup = new JPopupMenu();
		{
			
			/*************  CLOSE **************************/
			JMenuItem menuItem = new JMenuItem(ActionType.CLOSE.label);
			menuItem.setName(ta.getName());
			
			menuItem.addActionListener(ControllerFactory.getCommandController());
			popup.add(menuItem);
			
			/*************  Copy **************************/			
			menuItem = new JMenuItem(ActionType.COPY.label);
			menuItem.addActionListener(ControllerFactory.getMenuBarController());
			popup.add(menuItem);
			
			/*************  FIND **************************/
			menuItem = new JMenuItem(ActionType.FIND.label);
			menuItem.setName(ta.getName());
			
			menuItem.addActionListener(ControllerFactory.getMenuBarController());
			popup.add(menuItem);
			
		}

		ta.setComponentPopupMenu(popup);
		
		return ta;
    }

	public void removeTab(String viewName)
    {
		Component component = tabComponents.get(viewName);
		
		if (component!=null)
		{
			showView(ViewName.LOG_VIEW);
			mainTab.remove(component);
			tabViews.get(viewName).setText("");
			tabViews.get(viewName).removeAll();
			tabViews.remove(viewName);

			tabComponents.remove(viewName);
		}
    }

	public Object isAutoScrollSelected()
    {
	    return cbAutoScroll.isSelected();
    }
	
	public boolean isPauseSelected()
    {
	    return cbPause.isSelected();
    }

	public Object isWrapSelected()
    {
	    return cbWrap.isSelected();
    }
	
	
	protected void processCommand()
    {
		String command = ((JTextComponent)comboBoxCommand.getEditor().getEditorComponent()).getText();
    	ControllerFactory.getCommandController().actionPerformed(new ActionEvent(command,1,ActionType.EXECUTE.label));
    	
    	if (!commandHistory.contains(command))
    	{
    		commandHistory.add(command);
    		comboBoxCommand.addItem(command);
    		FileUtil.append(SystemUtil.getCommandHistoryFile(), command);
    	}
    }

	private void readCommandHistory(JComboBox<String> comboBoxCommand)
    {
	   this.commandHistory = FileUtil.readFile(SystemUtil.getCommandHistoryFile());
	   comboBoxCommand.addItem("");

	   for (String line: commandHistory)
	   {
		   comboBoxCommand.addItem(line);
	   }
    }

	public void setAutoScrollSelected(boolean selected)
    {
		cbAutoScroll.setSelected(selected);
    }

	public void setPauseSelected(boolean selected)
    {
	    this.cbPause.setSelected(selected);
    }

	
	
	/*******************************************************
	 * 
	 *******************************************************/	
	public void setViewTitle(ViewName viewName, String title)
	{
		setViewTitle(viewName.label, title);
	}

	public void showView(ViewName viewName) 
	{
		showView(viewName.label);
	}

	
	
	/*******************************************************
	 * 
	 *******************************************************/
	public void setViewTitle(String viewName, String title)
	{
		int index = getTabIndex(viewName);
		
		if (index >= 0)
			mainTab.setTitleAt(index, title);
		
	}

	public void showView(String viewName) 
	{
		Component component = tabComponents.get(viewName);
		
		if (component!=null)
		{
			this.mainTab.setSelectedComponent(component);
		}
	}
	
	
	private int getTabIndex(String viewName)
	{
		Component component = tabComponents.get(viewName);
		
		if (component!=null)
		{
			return this.mainTab.indexOfComponent(component);
		}
		
		return -1;
	}

	public void setToolTip(String viewName, String toolTipText)
    {
		int index = getTabIndex(viewName);
		
		if (index>=0)
		{
			mainTab.setToolTipTextAt(index, toolTipText);
		}
    }

	public Object isFoldSelected()
    {
	    return cbFold.isSelected();
    }
}
