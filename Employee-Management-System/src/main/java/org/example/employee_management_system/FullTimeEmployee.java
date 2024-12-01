package org.example.employee_management_system;

import java.sql.Date;
import java.time.LocalDate;

public class FullTimeEmployee extends Employee {
    private long id;
    private double monthlySalary;

    public FullTimeEmployee(long id, String name, String position, String type, LocalDate hireDate, double monthlySalary) {
        super(name, position, "FullTime", hireDate);
        this.id = id;
        this.monthlySalary = monthlySalary;
        calculateSalary();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
        calculateSalary();
    }

    @Override
    public void calculateSalary() {
        setCalculatedSalary(monthlySalary);
    }

    @Override
    public String toString() {
        return "FullTimeEmployee{" +
                "id=" + id +
                ", monthlySalary=" + monthlySalary +
                '}';
    }
}