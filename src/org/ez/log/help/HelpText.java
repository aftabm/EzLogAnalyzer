/**
 * Copyright � 2013 Aftab Mahmood
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details <http://www.gnu.org/licenses/>.
 **/
package org.ez.log.help;

import org.ez.log.view.MainView;

public class HelpText
{

	public static String getData()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("").append("\n");
		sb.append(MainView.getInstance().getMainFrame().getTitle()).append("\n");		
		sb.append("").append("\n");
		sb.append("Please read 'ezLogAnalyzer/doc/user_manual.pdf' for details").append(System.lineSeparator());
		sb.append("For question, bugs and enhancments, email to: aftab.mahmood@citrix.com").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("Read Log: Shows file selection dialog to select log(s) files.").append("\n");
		sb.append("").append("\n");
		sb.append("Create Filter: Shows filter creation dialog. Using this dialog filter can be stored or loaded from a filter file.").append("\n");
		sb.append("").append("\n");
		sb.append("Export Views:  Shows file save dialog. Application will save “Filter, Error and Console” views into a text files.").append("\n");
		sb.append("").append("\n");
		sb.append("Help: Shows a help dialog.").append("\n");
		sb.append("").append("\n");
		sb.append("Reload Log: Clears  the Log View and re-reads last log file.").append("\n");
		sb.append("").append("\n");
		sb.append("Clear View: Clears the selected view. Removes all text from view.").append("\n");
		sb.append("").append("\n");
		sb.append("Refresh View: Removes all highlights and inserts data from the buffer.").append("\n");
		sb.append("").append("\n");		
		sb.append("Tail: Keep scanning last log file for updates.").append("\n");
		sb.append("").append("\n");
		sb.append("Wrap: Wraps line in all views except “Log View”").append("\n");
		sb.append("").append("\n");
		sb.append("Auto Scroll: If selected application will move cart to last line.  This option is dependent on “refresh_rate” setting.").append("\n");
		sb.append("").append("\n");
		sb.append("Remove Duplicates: If this option is selected then system will skip line that contains the same message as previous line.").append("\n");
		sb.append("").append("\n");
		sb.append("Log View: Displays content of log file being read. Errors will be shown in RED color and filtered text in BLUE.").append("\n");
		sb.append("").append("\n");
		sb.append("Filter View: 	If a filter is defined either through filter dialog or through command window then qualified log lines will be shown in this view.  You can double click a line to see the context in log view.").append("\n");
		sb.append("").append("\n");
		sb.append("").append("\n");
		sb.append("Commands:").append("\n");
		sb.append("").append("\n");
		sb.append("You can type (or selct from combo box) any of following commands:").append("\n");
		sb.append("").append("\n");
		sb.append("   ").append("findall(word) -  Highlights matching words.").append(System.lineSeparator());
		sb.append("   ").append("findall(word, Highlighter color) - Highlights matching words with the given color.").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("filter(word {and}|{or}|{not} word;) - Creats a keyword filter.").append(System.lineSeparator());
		sb.append("      ").append("More than one statments can be combined with {and}|{or}|{not}. e.g ").append(System.lineSeparator());
		sb.append("      ").append("word {and}|{or}|{not} word; {and}|{or}|{not} word {and}|{or}|{not} word.").append(System.lineSeparator());
		sb.append("      ").append("';' marks end of a statement.").append(System.lineSeparator());
		sb.append("      ").append("One statment can have one type of operator only. e.g").append(System.lineSeparator());
		sb.append("      ").append("word {or} word {or} word; {not} word {and} word;").append(System.lineSeparator());
		sb.append("      ").append("In the above example {not} will be applied to the result of (word {and} word);").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("groupby(thread) -  Will rearrange log lines in filter view such that lines with same threadname are togather.").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("help").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("recentonly(message) -  Will remove older log line that has same message.").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("remove(word) - Removes matching lines.").append(System.lineSeparator());
		sb.append("").append("\n");
		sb.append("   ").append("repalce(word, word)").append(System.lineSeparator());

		return sb.toString();
	}
}
