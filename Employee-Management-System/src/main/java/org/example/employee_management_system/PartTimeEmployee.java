package org.example.employee_management_system;

import java.sql.Date;
import java.time.LocalDate;

public class PartTimeEmployee extends Employee {
    private long id;
    private double hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(long id, String name, String position, String type, LocalDate hireDate, double hourlyRate, double hoursWorked) {
        super(name, position, "PartTime", hireDate);
        this.id = id;
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        calculateSalary();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id; // Позволяем обновлять ID
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
        calculateSalary();
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
        calculateSalary();
    }

    @Override
    public void calculateSalary() {
        setCalculatedSalary(hourlyRate * hoursWorked);
    }

    @Override
    public String toString() {
        return "PartTimeEmployee{" +
                "id=" + id +
                ", hoursWorked=" + hoursWorked +
                ", hourlyRate=" + hourlyRate +
                '}';
    }
}