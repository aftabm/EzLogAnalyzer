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
package org.ez.log.help;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class HelpDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			HelpDlg dialog = new HelpDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public HelpDlg() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("Help");
		setBounds(100, 100, 582, 434);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setWheelScrollingEnabled(false);
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				JTextArea textArea = new JTextArea();
				textArea.setTabSize(3);
				textArea.setWrapStyleWord(true);
				textArea.setLineWrap(true);
				textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
				textArea.setEditable(false);
				scrollPane.setViewportView(textArea);
				textArea.setText(HelpText.getData());
				textArea.setCaretPosition(0);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() 
				{					
					@Override
					public void actionPerformed(ActionEvent e) {
						onOk(e);
					}
						
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setEnabled(false);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			
		}
		
		
	}

	protected void onOk(ActionEvent e) 
	{
		this.setVisible(false);
	}

}
