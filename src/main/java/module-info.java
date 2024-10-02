module com.devmate.java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires java.mail;
    requires MaterialFX;
    requires jdk.compiler;
    requires com.zaxxer.hikari;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires com.google.gson;
    requires java.net.http;
    requires org.json;

    opens com.devmate.java to javafx.fxml;
    exports com.devmate.java;
    exports com.devmate.java.Admin;
    opens com.devmate.java.Admin to javafx.fxml;
    exports com.devmate.java.Professor;
    opens com.devmate.java.Professor to javafx.fxml;
    exports com.devmate.java.Student;
    opens com.devmate.java.Student to javafx.fxml;
    exports com.devmate.java.Exception;
    opens com.devmate.java.Exception to javafx.fxml;
    exports com.devmate.java.Path_Groups_Subjects;
    opens com.devmate.java.Path_Groups_Subjects to javafx.fxml;


}
