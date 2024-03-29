package ra;

/*
 * 
 * This is the dialog for Employee search by ID
 * 
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class SearchByIdDialog extends JDialog implements ActionListener {
	private EmployeeDetails parent;
	private JButton search, cancel;
	private JTextField searchField;

	//constructor
	public SearchByIdDialog(EmployeeDetails parent) {
		setTitle("Search by Id");
		setModal(true);
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(searchPane());
		setContentPane(scrollPane);

		getRootPane().setDefaultButton(search);
		
		setSize(500, 190);
		setLocation(350, 250);
		setVisible(true);
	}
	//initialize search container
	public Container searchPane() {
		JPanel searchPanel = new JPanel(new GridLayout(3, 1));
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JLabel searchLabel;

		searchPanel.add(new JLabel("Search by ID"));

		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(searchLabel = new JLabel("Enter ID:"));
		searchLabel.setFont(this.parent.font1);
		textPanel.add(searchField = new JTextField(20));
		searchField.setFont(this.parent.font1);
		searchField.setDocument(new JTextFieldLimit(20));
		
		buttonPanel.add(search = new JButton("Search"));
		search.addActionListener(this);
		search.requestFocus();
		
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);

		searchPanel.add(textPanel);
		searchPanel.add(buttonPanel);

		return searchPanel;
	}

	// action listener for save and cancel button
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == search) {
			
			try {
				Double.parseDouble(searchField.getText());
				this.parent.searchByIdField.setText(searchField.getText());
				
				this.parent.searchEmployeeById();
				dispose();
			}
			catch (NumberFormatException num) {
				searchField.setBackground(new Color(255, 150, 150));
				JOptionPane.showMessageDialog(null, "Wrong ID format!");
			}
		}
		
		else if (e.getSource() == cancel)
			dispose();
	}
}

