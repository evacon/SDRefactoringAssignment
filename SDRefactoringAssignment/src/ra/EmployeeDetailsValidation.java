package ra;

import java.awt.Color;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class EmployeeDetailsValidation {
	
	private static RandomFile application = new RandomFile();
	private static File file;
	private static long currentByteStart = 0;
	private boolean change = false;
	Employee currentEmployee;
	JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	EmployeeDetails ed;
	
	public static boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
					&& Character.isDigit(pps.charAt(2))	&& Character.isDigit(pps.charAt(3)) 
					&& Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5)) 
					&& Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				application.openReadFile(file.getAbsolutePath());
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();
			}
			else
				ppsExist = true;
		}
		else
			ppsExist = true;

		return ppsExist;
	}

	public boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}

	public boolean checkForChanges() {
		boolean anyChanges = false;
		if (change) {
			ed.saveChanges();
			anyChanges = true;
		}
		else {
			ed.displayRecords(currentEmployee);
		}
		return anyChanges;
	}

	public boolean validateEmployeeDetails() {
		boolean valid = true;

		
		if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
			surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
			firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} 
		
		if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
			genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
			departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		try {

			Double.parseDouble(salaryField.getText());
			
			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} 
		} 

		catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} 
		} 

		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} 

		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		if (ppsField.isEditable())
			ed.setToWhite();

		return valid;
	}
}
