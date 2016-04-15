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
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.ez.log.command.FindCommand;
import org.ez.log.help.HelpDlg;
import org.ez.log.om.ActionType;
import org.ez.log.util.ConsoleLogger;
import org.ez.log.view.MainView;
import org.ez.log.view.MainView.ViewName;

public class MenuController extends Controller implements ActionListener
{
	//String command;
	
	//private JTextField view;
	private static final ConsoleLogger<MenuController> logger = ConsoleLogger.create(MenuController.class);
	private static HelpDlg helpDlg =null;
	
	@Override
	public void actionPerformed(final ActionEvent e) 
	{
		Thread workerThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ActionType actionType = ActionType.parse(e.getActionCommand());
				
				switch(actionType)
				{
					case OPEN_LOG:
						logger.error("actionPerformed", "Incorrect call. Should use FileOpenDlgController");
						break;
						
					case FILTER:
						logger.error("actionPerformed", "Incorrect call. Should use FilterDlgController");
						break;
						
					case CLOSE_VIEW:
						
						String viewName = MainView.getInstance().getCurrentViewName();
						
						if (viewName.equals(ViewName.LOG_VIEW.label) || viewName.equals(ViewName.ERROR_VIEW.label) || viewName.equals(ViewName.HISTORY_VIEW.label))
						{
							logger.info("actionPerformed: closeview", "This view is not closeable. "+viewName);
							return;
						}
						
						int result =  JOptionPane.showConfirmDialog (MainView.getInstance().getMainFrame(), "Do you want to close "+MainView.getInstance().getCurrentViewName()+" ?","Warning",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		                if(result == JOptionPane.YES_OPTION)
		                {
		                	ViewController controller = ControllerFactory.getViewController(viewName);
		                	ActionEvent action = new  ActionEvent(controller.getView(), 0, ActionType.CLOSE.name());
		                	ControllerFactory.getCommandController().actionPerformed(action);
		                }
						
						break;
						
					case CLEAR_VIEW:

						result =  JOptionPane.showConfirmDialog (MainView.getInstance().getMainFrame(), "Do you want to clear "+MainView.getInstance().getCurrentViewName()+" ?","Warning",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		                if(result == JOptionPane.YES_OPTION)
		                {
		                	ViewController controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
		                	
		                	if (controller!=null)
		                		controller.clearView();
		                }
		                
						break;
						
					case REFRESH_VIEW:
						
						result =  JOptionPane.showConfirmDialog (MainView.getInstance().getMainFrame(), "Do you want to refresh "+MainView.getInstance().getCurrentViewName()+" ?","Warning",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		                if(result == JOptionPane.YES_OPTION)
		                {
		                	ViewController controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
		                	
		                	if (controller!=null)
		                		controller.refreshView();
		                }
						
						break;

					case RELOAD_LOG:
						ControllerFactory.getLogViewController().doReloadLog();
						break;
						
					case EXPORT:
						logger.error("actionPerformed", "Incorrect call. Should use FileSaveDlgController");
						break;
						
					case HELP:
						if (helpDlg == null)
							helpDlg = new HelpDlg();
						
						helpDlg.setVisible(true);				
						break;
							
					case FIND:
						ViewController controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
						
						if(controller!=null)
						{
							FindCommand cmd = new FindCommand();
							cmd.setInputController(controller);
							cmd.setOutputController(controller);
							
							cmd.asyncExecute();
						}

						break;
						
					case COPY:
						controller = ControllerFactory.getViewController(MainView.getInstance().getCurrentViewName());
						
						if(controller!=null)
						{
							controller.doCopy();
						}
						
						
					default:
							break;
				}
				
			}
		});
		
		workerThread.setName("MBC-ACTION-THREAD");
		workerThread.setDaemon(true);
		workerThread.run();
	}

}
