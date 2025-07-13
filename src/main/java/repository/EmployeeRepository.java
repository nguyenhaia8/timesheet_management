package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Employee;

/**
 * This class contains code to interact directly with DB
 */
public class EmployeeRepository {
    private final String URL = "jdbc:mysql://localhost:3306/TIMESHEET_DB";
    private final String USER = "root";
    private final String PASSWORD = "Globe@1234";

    public void createEmployee(Employee employee) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "INSERT INTO employee (employee_code, first_name, last_name, department) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employee.getEmployeeCode());
            preparedStatement.setString(2, employee.getFirstName());
            preparedStatement.setString(3, employee.getLastName());
            preparedStatement.setString(4, employee.getDepartment());
            preparedStatement.executeUpdate();
            System.out.println("Employee created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
