package org.ez.log.command;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;

import org.ez.log.om.CommandType;
import org.ez.log.util.StyleManager;
import org.ez.log.util.UiUtil;
import org.ez.log.view.FindDlg;
import org.ez.log.view.MainView;

public class FindCommand extends Command implements ActionListener 
{

	private Matcher matcher=null;
	private String textToFind="";
	private FindDlg dlg=null;
	
	public FindCommand()
    {
	    super(CommandType.find);
    }

	@Override
	public boolean execute()
	{
		dlg = new FindDlg();
		dlg.addNextListner(this);
		
		dlg.setModalityType(ModalityType.APPLICATION_MODAL);
		dlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	
		dlg.setLocation(UiUtil.getCenterLocation(MainView.getInstance().getMainFrame(), dlg));
		dlg.setVisible(true);
		
		return false;
	}

	@Override
    public void actionPerformed(ActionEvent e)
    {
		Matcher currentMatcher = getMatcher(dlg.getText());
		
		if (currentMatcher!=null && currentMatcher.find())
		{
			outputController.removeHighlights(StyleManager.getInstance().getFindHighlightPainter());
			
			int start = currentMatcher.start();
			int end = currentMatcher.end();

			outputController.highlight(start, end, StyleManager.getInstance().getFindHighlightPainter());
			outputController.getView().setCaretPosition(end);
		}

    }

	private Matcher getMatcher(String text)
    {
		if(matcher==null || !textToFind.equals(text))
		{
			String data = inputController.getText();
					
			if(data==null || data.isEmpty())
				return null;
			
			Pattern pattern = Pattern.compile(Pattern.quote(text),Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(data);
			textToFind=text;
		}
		
		return matcher;
    }

}
