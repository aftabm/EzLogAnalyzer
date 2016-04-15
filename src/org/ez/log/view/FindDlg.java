package org.ez.log.view;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.ComponentOrientation;
import javax.swing.border.EmptyBorder;
import java.awt.Font;

public class FindDlg extends JDialog {
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
	private JTextField textFieldFind;
	private JButton btnNext;
	private JLabel lblTextToSearch;
	private JPanel panel;
	private JPanel panel_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FindDlg dialog = new FindDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FindDlg() {
		getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setAlwaysOnTop(true);
		setTitle("Find");
		setBounds(100, 100, 331, 169);
		getContentPane().setLayout(new GridLayout(3, 1, 0, 10));
		{
			lblTextToSearch = new JLabel("Text to find:");
			lblTextToSearch.setVerticalAlignment(SwingConstants.BOTTOM);
			getContentPane().add(lblTextToSearch);
		}
		{
			textFieldFind = new JTextField();
			textFieldFind.setFont(new Font("Consolas", Font.PLAIN, 14));
			textFieldFind.setHorizontalAlignment(SwingConstants.LEFT);
			getContentPane().add(textFieldFind);
			textFieldFind.setColumns(10);
			textFieldFind.setFocusable(true);
			textFieldFind.setBorder(new LineBorder(new Color(0, 0, 0)));
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(0, 0, 0, 0));
			getContentPane().add(buttonPane);
			{
				buttonPane.setLayout(new GridLayout(0, 4, 10, 0));
			}
			{
				panel = new JPanel();
				buttonPane.add(panel);
			}
			{
				btnNext = new JButton("Next");
				buttonPane.add(btnNext);
			}
			JButton btnClose = new JButton("Close");
			//btnClose.setActionCommand("OK");
			btnClose.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					onClose(e);
					
				}});
			buttonPane.add(btnClose);
			getRootPane().setDefaultButton(btnNext);
			{
				panel_1 = new JPanel();
				buttonPane.add(panel_1);
			}
			
			//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
	}
	
	public void addNextListner(ActionListener actionListner)
	{
		btnNext.addActionListener(actionListner);
	}

	protected void onClose(ActionEvent e) 
	{
		textFieldFind.getText();
		this.setVisible(false);
	}
	
	public String getText()
	{
		return this.textFieldFind.getText();
	}

	public void setText(String text) 
	{
		this.textFieldFind.setText(text);
	}


}
