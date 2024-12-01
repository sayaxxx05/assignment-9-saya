module org.example.employee_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.employee_management_system to javafx.fxml;
    exports org.example.employee_management_system;
}