package org.ez.log.view;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindAndReplaceDlg extends JDialog {
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
	private JTextField textFieldFind;
	private String textToFind;
	private JTextField textFieldReplacement;
	private JButton btnNext;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FindAndReplaceDlg dialog = new FindAndReplaceDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FindAndReplaceDlg() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setAlwaysOnTop(true);
		setTitle("Find and replace");
		setBounds(100, 100, 331, 169);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 276, 0};
		gridBagLayout.rowHeights = new int[]{96, 0, 0, 33, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		
		getContentPane().setLayout(gridBagLayout);
		{
			JLabel lblNewLabel = new JLabel("Text to find:");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			textFieldFind = new JTextField();
			GridBagConstraints gbc_textFieldFind = new GridBagConstraints();
			gbc_textFieldFind.insets = new Insets(0, 0, 5, 0);
			gbc_textFieldFind.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldFind.gridx = 1;
			gbc_textFieldFind.gridy = 0;
			getContentPane().add(textFieldFind, gbc_textFieldFind);
			textFieldFind.setColumns(10);
			textFieldFind.setFocusable(true);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Replace With:");
			lblNewLabel_1.setEnabled(false);
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 0;
			gbc_lblNewLabel_1.gridy = 1;
			getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			textFieldReplacement = new JTextField();
			textFieldReplacement.setEditable(false);
			textFieldReplacement.setEnabled(false);
			GridBagConstraints gbc_textFieldReplacement = new GridBagConstraints();
			gbc_textFieldReplacement.insets = new Insets(0, 0, 5, 0);
			gbc_textFieldReplacement.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldReplacement.gridx = 1;
			gbc_textFieldReplacement.gridy = 1;
			getContentPane().add(textFieldReplacement, gbc_textFieldReplacement);
			textFieldReplacement.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 1;
			gbc_buttonPane.gridy = 3;
			getContentPane().add(buttonPane, gbc_buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						onOk(e);
						
					}});
				{
					JButton btnReplace = new JButton("Replace");
					btnReplace.setEnabled(false);
					buttonPane.add(btnReplace);
				}
				{
					btnNext = new JButton("Next");
					buttonPane.add(btnNext);
				}
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	public void addNextListner(ActionListener actionListner)
	{
		btnNext.addActionListener(actionListner);
	}

	protected void onOk(ActionEvent e) 
	{
		textToFind= textFieldFind.getText();
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

	public String getReplacement() {
		return this.textFieldReplacement.getText();
	}


}
