package books;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeManagementSystem extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField salaryField;
    private JTextField designationField;
    private Connection connection;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JFrame employeeFrame;

    public EmployeeManagementSystem() {
        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        idField = new JTextField(10);
        nameField = new JTextField(10);
        salaryField = new JTextField(10);
        designationField = new JTextField(10);

        JButton addButton = new JButton("Insert");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        JButton viewButton = new JButton("View Employees");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmployeeTableWindow();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Salary:"), gbc);

        gbc.gridx = 1;
        add(salaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Designation:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(designationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(addButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(viewButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(deleteButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(resetButton, gbc);

        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_java", "root", "Sanket@05");
            System.out.println("Connection successful.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEmployee() {
        String id = idField.getText();
        String name = nameField.getText();
        String salary = salaryField.getText();
        String designation = designationField.getText();

        String insertSQL = "INSERT INTO employees (id, name, salary, designation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, salary);
            preparedStatement.setString(4, designation);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Employee added successfully.");
                resetFields();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEmployeeTableWindow() {
        if (employeeFrame != null) {
            employeeFrame.dispose();
        }

        employeeFrame = new JFrame("Employee Table");
        employeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        employeeFrame.setSize(800, 400);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Salary", "Designation"}, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);

        String selectSQL = "SELECT * FROM employees";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String salary = resultSet.getString("salary");
                String designation = resultSet.getString("designation");
                tableModel.addRow(new Object[]{id, name, salary, designation});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch employee data.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        employeeFrame.add(tableScrollPane);
        employeeFrame.setVisible(true);
    }

    private void deleteEmployee() {
        String id = idField.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an ID to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String deleteSQL = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setString(1, id);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Employee record deleted successfully.");
                    resetFields();
                } else {
                    JOptionPane.showMessageDialog(null, "No records found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete employee record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetFields() {
        idField.setText("");
        nameField.setText("");
        salaryField.setText("");
        designationField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EmployeeManagementSystem eManagementSystem = new EmployeeManagementSystem();
                eManagementSystem.setVisible(true);
            }
        });
    }
}
