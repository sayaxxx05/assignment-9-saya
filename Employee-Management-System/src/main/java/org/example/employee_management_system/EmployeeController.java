package org.example.employee_management_system;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.time.LocalDate;
import java.sql.Date;
import java.sql.*;
import java.time.format.DateTimeParseException;

public class EmployeeController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField positionField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextField hourlyRateField;
    @FXML
    private TextField hoursWorkedField;
    @FXML
    private TextField maxHoursField;
    @FXML
    private TextField annualSalaryField;
    @FXML
    private ChoiceBox<String> employeeTypeChoiceBox;

    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Long> idColumn;
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> positionColumn;
    @FXML
    private TableColumn<Employee, LocalDate> hireDateColumn;
    @FXML
    private TableColumn<Employee, String> typeColumn;
    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    private Label totalSalaryLabel;
    @FXML
    private Label calculatedSalariesLabel;

    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private EmployeeData employeeData;

    public EmployeeController() {
        employeeData = new EmployeeData();
        employees = FXCollections.observableArrayList(employeeData.getAllEmployees());
    }

    @FXML
    public void initialize() {
        employeeTypeChoiceBox.setItems(FXCollections.observableArrayList("Full-Time", "Part-Time", "Contract"));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        typeColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof FullTimeEmployee) {
                return new SimpleStringProperty("Full-Time");
            } else if (data.getValue() instanceof PartTimeEmployee) {
                return new SimpleStringProperty("Part-Time");
            } else if (data.getValue() instanceof ContractEmployee) {
                return new SimpleStringProperty("Contract");
            }
            return null;
        });
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("calculatedSalary"));

        employeeTable.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        positionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        hireDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DatePicker datePicker = new DatePicker();

            {
                datePicker.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    LocalDate date = datePicker.getValue();
                    if (date != null) {
                        // Преобразуем LocalDate в java.sql.Date перед сохранением в объект
                        employee.setHireDate(Date.valueOf(date).toLocalDate());
                    }
                });
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Преобразуем java.sql.Date в LocalDate перед отображением
                    datePicker.setValue(item);
                    setGraphic(datePicker);
                }
            }
        });


        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                "Full-Time", "Part-Time", "Contract"
        ));

        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        employeeTable.setItems(employees);
    }

    @FXML
    private void addEmployee() {
        try {
            String name = nameField.getText().trim();
            String position = positionField.getText().trim();
            LocalDate hireDateLocal = hireDatePicker.getValue();
            String type = employeeTypeChoiceBox.getValue();

            if (name.isEmpty() || position.isEmpty() || hireDateLocal == null || type == null) {
                showAlert(Alert.AlertType.WARNING, "Ошибка ввода", "Заполните все поля.");
                return;
            }

            Date hireDate = Date.valueOf(hireDateLocal);
            Employee employee = null;
            long tempId = 0;

            switch (type) {
                case "Full-Time" -> {
                    // Инициализация для Full-Time сотрудника
                    double monthlySalary = Double.parseDouble(annualSalaryField.getText().trim()); // Вероятно, здесь предполагается годовая зарплата
                    employee = new FullTimeEmployee(0, name, position, "Full-Time", hireDate.toLocalDate(), monthlySalary);
                }
                case "Part-Time" -> {
                    // Инициализация для Part-Time сотрудника
                    double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                    double hoursWorked = Double.parseDouble(hoursWorkedField.getText().trim());
                    employee = new PartTimeEmployee(0, name, position, "Part-Time", hireDate.toLocalDate(), hourlyRate, hoursWorked);
                }
                case "Contract" -> {
                    // Инициализация для Contract сотрудника
                    double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                    double maxHours = Double.parseDouble(maxHoursField.getText().trim());
                    employee = new ContractEmployee(0, name, position, "Contract", hireDate.toLocalDate(), hourlyRate, maxHours);
                }
            }

            employees.add(employee);
            employeeData.addEmployee(employee);
            employees.clear();
            employees.addAll(employeeData.getAllEmployees());
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Сотрудник успешно добавлен.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Введите числовые значения для зарплаты и часов.");
            showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Не удалось добавить сотрудника: ");
        }
    }

    @FXML
    private void deleteSelectedEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Удаление сотрудника", "Выберите сотрудника для удаления.");
            return;
        }

        int employeeId = (int) selectedEmployee.getId(); // Приводим ID к типу `int`, если требуется
        try {
            // Удаляем из базы данных
            employeeData.deleteEmployee(employeeId);

            // Удаляем из ObservableList
            employees.remove(selectedEmployee);

            showAlert(Alert.AlertType.INFORMATION, "Удаление сотрудника", "Сотрудник успешно удален.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить сотрудника: " + e.getMessage());
        }
    }

    @FXML
    private void updateSelectedEmployee() {
        // Получаем выбранного сотрудника из таблицы
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите сотрудника для обновления.");
            return;
        }

        try {
            // Обновляем общие поля (имя, позиция, дата назначения)
            String newName = nameField.getText().trim();
            String newPosition = positionField.getText().trim();
            LocalDate newHireDate = hireDatePicker.getValue();
            String newType = employeeTypeChoiceBox.getValue();

            if (!newName.isEmpty()) {
                selectedEmployee.setName(newName);
            }

            if (!newPosition.isEmpty()) {
                selectedEmployee.setPosition(newPosition);
            }

            newHireDate = hireDatePicker.getValue();
            if (newHireDate != null) {
                selectedEmployee.setHireDate(Date.valueOf(newHireDate).toLocalDate());
            }

            // Проверяем и обновляем тип сотрудника
            if (newType != null) {
                switch (newType) {
                    case "Full-Time" -> {
                        // Если тип изменён на Full-Time
                        if (!(selectedEmployee instanceof FullTimeEmployee)) {
                            selectedEmployee = new FullTimeEmployee(selectedEmployee.getId(),
                                    newName,
                                    newPosition,
                                    "Full-Time",
                                    selectedEmployee.getHireDate(),
                                    0.0);
                        }
                        String annualSalaryText = annualSalaryField.getText().trim();
                        if (!annualSalaryText.isEmpty()) {
                            double annualSalary = Double.parseDouble(annualSalaryText);
                            ((FullTimeEmployee) selectedEmployee).setMonthlySalary(annualSalary);
                        }
                    }
                    case "Part-Time" -> {
                        // Если тип изменён на Part-Time
                        if (!(selectedEmployee instanceof PartTimeEmployee)) {
                            selectedEmployee = new PartTimeEmployee(selectedEmployee.getId(),
                                    newName,
                                    newPosition,
                                    "Part-Time",
                                    selectedEmployee.getHireDate(),
                                    0.0, 0.0);
                        }
                        String hourlyRateText = hourlyRateField.getText().trim();
                        String hoursWorkedText = hoursWorkedField.getText().trim();

                        if (!hourlyRateText.isEmpty()) {
                            double hourlyRate = Double.parseDouble(hourlyRateText);
                            ((PartTimeEmployee) selectedEmployee).setHourlyRate(hourlyRate);
                        }

                        if (!hoursWorkedText.isEmpty()) {
                            double hoursWorked = Double.parseDouble(hoursWorkedText);
                            ((PartTimeEmployee) selectedEmployee).setHoursWorked(hoursWorked);
                        }
                    }
                    case "Contract" -> {
                        // Если тип изменён на Contract
                        if (!(selectedEmployee instanceof ContractEmployee)) {
                            selectedEmployee = new ContractEmployee(selectedEmployee.getId(),
                                    newName,
                                    newPosition,
                                    "Contract",
                                    selectedEmployee.getHireDate(),
                                    0.0, 0.0);
                        }
                        String hourlyRateText = hourlyRateField.getText().trim();
                        String maxHoursText = maxHoursField.getText().trim();

                        if (!hourlyRateText.isEmpty()) {
                            double hourlyRate = Double.parseDouble(hourlyRateText);
                            ((ContractEmployee) selectedEmployee).setHourlyRate(hourlyRate);
                        }

                        if (!maxHoursText.isEmpty()) {
                            double maxHours = Double.parseDouble(maxHoursText);
                            ((ContractEmployee) selectedEmployee).setMaxHours(maxHours);
                        }
                    }
                }
            }

            // Обновляем данные сотрудника в базе
            employeeData.updateEmployee((int) selectedEmployee.getId(), selectedEmployee);

            // Обновляем таблицу
            employees.clear();
            employees.addAll(employeeData.getAllEmployees());
            employeeTable.refresh();

            showAlert(Alert.AlertType.INFORMATION, "Успех", "Данные сотрудника успешно обновлены.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Проверьте правильность числовых данных.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Произошла ошибка при обновлении данных сотрудника.");
        }
    }


    @FXML
    private void calculateSalaries() {
        double totalSalary = 0.0;
        for (Employee employee : employees) {
            employee.calculateSalary();
            totalSalary += employee.getCalculatedSalary();
            System.out.println(totalSalary);
        }

        employeeTable.refresh();
        calculatedSalariesLabel.setText("Общая сумма зарплат: ");
        calculatedSalariesLabel.setVisible(true);
        calculatedSalariesLabel.setManaged(true);
        totalSalaryLabel.setText(String.valueOf(totalSalary));
        totalSalaryLabel.setVisible(true);
        totalSalaryLabel.setManaged(true);
    }



    private void clearFields() {
        nameField.clear();
        positionField.clear();
        hireDatePicker.setValue(null);
        hourlyRateField.clear();
        hoursWorkedField.clear();
        maxHoursField.clear();
        annualSalaryField.clear();
        employeeTypeChoiceBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
