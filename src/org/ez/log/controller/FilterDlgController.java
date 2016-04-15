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

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ez.log.event.Event;
import org.ez.log.event.EventDataKey;
import org.ez.log.event.EventListener;
import org.ez.log.om.ActionType;
import org.ez.log.om.Filter;
import org.ez.log.util.UiUtil;
import org.ez.log.view.LogFilterEditorDlg;
import org.ez.log.view.MainView;

public class FilterDlgController extends  DialogController
{
	private LogFilterEditorDlg dlg =null;
	private Filter filter;
	private Map<Filter, FilteredViewController> filterToControllerMap = new HashMap<Filter, FilteredViewController>();
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ActionType actionType = ActionType.parse(e.getActionCommand());
		JFrame parent = MainView.getInstance().getMainFrame();
		
		switch(actionType)
		{
			case FILTER:
				
					if(dlg==null)
					{ 
						dlg = new LogFilterEditorDlg(parent, this);
						dlg.setModalityType(ModalityType.APPLICATION_MODAL);
						dlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					}
					
					dlg.setLocation(UiUtil.getCenterLocation(parent, dlg));
					dlg.setVisible(true);
					
					break;
					
			case OK:
				this.filter = dlg.getData();
				dlg.setVisible(false);
				
				FilteredViewController controller=null;
				
				if (filterToControllerMap.containsKey(filter))
					controller = filterToControllerMap.get(filter);
				else
				{
					controller =  ControllerFactory.creatFilteredViewController();
					
					controller.addDisposeListner(new EventListener()
					{
						@Override
						public void onEvent(Event event)
						{
							onControllerDisposed(event);
						}
					});
					
					controller.setFilter(filter);
					controller.registerForNewLog();
					
					MainView.getInstance().setToolTip(controller.getView().getName(), filter.toString());
					
					filterToControllerMap.put(filter, controller);
				}
			
				break;
			
			default:
					//TBD
		}
	}

	protected void onControllerDisposed(Event event)
    {
		Object controller = event.getData(EventDataKey.DISPOSED, Object.class);
		
		for(Entry<Filter, FilteredViewController> entry : filterToControllerMap.entrySet())
		{
			if (entry.getValue().equals(controller))
			{
				filterToControllerMap.remove(entry.getKey());
				break;
			}
		}
    }


}
