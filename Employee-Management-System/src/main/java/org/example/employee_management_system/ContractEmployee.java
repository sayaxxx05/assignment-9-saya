package org.example.employee_management_system;

import java.sql.Date;
import java.time.LocalDate;

public class ContractEmployee extends Employee {
    private long id;
    private double hourlyRate;
    private double maxHours;

    public ContractEmployee(long id, String name, String position, String type, LocalDate hireDate, double hourlyRate, double maxHours) {
        super(name, position, "Contract", hireDate);
        this.hourlyRate = hourlyRate;
        this.maxHours = maxHours;
        this.id = id;
        calculateSalary();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id; // Позволяем обновлять ID
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
        calculateSalary();
    }

    public double getMaxHours() {
        return maxHours;
    }

    public void setMaxHours(double maxHours) {
        this.maxHours = maxHours;
        calculateSalary();
    }

    @Override
    public void calculateSalary() {
        setCalculatedSalary(hourlyRate * maxHours);
    }

    @Override
    public String toString() {
        return "ContractEmployee{" +
                "id=" + id +
                ", hourlyRate=" + hourlyRate +
                ", maxHours=" + maxHours +
                '}';
    }
}