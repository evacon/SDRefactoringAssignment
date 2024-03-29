package ra;


/* 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;



import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	private long currentByteStart = 0;
	private RandomFile application = new RandomFile();
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	private File file;
	private boolean change = false;
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static EmployeeDetails frame = new EmployeeDetails();
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	String generatedFileName;
	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;
	String[] gender = { "", "M", "F" };
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	String[] fullTime = { "", "Yes", "No" };
	String gp = "growx, pushx";
	String gpw = "growx, pushx, wrap";
	EmployeeDetailsValidation edv = new EmployeeDetailsValidation();

	private JMenuBar menuBar() {
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}

	private JPanel searchPanel() {
		
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), gp);
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200, "+ gp);
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(searchId = new JButton(new ImageIcon(
				new ImageIcon("stuff/search.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, " + gpw);
		searchId.addActionListener(this);
		searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), gp);
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200, " + gp);
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchSurname = new JButton(new ImageIcon(new ImageIcon("stuff/search.png").getImage()
						.getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, " +gpw);
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText("Search Employee By Surname");

		return searchPanel;
	}

	private JPanel navigPanel() {
		
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("stuff/first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("stuff/prev.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(this);
		previous.setToolTipText("Display previous Record");

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("stuff/next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(this);
		next.setToolTipText("Display next Record");

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("stuff/last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(this);
		last.setToolTipText("Display last Record");

		return navigPanel;
	}

	private JPanel buttonPanel() {
		
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), gp);
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), gp);
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), gpw);
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), gp);
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}

	private JPanel detailsPanel() {
		
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

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
		empDetails.add(genderCombo = new JComboBox<String>(gender), gpw);

		empDetails.add(new JLabel("Department:"), gp);
		empDetails.add(departmentCombo = new JComboBox<String>(department), gpw);

		empDetails.add(new JLabel("Salary:"), gp);
		empDetails.add(salaryField = new JTextField(20), gpw);

		empDetails.add(new JLabel("Full Time:"), gp);
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), gpw);

		buttonPanel.add(saveChange = new JButton("Save"));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2, " + gpw);

		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			} 
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {

					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}
				});
			} 
		}
		return empDetails;
	}

	public void displayRecords(Employee thisEmployee) {
		
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");

		if (thisEmployee == null||thisEmployee.getEmployeeId() == 0) {
		} 
		else {
			
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			}
			found = false;
			while (!found && countDep < department.length - 1) {
				
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			} 
			
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(format.format(thisEmployee.getSalary()));
			
			if (thisEmployee.getFullTime() == true)
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}

	private void displayEmployeeSummaryDialog() {
		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}

	private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}

	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}

	// if any active record in file look for first record
	private void firstRecord() {
		
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			currentByteStart = application.getFirst();
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();
			
			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	private void previousRecord() {
		
		if (isSomeoneToDisplay()) {
			
			application.openReadFile(file.getAbsolutePath());
			currentByteStart = application.getPrevious(currentByteStart);
			currentEmployee = application.readRecords(currentByteStart);
			while (currentEmployee.getEmployeeId() == 0) {
				currentByteStart = application.getPrevious(currentByteStart);
				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	private void nextRecord() {
		
		if (isSomeoneToDisplay()) {
			
			application.openReadFile(file.getAbsolutePath());
			currentByteStart = application.getNext(currentByteStart);
			currentEmployee = application.readRecords(currentByteStart);
			while (currentEmployee.getEmployeeId() == 0) {
				currentByteStart = application.getNext(currentByteStart);
				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	private void lastRecord() {
		
		if (isSomeoneToDisplay()) {
			
			application.openReadFile(file.getAbsolutePath());
			currentByteStart = application.getLast();
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();
			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();
		}
	}

	public void searchEmployeeById() {
		
		boolean found = false;

		try {
			if (isSomeoneToDisplay()) {
				
				firstRecord();
				int firstId = currentEmployee.getEmployeeId();
				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				}
				else {
					nextRecord();
					while (firstId != currentEmployee.getEmployeeId()) {
						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else
							nextRecord();
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} 
		}
		catch (NumberFormatException e) {
			
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}

	public void searchEmployeeBySurname() {
		boolean found = false;
		if (isSomeoneToDisplay()) {
			firstRecord();
			String firstSurname = currentEmployee.getSurname().trim();
			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			}
			else {
				nextRecord();
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					} 
					else
						nextRecord();
				}
			}
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		}
		searchBySurnameField.setText("");
	}

	public int getNextFreeId() {
		
		int nextFreeId = 0;
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		application.openWriteFile(file.getAbsolutePath());
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {
			
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			if (returnVal == JOptionPane.YES_OPTION) {
				application.openWriteFile(file.getAbsolutePath());
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();
				if (isSomeoneToDisplay()) {
					nextRecord();
					displayRecords(currentEmployee);
				}
			}
		}
	}

	private Vector<Object> getAllEmloyees() {
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();
		firstId = currentEmployee.getEmployeeId();
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();
		} while (firstId != currentEmployee.getEmployeeId());
		currentByteStart = byteStart;

		return allEmployee;
	}

	private void editDetails() {
		if (isSomeoneToDisplay()) {
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);
		}
	}

	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		application.openReadFile(file.getAbsolutePath());
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();
		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}

	public void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}

	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		fc.setFileFilter(datfilter);
		File newFile;
		
		if (file.length() != 0 || change) {
			
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			newFile = fc.getSelectedFile();
			if (file.getName().equals(generatedFileName))
				file.delete();
			file = newFile;
			application.openReadFile(file.getAbsolutePath());
			firstRecord();
			displayRecords(currentEmployee);
			application.closeReadFile();
		}
	}

	private void saveFile() {
		if (file.getName().equals(generatedFileName))
			saveFileAs();
		else {
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				if (returnVal == JOptionPane.YES_OPTION) {

					if (!idField.getText().equals("")) {
						application.openWriteFile(file.getAbsolutePath());
						currentEmployee = getChangedDetails();
						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();
					}
				}
			}

			displayRecords(currentEmployee);
			setEnabled(false);
		}
	}

	public void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		if (returnVal == JOptionPane.YES_OPTION) {
			application.openWriteFile(file.getAbsolutePath());
			currentEmployee = getChangedDetails();
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();
			changesMade = false;
		} 
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			if (!edv.checkFileName(newFile)) {
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				application.createFile(newFile.getAbsolutePath());
			}
			else
				application.createFile(newFile.getAbsolutePath());

			try {

				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			}
			catch (IOException e) {
				System.out.println("ERROR" +e.getMessage());
			}
		}
		changesMade = false;
	}

	private void exitApp() {
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();
					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}
				else if (returnVal == JOptionPane.NO_OPTION) {
					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}
			}
			else {
				if (file.getName().equals(generatedFileName))
					file.delete();
				System.exit(0);
			}
			
		} else {
	
			if (file.getName().equals(generatedFileName))
				file.delete();
			System.exit(0);
		} 
	}


	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();

		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		return fileName.toString();

	}

	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		file = new File(generatedFileName);
		application.createFile(file.getName());
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == closeApp) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				exitApp();
		} else if (e.getSource() == open) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				openFile();
		} else if (e.getSource() == save) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				saveFile();
			change = false;
		} else if (e.getSource() == saveAs) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				saveFileAs();
			change = false;
		} else if (e.getSource() == searchById) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				displaySearchByIdDialog();
		} else if (e.getSource() == searchBySurname) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				displaySearchBySurnameDialog();
		} else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeBySurname();
		else if (e.getSource() == saveChange) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				;
		} else if (e.getSource() == cancelChange)
			cancelChange();
		else if (e.getSource() == firstItem || e.getSource() == first) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges()) {
				firstRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == prevItem || e.getSource() == previous) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges()) {
				previousRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == nextItem || e.getSource() == next) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges()) {
				nextRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == lastItem || e.getSource() == last) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges()) {
				lastRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == listAll || e.getSource() == displayAll) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
		} else if (e.getSource() == create || e.getSource() == add) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == modify || e.getSource() == edit) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				editDetails();
		} else if (e.getSource() == delete || e.getSource() == deleteButton) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				deleteRecord();
		} else if (e.getSource() == searchBySurname) {
			if (edv.validateEmployeeDetails() && !edv.checkForChanges())
				new SearchBySurnameDialog(EmployeeDetails.this);
		}
	}

	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());
		dialog.add(searchPanel(), "width 400:400:400, " +gp);
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		dialog.add(buttonPanel(), gp+", span 2,wrap");
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	public void windowClosing(WindowEvent e) {

		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}