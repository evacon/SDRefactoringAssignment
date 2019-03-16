package ra;

/*
 * 
 * This is a dialog for adding new Employees and saving records to file
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class AddRecordDialog extends JDialog implements ActionListener {
	
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JButton save, cancel;
	private EmployeeDetails parent;
	EmployeeDetailsValidation edv = new EmployeeDetailsValidation();

	public AddRecordDialog(EmployeeDetails parent) {
		
		setTitle("Add Record");
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);
		
		getRootPane().setDefaultButton(save);
		
		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}

	public Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
		JTextField field;
		String gp = "growx, pushx";
		String gpw = "growx, pushx, wrap";

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), gp);
		empDetails.add(idField = new JTextField(20), gpw);
		idField.setEditable(false);
		

		empDetails.add(new JLabel("PPS Number:"), gp);
		empDetails.add(ppsField = new JTextField(20), gpw);

		empDetails.add(new JLabel("Surname:"), gp);
		empDetails.add(surnameField = new JTextField(20), gpw);

		empDetails.add(new JLabel("First Name:"), gp);
		empDetails.add(firstNameField = new JTextField(20), gpw);

		empDetails.add(new JLabel("Gender:"), gp);
		empDetails.add(genderCombo = new JComboBox<String>(this.parent.gender), gpw);

		empDetails.add(new JLabel("Department:"), gp);
		empDetails.add(departmentCombo = new JComboBox<String>(this.parent.department), gpw);

		empDetails.add(new JLabel("Salary:"), gp);
		empDetails.add(salaryField = new JTextField(20), gpw);

		empDetails.add(new JLabel("Full Time:"), gp);
		empDetails.add(fullTimeCombo = new JComboBox<String>(this.parent.fullTime), gpw);

		buttonPanel.add(save = new JButton("Save"));
		save.addActionListener(this);
		save.requestFocus();
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);

		empDetails.add(buttonPanel, "span 2, " + gpw );
		
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			
			empDetails.getComponent(i).setFont(this.parent.font1);
			
			if (empDetails.getComponent(i) instanceof JComboBox) {
				
				empDetails.getComponent(i).setBackground(Color.WHITE);
			}
			
			else if(empDetails.getComponent(i) instanceof JTextField){
				field = (JTextField) empDetails.getComponent(i);
				if(field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
				field.setDocument(new JTextFieldLimit(20));
			}
		}
		
		idField.setText(Integer.toString(this.parent.getNextFreeId()));
		return empDetails;
	}

	public void addRecord() {
		
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;
		
		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(),
				firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0),
				departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		this.parent.currentEmployee = theEmployee;
		this.parent.addRecord(theEmployee);
		this.parent.displayRecords(theEmployee);
	}

	public boolean checkInput() {
		
		boolean valid = true;
		final Color bg = new Color(255, 150, 150);
		
		if (ppsField.getText().equals("")) {
			ppsField.setBackground(bg);
			valid = false;
		}
		if (edv.correctPps(this.ppsField.getText().trim(), -1)) {
			ppsField.setBackground(bg);
			valid = false;
		}
		if (surnameField.getText().isEmpty()) {
			surnameField.setBackground(bg);
			valid = false;
		}
		if (firstNameField.getText().isEmpty()) {
			firstNameField.setBackground(bg);
			valid = false;
		}
		if (genderCombo.getSelectedIndex() == 0) {
			genderCombo.setBackground(bg);
			valid = false;
		}
		if (departmentCombo.getSelectedIndex() == 0) {
			departmentCombo.setBackground(bg);
			valid = false;
		}
		
		try {
			
			Double.parseDouble(salaryField.getText());
			
			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(bg);
				valid = false;
			}
		}
		catch (NumberFormatException num) {
			salaryField.setBackground(bg);
			valid = false;
		}
		if (fullTimeCombo.getSelectedIndex() == 0) {
			fullTimeCombo.setBackground(bg);
			valid = false;
		}
		return valid;
	}

	public void setToWhite() {
		
		ppsField.setBackground(Color.WHITE);
		surnameField.setBackground(Color.WHITE);
		firstNameField.setBackground(Color.WHITE);
		salaryField.setBackground(Color.WHITE);
		genderCombo.setBackground(Color.WHITE);
		departmentCombo.setBackground(Color.WHITE);
		fullTimeCombo.setBackground(Color.WHITE);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == save) {

			if (checkInput()) {
				addRecord();
				dispose();
				this.parent.changesMade = true;
			}
			
			else {
				JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
				setToWhite();
			}
		}
		else if (e.getSource() == cancel)
			dispose();
	}
}