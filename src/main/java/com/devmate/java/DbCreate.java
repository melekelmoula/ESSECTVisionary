package com.devmate.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbCreate {
    public static void main(String[] args) {
        // SQL statements to create tables
        String usersTableSQL = "CREATE TABLE `users` (\n" +
                " `Username` varchar(200) NOT NULL,\n" +
                " `Password` varchar(200) NOT NULL,\n" +
                " `Groups_list` varchar(200) DEFAULT NULL,\n" +
                " `Groupnumber` varchar(200) DEFAULT NULL,\n" +
                " `Subjects` varchar(200) DEFAULT NULL,\n" +
                " `Role` varchar(200) NOT NULL,\n" +
                " PRIMARY KEY (`Username`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1";


        // SQL statements to create tables
        String resultTableSQL = "CREATE TABLE `result` (\n" +
                " `Username` varchar(100) NOT NULL,\n" +
                " `Score` int(100) NOT NULL,\n" +
                " `Quizno` int(100) NOT NULL,\n" +
                " `Subject` varchar(100) NOT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

        // SQL statements to create tables
        String quiztTableSQL = "CREATE TABLE `quiz` (\n" +
                " `quizz_no` int(200) NOT NULL,\n" +
                " `professor` varchar(50) NOT NULL,\n" +
                " `question` varchar(60) NOT NULL,\n" +
                " `answer1` varchar(60) NOT NULL,\n" +
                " `answer2` varchar(20) NOT NULL,\n" +
                " `answer3` varchar(20) NOT NULL,\n" +
                " `matiere` varchar(60) NOT NULL,\n" +
                " `correct_answer` varchar(20) NOT NULL,\n" +
                " `date_start` date NOT NULL,\n" +
                " `date_end` date NOT NULL,\n" +
                " `time` int(11) NOT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

        // SQL statements to create tables
        String questiontTableSQL = "CREATE TABLE `questions` (\n" +
                " `professor` varchar(1000) NOT NULL,\n" +
                " `question` varchar(1000) NOT NULL,\n" +
                " `answer1` varchar(1000) NOT NULL,\n" +
                " `answer2` varchar(1000) NOT NULL,\n" +
                " `answer3` varchar(1000) NOT NULL,\n" +
                " `correct_answer` varchar(1000) NOT NULL,\n" +
                " `subject` varchar(1000) NOT NULL,\n" +
                " `level` int(255) NOT NULL,\n" +
                " `groups_list` varchar(1000) NOT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1";


        // SQL statements to create tables
        String groupssubjectstTableSQL = "CREATE TABLE `groups_subjects` (\n" +
                " `Subject` varchar(50) DEFAULT NULL,\n" +
                " `Groups` varchar(250) DEFAULT NULL,\n" +
                " UNIQUE KEY `Subject` (`Subject`),\n" +
                " UNIQUE KEY `Groups` (`Groups`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1";


        // SQL statement to insert a new row into the admins table
        String insertAdminSQL = "INSERT INTO `users` (`Username`, `Password`,'Role') VALUES ('a', 'a','Admin')";

        // Establishing database connection
        try (Connection connection = DriverManager.getConnection(DatabaseManager.URL, DatabaseManager.USERNAME, DatabaseManager.PASSWORD)) {
            Statement statement = connection.createStatement();
            // Creating users table
            statement.executeUpdate(usersTableSQL);
            System.out.println("Table created: users");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
