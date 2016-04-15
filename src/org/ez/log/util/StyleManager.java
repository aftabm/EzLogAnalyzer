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
package org.ez.log.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.ez.log.util.DefaultProperties.PropertyName;


public class StyleManager
{
	
	public static final Font consolas12 = new Font("Consolas", Font.PLAIN, 12);
	public static final Font consolas10 = new Font("Consolas", Font.PLAIN, 10);
	public static final Font calibri12  =  new Font("Calibri", Font.PLAIN, 12);
	public static final Font calibri10 = new Font("Calibri", Font.PLAIN, 10);
	
	private Font defaultFont=null;

	private HighlightPainter defaultHighlightPainter=null;
	private HighlightPainter filterHighlightPainter;
	
	private DefaultHighlightPainter findHighlightPainter;
	private DefaultHighlightPainter contextLineHighlightPainter;
	
	private static StyleManager instance =null;
	
	private SimpleAttributeSet defaultParagraphAttribute=null;
	private SimpleAttributeSet errorParagraphAttribute=null;
	private SimpleAttributeSet compliantParagraphAttribute=null;
	private DefaultHighlightPainter errorHighlightPainter;
	
	public static StyleManager getInstance()
	{
		if (instance==null)
		{
			synchronized(StyleManager.class)
			{
				if (instance==null)
				{
					instance = new StyleManager();
				}
			}
		}
		
		return instance;
	}

	
	private StyleManager()
	{
		createDefaultParagraphAttribute();
		createCompliantParagraphAttribute();
		createErrorParagraphAttribute();
		
		defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		findHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
		contextLineHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		filterHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
		errorHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
		
		try
		{
			String name = DefaultProperties.get(PropertyName.font_name);
			String style = DefaultProperties.get(PropertyName.font_style);
			String size = DefaultProperties.get(PropertyName.font_size);
			
			defaultFont = new Font (name, Integer.parseInt(style), Integer.parseInt(size));
		}
		catch(Exception e)
		{
			defaultFont=consolas12;
		}
	}

	private void createCompliantParagraphAttribute()
	{
		compliantParagraphAttribute = new SimpleAttributeSet();
		//StyleConstants.setSpaceAbove(compliantParagraphAttribute, 24f);
		//StyleConstants.setSpaceBelow(compliantParagraphAttribute, 24f);
		StyleConstants.setBold(compliantParagraphAttribute, true);
		StyleConstants.setForeground(compliantParagraphAttribute, Color.BLUE);
		//StyleConstants.setBackground(compliantParagraphAttribute, Color.CYAN);
	}
	private void createDefaultParagraphAttribute()
	{
		defaultParagraphAttribute = new SimpleAttributeSet();
		//StyleConstants.setSpaceBelow(att, 12f);
		//StyleConstants.setBackground(att, Color.RED);
	}
	
	private void createErrorParagraphAttribute()
	{
		errorParagraphAttribute = new SimpleAttributeSet();
		StyleConstants.setSpaceAbove(errorParagraphAttribute, 240f);
		StyleConstants.setSpaceBelow(errorParagraphAttribute, 240f);
		//StyleConstants.setBackground(att, Color.YELLOW);
		StyleConstants.setBold(errorParagraphAttribute, true);
		StyleConstants.setForeground(errorParagraphAttribute, Color.RED);
	}		
	public AttributeSet getCompliantParagraphAttribute()
	{
		return compliantParagraphAttribute;
	}
	
/*	public SimpleAttributeSet getErrorParagraphAttribute(final AttributeSet attributes)
	{
		if (attributes==null)
			return this.errorParagraphAttribute;
		
		SimpleAttributeSet out  = new SimpleAttributeSet(attributes);
		StyleConstants.setBold(out, true);
		StyleConstants.setForeground(out, Color.RED);
		
		return out;
	}
	
	public SimpleAttributeSet getCompliantParagraphAttribute(final AttributeSet attributes)
	{
		if (attributes==null)
			return this.compliantParagraphAttribute;
		
		SimpleAttributeSet out = new SimpleAttributeSet(attributes);
		StyleConstants.setBold(out, true);
		
		return out;
	}*/	
	
	
	
	public HighlightPainter getContextLineHighlightPainter()
	{
		return this.contextLineHighlightPainter;
	}

	public Font getDefaultFont()
	{
		return defaultFont;
	}
	
	public HighlightPainter getDefaultHighlightPainter()
	{
		return defaultHighlightPainter;
	}
	
	
	public AttributeSet getDefaultParagraphAttribute()
	{
		return defaultParagraphAttribute;
	}
	
	public AttributeSet getErrorParagraphAttribute()
	{
		return errorParagraphAttribute;
	}	


	public HighlightPainter getFilterHighlightPainter()
    {
	    return this.filterHighlightPainter;
    }
	
	public HighlightPainter getFindHighlightPainter()
	{
		return this.findHighlightPainter;
	}

	public Border getMainMenuButtonBorder() 
	{
		return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}


	public HighlightPainter getErrorHighlightPainter()
    {
	    return errorHighlightPainter;
    }
		
}
