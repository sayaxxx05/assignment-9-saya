package org.example.employee_management_system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeData {
    private Connection connection;

    public EmployeeData() {
        String url = "jdbc:postgresql:employee_db";
        String username = "postgres";
        String password = "Tls06141301";

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database is successfully connected...");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e);
        }
    }

    public void addEmployee(Employee employee) {
        String sql = "INSERT INTO employee (name, position, type, hire_date, calculated_salary, hourly_rate, hours_worked, max_hours) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getPosition());
            statement.setString(3, employee.getType());
            statement.setDate(4, Date.valueOf(employee.getHireDate())); // hireDate из LocalDate
            statement.setDouble(5, employee.getCalculatedSalary());

            if (employee instanceof PartTimeEmployee partTime) {
                statement.setDouble(6, partTime.getHourlyRate());
                statement.setDouble(7, partTime.getHoursWorked());
                statement.setNull(8, Types.DOUBLE);
            } else if (employee instanceof ContractEmployee contract) {
                statement.setDouble(6, contract.getHourlyRate());
                statement.setNull(7, Types.DOUBLE);
                statement.setObject(8, contract.getMaxHours(), Types.DOUBLE);
            } else {
                statement.setNull(6, Types.DOUBLE);
                statement.setNull(7, Types.DOUBLE);
                statement.setNull(8, Types.DOUBLE);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                employee.setId(resultSet.getLong("id")); // Устанавливаем ID, сгенерированный базой
            }
        } catch (SQLException e) {
            System.out.println("Error adding employee: " + e.getMessage());
        }
    }


    public void deleteEmployee(int id) {
        String query = "DELETE FROM employee WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Employee deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e);
        }
    }

    public void updateEmployee(int id, Employee employee) {
        String sql = "UPDATE employee SET name = ?, position = ?, type = ?, hire_date = ?, calculated_salary = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql:employee_db", "postgres", "Tls06141301");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, employee.getName());  // имя сотрудника
            preparedStatement.setString(2, employee.getPosition());  // должность
            preparedStatement.setString(3, employee.getType());  // тип сотрудника
            preparedStatement.setDate(4, Date.valueOf(employee.getHireDate()));  // дата найма
            preparedStatement.setDouble(5, employee.getCalculatedSalary());  // расчетная зарплата
            preparedStatement.setInt(6, id);  // id сотрудника

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No employee found with ID: " + id);
            } else {
                System.out.println("Employee updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String type = resultSet.getString("type");

                Employee employee;
                switch (type) {
                    case "FullTime" -> employee = new FullTimeEmployee(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("calculated_salary")
                    );
                    case "PartTime" -> employee = new PartTimeEmployee(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("hourly_rate"),
                            resultSet.getDouble("hours_worked")
                    );
                    case "Contract" -> employee = new ContractEmployee(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("hourly_rate"),
                            resultSet.getDouble("max_hours")
                    );
                    default -> throw new IllegalArgumentException("Unknown employee type: " + type);
                }

                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees: " + e.getMessage());
        }

        return employees;
    }


    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employee WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String type = resultSet.getString("type");

                return switch (type) {
                    case "FullTime" -> new FullTimeEmployee(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("calculated_salary")
                    );
                    case "PartTime" -> new PartTimeEmployee(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("hourly_rate"),
                            resultSet.getDouble("hours_worked")
                    );
                    case "Contract" -> new ContractEmployee(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("position"),
                            type,
                            resultSet.getDate("hire_date").toLocalDate(),
                            resultSet.getDouble("hourly_rate"),
                            resultSet.getDouble("max_hours")
                    );
                    default -> throw new IllegalArgumentException("Unknown employee type: " + type);
                };
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee by ID: " + e.getMessage());
        }

        return null;
    }
}
