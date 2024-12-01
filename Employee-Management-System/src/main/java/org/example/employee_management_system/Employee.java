package org.example.employee_management_system;

import java.sql.Date;
import java.time.LocalDate;

public abstract class Employee {
    private static long idCounter = 0;

    private long id;
    private String name;
    private String position;
    private String type;
    private double calculatedSalary;
    private LocalDate hireDate;

    public Employee(String name, String position, String type, LocalDate hireDate) {
        this.id = ++idCounter;
        this.name = name;
        this.position = position;
        this.type = type;
        this.hireDate = hireDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { // Добавлен сеттер для ID
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getCalculatedSalary() {
        return calculatedSalary;
    }

    public void setCalculatedSalary(double calculatedSalary) {
        this.calculatedSalary = calculatedSalary;
    }

    public abstract void calculateSalary();
}
