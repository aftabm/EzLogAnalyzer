package org.ez.log.command;

import javax.swing.JOptionPane;

import org.ez.log.om.CommandType;

public class HelpCommand extends Command
{

	public HelpCommand()
    {
	    super(CommandType.help);
    }

	@Override
    public boolean execute()
    {
		StringBuilder sb = new StringBuilder();
		sb.append("Available commands are:").append(System.lineSeparator());
		sb.append("   ").append("findall(word) -  Highlights matching words.").append(System.lineSeparator());
		sb.append("   ").append("findall(word, Highlighter Color) -  Highlights matching words with the given color.").append(System.lineSeparator());
		sb.append("   ").append("filter(word {and}|{or}|{not} word;) - Creats a keyword filter.").append(System.lineSeparator());
		sb.append("   ").append("groupby(thread) -  Will rearrange log lines such that lines with same thread are togather.").append(System.lineSeparator());
		sb.append("   ").append("help").append(System.lineSeparator());
		sb.append("   ").append("recent(message) -  Will remove older log line from Log View that has the same message.").append(System.lineSeparator());
		sb.append("   ").append("remove(word) - Removes matching lines.").append(System.lineSeparator());
		sb.append("   ").append("repalce(word, word)").append(System.lineSeparator());
        JOptionPane.showConfirmDialog (mainView.getMainFrame(), sb.toString(),"CommandFactory Help",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);        
	    return false;
    }

}