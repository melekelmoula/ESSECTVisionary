package com.devmate.java.Admin;
import com.devmate.java.DatabaseManager;
import com.devmate.java.Exception.EmailNotFoundException;
import com.devmate.java.Exception.IncorrectLoginException;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Path_Groups_Subjects.Subject;
import com.devmate.java.Student.Student;
import com.devmate.java.Professor.Professor;
import javafx.collections.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    private String Username;
    private String Password;

    public Admin(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public static <T> T SELECT_RECOVER_BY_EMAIL(String email) throws EmailNotFoundException, SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sqlStudent = "SELECT Student_Username, Student_Password FROM essect_students WHERE Student_Email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlStudent)) {
                statement.setString(1, email);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("Student_Username");
                        String password = resultSet.getString("Student_Password");
                        return (T) new Student(username, password);
                    }
                }
            }

            String sqlProfessor = "SELECT Professor_Username, Professor_Password FROM essect_professors WHERE Professor_Email = ?";
            try (PreparedStatement statement2 = connection.prepareStatement(sqlProfessor)) {
                statement2.setString(1, email);
                try (ResultSet resultSet2 = statement2.executeQuery()) {
                    if (resultSet2.next()) {
                        String username = resultSet2.getString("Professor_Username");
                        String password = resultSet2.getString("Professor_Password");
                        return (T) new Professor(username, password);
                    } else {
                        throw new EmailNotFoundException("The email you entered isn’t connected to an account");
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static Admin SELECT_ADMIN_BY_USERNAME_PASSWORD(String username, String password) throws IncorrectLoginException, SQLException {
        Admin admin;
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Admin_Username, Admin_Password FROM essect_admins WHERE Admin_Username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String passwordadmin = resultSet.getString("Admin_Password");
                        if (passwordadmin.equals(password)) {
                            admin = new Admin(username, password);
                        } else {
                            throw new IncorrectLoginException("The password you’ve entered is incorrect");
                        }
                    } else {
                        throw new IncorrectLoginException("The username you entered isn’t connected to an account");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return admin;
    }

    public static Student SELECT_STUDENT_BY_USERNAME_PASSWORD(String username, String password) throws IncorrectLoginException, SQLException{
        Student Student_X ;
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Student_Username,Student_Password,Student_Email,Student_Path,Student_Group FROM essect_students WHERE Student_Username=? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String passwordStudent = resultSet.getString("Student_Password");
                if (passwordStudent.equals(password))
                {
                Student_X = new Student(username, password);
                String path_Name = resultSet.getString("Student_Path");
                String group_Name = resultSet.getString("Student_Group");
                String Email = resultSet.getString("Student_Email");

                if (path_Name!=null){
                    ObservableList<Subject> SubjectsList = FXCollections.observableArrayList(); // Initialize observable list

                    String sql2 = "SELECT Subject_Name,Subject_Coefficient FROM essect_subjects WHERE Path_Name=? ";
                    PreparedStatement statement2 = connection.prepareStatement(sql2);
                    statement2.setString(1, path_Name);
                    ResultSet resultSet2 = statement2.executeQuery();
                    while (resultSet2.next()) {
                        Subject Subject_X = new Subject();
                        Subject_X.setSubjectName(resultSet2.getString("Subject_Name"));
                        Subject_X.setSubjectCoefficient(resultSet2.getDouble("Subject_Coefficient"));
                        SubjectsList.add(Subject_X);
                    }

                    if (path_Name != null && group_Name == null) {
                        Path Path_X = new Path();
                        Path_X.setPathname(path_Name);
                        Path_X.setSubjectsList(SubjectsList);
                        Student_X.setPath(Path_X);

                    }
                    if (group_Name != null) {
                        Group Group_X = new Group();
                        Group_X.setGroupeName(group_Name);
                        Group_X.setPath(path_Name);
                        Group_X.setPathSubjectList(SubjectsList);
                        Student_X.setGroup(Group_X);
                    }
               }
                Student_X.setEmail(Email);
             }
                else {
                    throw new IncorrectLoginException("The password you’ve entered is incorrect");
                }
            }
            else {
                throw new IncorrectLoginException("The username you entered isn’t connected to an account");
            }
        }
        catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return Student_X;
    }

    public static Professor SELECT_PROFESSOR_BY_USERNAME_PASSWORD(String username, String password) throws IncorrectLoginException, SQLException {
        Professor Professor_X ;

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Professor_Username, Professor_Password FROM essect_professors WHERE Professor_Username=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String passwordProfessor = resultSet.getString("Professor_Password");
                        if (passwordProfessor.equals(password)) {
                            Professor_X = new Professor(username, password);
                            ObservableList<Group> AffectationListProfessor = FXCollections.observableArrayList();

                            String sql3 = "SELECT DISTINCT Path_name, Group_Name FROM essect_professors_affectation WHERE Professor_Name = ?";
                            try (PreparedStatement statement3 = connection.prepareStatement(sql3)) {
                                statement3.setString(1, username);
                                try (ResultSet resultSet3 = statement3.executeQuery()) {
                                    while (resultSet3.next()) {
                                        Group Group_X = new Group();
                                        Group_X.setGroupeName(resultSet3.getString("Group_Name"));
                                        Path Path_X = new Path ();
                                        Path_X.setPathname(resultSet3.getString("Path_Name"));
                                        Group_X.setPath(Path_X);

                                        ObservableList<Subject> AffectationSubjectList = FXCollections.observableArrayList();
                                        String sql4 = "SELECT Subject_Name,Path_name FROM essect_professors_affectation WHERE Professor_Name = ? AND Group_Name=? AND Path_name=?";
                                        try (PreparedStatement statement4 = connection.prepareStatement(sql4)) {
                                            statement4.setString(1, username);
                                            statement4.setString(2, Group_X.getGroupeName());
                                            statement4.setString(3, Group_X.getPathname());
                                            try (ResultSet resultSet4 = statement4.executeQuery()) {
                                                while (resultSet4.next()) {
                                                    Subject Subject_X = new Subject();
                                                    Subject_X.setSubjectName(resultSet4.getString("Subject_Name"));
                                                    Subject_X.setPathname(Path_X);
                                                    AffectationSubjectList.add(Subject_X);
                                                }
                                            }
                                        }
                                        Group_X.setPathSubjectList(AffectationSubjectList);
                                        AffectationListProfessor.add(Group_X); // Group List
                                    }
                                }
                            }
                            Professor_X.setAffectation_List(AffectationListProfessor);

                        } else {
                            throw new IncorrectLoginException("The password you’ve entered is incorrect");
                        }
                    } else {
                        throw new IncorrectLoginException("The username you entered isn’t connected to an account");
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return Professor_X;
    }

    public static Boolean SELECT_THEME(String Username,String Role) {
        Boolean Theme = false; // Default value
        String Aux = Role.substring(0, Role.length() - 1);

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Theme FROM essect_" + Role + " WHERE " + Aux +"_Username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Theme = resultSet.getBoolean("Theme");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Theme;
    }

    public static void UPDATE_THEME(Boolean ThemeValue,String Username,String Role) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String Aux = Role.substring(0, Role.length() - 1);

            String sql = "UPDATE essect_"+Role+" SET Theme=? WHERE " + Aux +"_Username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBoolean(1, ThemeValue);
            statement.setString(2, Username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CREATE_PATH (String Path_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO essect_paths (Path_Name) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Path_Name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void UPDATE_PATH(String OldPath_Name, String NewPath_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE essect_paths SET Path_Name = ? WHERE Path_Name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, NewPath_Name);
            statement.setString(2, OldPath_Name);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void DELETE_PATH(String Path_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM essect_paths WHERE Path_Name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Path_Name);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public ObservableList<Path> SELECT_ALL_PATHS(String Param) {

        ObservableList<Path> PathsList = FXCollections.observableArrayList(); // Initialize observable list

        String sql = "SELECT DISTINCT Path_Name FROM essect_paths";
        try (Connection connection = DatabaseManager.getConnection()) {
             PreparedStatement statement = connection.prepareStatement(sql) ;
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String pathname = resultSet.getString("Path_Name");

                    Path Path_X = new Path();
                    Path_X.setPathname(pathname);

                    ObservableList<Student> StudentsList = FXCollections.observableArrayList();
                    ObservableList<Subject> SubjectsList = FXCollections.observableArrayList();
                    ObservableList<Group> GroupsList = FXCollections.observableArrayList();

                    if (Param == null || Param.contains("Group")) {
                        // Fetch groups
                        String sqlGroups = "SELECT Group_Name FROM essect_groups WHERE Path_Name = ?";
                        try (PreparedStatement statementGroups = connection.prepareStatement(sqlGroups)) {
                            statementGroups.setString(1, pathname);
                            try (ResultSet resultSetGroups = statementGroups.executeQuery()) {
                                while (resultSetGroups.next()) {
                                    Group Group_X = new Group();
                                    Group_X.setGroupeName(resultSetGroups.getString("Group_Name"));
                                    GroupsList.add(Group_X);
                                }
                            }
                        }
                    }
                    if (Param == null || Param.contains("Subject")) {

                        String sqlSubjects = "SELECT Subject_Name,Subject_Coefficient,TotalExams FROM essect_subjects WHERE Path_Name = ?";
                        try (PreparedStatement statementSubjects = connection.prepareStatement(sqlSubjects)) {
                            statementSubjects.setString(1, pathname);
                            try (ResultSet resultSetSubjects = statementSubjects.executeQuery()) {
                                while (resultSetSubjects.next()) {
                                    Subject Subject_X = new Subject();
                                    Subject_X.setSubjectName(resultSetSubjects.getString("Subject_Name"));
                                    Subject_X.setSubjectCoefficient(resultSetSubjects.getDouble("Subject_Coefficient"));
                                    Subject_X.setPathname(pathname);
                                    Subject_X.setTotalExams(resultSetSubjects.getInt("TotalExams"));
                                    SubjectsList.add(Subject_X);
                                }
                            }
                        }
                    }
                    if (Param == null || Param.contains("Student")) {

                        // Fetch students
                        String sqlStudents = "SELECT Student_Username FROM essect_students WHERE Student_Path = ?";
                        try (PreparedStatement statementStudents = connection.prepareStatement(sqlStudents)) {
                            statementStudents.setString(1, pathname);
                            try (ResultSet resultSetStudents = statementStudents.executeQuery()) {
                                while (resultSetStudents.next()) {
                                    Student Student_X = new Student();
                                    Student_X.setUsername(resultSetStudents.getString("Student_Username"));
                                    StudentsList.add(Student_X);
                                }
                            }
                        }
                    }
                    Path_X.setGroupslist(GroupsList);
                    Path_X.setSubjectsList(SubjectsList);
                    Path_X.setStudentslist(StudentsList);
                    PathsList.add(Path_X);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PathsList;
    }

    public void CREATE_SUBJECT(String Subject_Name, Double Subject_Coefficient, String Path_Name, int examTotals) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql ="INSERT INTO essect_subjects (Subject_Name,Subject_Coefficient, Path_Name,TotalExams) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Subject_Name);
            statement.setDouble(2, Subject_Coefficient);
            statement.setString(3, Path_Name);
            statement.setInt(4, examTotals);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage()); }
    }

    public void UPDATE_SUBJECT(String OldSubject_Name, String NewSubject_Name, Double Subject_Coefficient, String Path_Name, int TotalExams) throws  SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE essect_subjects SET Subject_Name = ? ,Subject_Coefficient=?,Path_Name =?,TotalExams=? WHERE Subject_Name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, NewSubject_Name);
            statement.setDouble(2, Subject_Coefficient);
            statement.setString(3, Path_Name);
            statement.setInt(4, TotalExams);
            statement.setString(5, OldSubject_Name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void DELETE_SUBJECT(String Subject_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM essect_subjects WHERE Subject_Name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Subject_Name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void CREATE_PROFESSOR(String Professor_Username, String Professor_Password, String Professor_Email, ArrayList<String> SubjectsList) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {

            String sql = "INSERT INTO essect_professors (Professor_Username,Professor_Password,Professor_Email) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, Professor_Username);
            statement.setString(2, Professor_Password);
            statement.setString(3, Professor_Email);
            statement.executeUpdate();

            for (String Subject_X : SubjectsList) {
                String sql2 = "INSERT INTO essect_professors_subjects (Professor_Name,Subject_Name) VALUES (?,?)";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setString(1, Professor_Username);
                statement2.setString(2, Subject_X);
                statement2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}
    }

    public void UPDATE_PROFESSOR(String OldProfessor_Username, String Professor_Email, String NewProfessor_Username, String Professor_Password, ArrayList<String> SubjectsList) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {

            try {
                String sqlUpdateProfessors = "UPDATE essect_professors SET Professor_Username = ?, Professor_Password = ?, Professor_Email = ? WHERE Professor_Username = ?";
                PreparedStatement updateProfessorsStatement = connection.prepareStatement(sqlUpdateProfessors);
                updateProfessorsStatement.setString(1, NewProfessor_Username);
                updateProfessorsStatement.setString(2, Professor_Password);
                updateProfessorsStatement.setString(3, Professor_Email);
                updateProfessorsStatement.setString(4, OldProfessor_Username);
                updateProfessorsStatement.executeUpdate();
                }
                catch (SQLException a){throw new Exception(a.getMessage());}

                String sqlGetSubjects2 = "SELECT Subject_Name FROM essect_professors_subjects WHERE Professor_Name = ?";
                PreparedStatement getSubjectsStatement2 = connection.prepareStatement(sqlGetSubjects2);
                getSubjectsStatement2.setString(1, NewProfessor_Username);
                ResultSet resultSet2 = getSubjectsStatement2.executeQuery();
                ArrayList<String> currentSubjects2 = new ArrayList<>();

                while (resultSet2.next()) {
                    currentSubjects2.add(resultSet2.getString("Subject_Name"));
                }

                if (!currentSubjects2.containsAll(SubjectsList)) {
                    for (String subject : SubjectsList) {
                        if (!currentSubjects2.contains(subject)) {
                            String sqlInsertSubject = "INSERT INTO essect_professors_subjects (Professor_Name, Subject_Name) VALUES (?, ?)";
                            PreparedStatement insertSubjectStatement = connection.prepareStatement(sqlInsertSubject);
                            insertSubjectStatement.setString(1, NewProfessor_Username);
                            insertSubjectStatement.setString(2, subject);
                            insertSubjectStatement.executeUpdate();
                        }
                    }
                }
                else if (!SubjectsList.containsAll(currentSubjects2)) {

                    for (String currentSubject : currentSubjects2) {
                        if (!SubjectsList.contains(currentSubject)) {
                            String sqlDeleteSubject = "DELETE FROM essect_professors_subjects WHERE Professor_Name = ? AND Subject_Name = ?";
                            PreparedStatement deleteSubjectStatement = connection.prepareStatement(sqlDeleteSubject);
                            deleteSubjectStatement.setString(1, NewProfessor_Username);
                            deleteSubjectStatement.setString(2, currentSubject);
                            deleteSubjectStatement.executeUpdate();
                        }
                    }}

        } catch (Exception e) {
         throw new SQLException(e.getMessage());}
     }

    public void DELETE_PROFESSOR(String Professor_Username) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM essect_professors WHERE Professor_Username=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Professor_Username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}
    }

    public ObservableList<Professor> SELECT_ALL_PROFESSORS() {
        ObservableList<Professor> ProfessorsList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Professor_Username, Professor_Password, Professor_Email FROM essect_professors";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int SubjectAffectationCount = 0;

                String username = resultSet.getString("Professor_Username");
                String Email = resultSet.getString("Professor_Email");
                String password = resultSet.getString("Professor_Password");

                Professor Professor_X = new Professor(username, password);
                Professor_X.setEmail(Email);
                String sql2 = "SELECT Subject_Name FROM essect_professors_subjects WHERE Professor_Name = ?";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setString(1, username);
                ResultSet resultSet2 = statement2.executeQuery();
                ObservableList<Subject> SpecializationsList = FXCollections.observableArrayList();

                while (resultSet2.next()) {
                    Subject Subject_X=new Subject();
                    Subject_X.setSubjectName(resultSet2.getString("Subject_Name"));
                    SpecializationsList.add(Subject_X);
                }

                ObservableList<Group> AffectationListProfessor = FXCollections.observableArrayList();

                String sql3 = "SELECT distinct Path_Name FROM essect_professors_affectation WHERE Professor_Name = ?";
                PreparedStatement statement3 = connection.prepareStatement(sql3);
                statement3.setString(1, username);
                ResultSet resultSet3 = statement3.executeQuery();

                while (resultSet3.next()) {
                    Path Path_X = new Path();
                    Path_X.setPathname(resultSet3.getString("Path_Name"));

                    String sql4 = "SELECT distinct Group_Name FROM essect_professors_affectation WHERE Professor_Name = ? AND Path_Name=?";
                    PreparedStatement statement4 = connection.prepareStatement(sql4);
                    statement4.setString(1, username);
                    statement4.setString(2, Path_X.getPathname());
                    ResultSet resultSet4 = statement4.executeQuery();
                    while (resultSet4.next()) {
                            Group Group_X = new Group();
                            Group_X.setPath(Path_X.getPathname());
                            Group_X.setGroupeName(resultSet4.getString("Group_Name"));

                            String sql5 = "SELECT Subject_Name FROM essect_professors_affectation WHERE Professor_Name = ? AND Path_Name=? AND Group_Name=?";
                            PreparedStatement statement5 = connection.prepareStatement(sql5);
                            statement5.setString(1, username);
                            statement5.setString(2, Group_X.getPathname());
                            statement5.setString(3, Group_X.getGroupeName());
                            ResultSet resultSet5 = statement5.executeQuery();
                            ObservableList<Subject> SubjectsList = FXCollections.observableArrayList();

                        while (resultSet5.next()) {
                            Subject Subject_X = new Subject();
                            Subject_X.setSubjectName(resultSet5.getString("Subject_Name"));
                            SubjectsList.add(Subject_X);
                        }
                        Group_X.setPathSubjectList(SubjectsList);
                        SubjectAffectationCount+=SubjectsList.size();
                        AffectationListProfessor.add(Group_X);
                    }
                }

                Professor_X.setSkills_List(SpecializationsList);
                Professor_X.setAffectation_List(AffectationListProfessor);
                Professor_X.setTotal_Affectectation(SubjectAffectationCount);
                ProfessorsList.add(Professor_X);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ProfessorsList;
    }

    public void CREATE_STUDENT(String Student_Username, String Student_Password, String Student_Email, String Path_Name, String Gender , LocalDate Birthdate) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO essect_students (Student_Username, Student_Password,Student_Email,Student_Path,Gender,BirthDate) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Student_Username);
            statement.setString(2, Student_Password);
            statement.setString(3, Student_Email);
            statement.setString(4, Path_Name);
            statement.setString(5, Gender);
            statement.setDate(6, Date.valueOf(Birthdate));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}
    }

    public void UPDATE_STUDENT(String OldStudent_Username, String NewStudent_Username, String Student_Password, String Student_Email, String Path_Name, String Group_Name, String Gender, LocalDate Birthdate) throws Exception {

        Integer NewGroupID = null; // Use Integer object to handle NULL values
        String OldfetchedGroupName = null; // To store the fetched group name

        try (Connection connection = DatabaseManager.getConnection()) {
            // Query to fetch old student group information
            String sql1 = "SELECT Student_Group, Group_ID FROM essect_students WHERE Student_Username=?";
            try (PreparedStatement statement1 = connection.prepareStatement(sql1)) {
                statement1.setString(1, OldStudent_Username);
                try (ResultSet result1 = statement1.executeQuery()) {
                    if (result1.next()) {
                        OldfetchedGroupName = result1.getString("Student_Group");
                    }
                }
            }


            // Query to get the new group ID
            String sql2 = "SELECT Group_ID,Group_Name FROM essect_groups WHERE Group_Name=? AND Path_Name=?";
            try (PreparedStatement statement2 = connection.prepareStatement(sql2)) {
                statement2.setString(1, Group_Name);
                statement2.setString(2, Path_Name);
                try (ResultSet result2 = statement2.executeQuery()) {
                    if (result2.next()) {
                        NewGroupID = result2.getInt("Group_ID");
                    }
                }
            }

            // Update the student information
            String sql3 = "UPDATE essect_students SET Student_Username=?, Student_Password=?, Student_Email=?, Student_Path=?, Student_Group=?, Group_ID=?, Gender=?, BirthDate=? ,FinalGrade=? WHERE Student_Username=?";
            try (PreparedStatement statement3 = connection.prepareStatement(sql3)) {
                statement3.setString(1, NewStudent_Username);
                statement3.setString(2, Student_Password);
                statement3.setString(3, Student_Email);
                statement3.setString(4, Path_Name);
                statement3.setString(5, Group_Name);
                if (NewGroupID != null) {
                    statement3.setInt(6, NewGroupID);
                } else {
                    statement3.setNull(6, Types.INTEGER); // Set Group_ID to NULL if not found
                }
                statement3.setString(7, Gender);
                statement3.setDate(8, Date.valueOf(Birthdate));
                statement3.setNull(9, Types.DOUBLE);
                statement3.setString(10, OldStudent_Username);

                statement3.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    public void DELETE_STUDENT(String Student_Username) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM essect_students WHERE Student_Username=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Student_Username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}
        }

    public ObservableList<Student> SELECT_ALL_STUDENTS() {
        ObservableList<Student> StudentsList = FXCollections.observableArrayList(); // Initialize observable list

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Student_Username, Student_Password, Student_Email,Student_Path,Student_Group,Gender,BirthDate,FinalGrade FROM essect_students";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("Student_Username");
                String password = resultSet.getString("Student_Password");
                String Email = resultSet.getString("Student_Email");
                String path = resultSet.getString("Student_Path");
                String group = resultSet.getString("Student_Group");
                String gender = resultSet.getString("Gender");
                LocalDate Birthdate = resultSet.getDate("BirthDate").toLocalDate();
                Double FinalGrade = resultSet.getDouble("FinalGrade");

                Student Student_X = new Student(username, password);
                Student_X.setEmail(Email);

                if (group != null) {
                    Group Group_X = new Group();
                    Group_X.setPath(path);
                    Group_X.setGroupeName(group);
                    Student_X.setGroup(Group_X);
                } else if (group == null && path != null) {
                    Path Path_X = new Path();
                    Path_X.setPathname(path);
                    Student_X.setPath(Path_X);
                }

                Student_X.setGender(gender);
                Student_X.setBirthdate(Birthdate);
                Student_X.setFinalGrade(FinalGrade);

                StudentsList.add(Student_X);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return StudentsList; // Return the ObservableList of students
    }

    public void CREATE_GROUP(String Group_Name, String Path_Name, ObservableList<String> SelectedStudentsList, ObservableList<Subject> SelectedProfessorsList) throws SQLException {

        try (Connection connection = DatabaseManager.getConnection()) {
            int GroupID = -1;
            String sqlGroup = "INSERT INTO essect_groups (Group_Name, Path_Name) VALUES (?, ?)";
            PreparedStatement statementGroup = connection.prepareStatement(sqlGroup);
            statementGroup.setString(1, Group_Name);
            statementGroup.setString(2, Path_Name);
            statementGroup.executeUpdate();


            String sqlStudent2 = "Select Group_ID from essect_groups WHERE Group_Name=? And Path_Name=?";
            PreparedStatement statementStudent2 = connection.prepareStatement(sqlStudent2);
            statementStudent2.setString(1, Group_Name);
            statementStudent2.setString(2, Path_Name);
            ResultSet resultSdqsdet = statementStudent2.executeQuery();
            while (resultSdqsdet.next()) {
                GroupID = resultSdqsdet.getInt("Group_ID");
            }

            String sqlStudent = "UPDATE essect_students SET Student_Group=?,Group_Id=? WHERE Student_Username=? ";
            PreparedStatement statementStudent = connection.prepareStatement(sqlStudent);
            for (String student : SelectedStudentsList) {
                statementStudent.setString(1, Group_Name);
                statementStudent.setInt(2, GroupID);
                statementStudent.setString(3, student);
                statementStudent.executeUpdate();
            }

            String sqlProf = "INSERT INTO essect_professors_affectation (Professor_Name,Subject_Name,Group_Name,Path_name) VALUES (?,?,?,?)";
            PreparedStatement statementProf = connection.prepareStatement(sqlProf);
            for (Subject Subject_X : SelectedProfessorsList) {
                statementProf.setString(1, Subject_X.getAssignedProfessor().getUsername());
                statementProf.setString(2, Subject_X.getSubjectName());
                statementProf.setString(3, Group_Name);
                statementProf.setString(4, Path_Name);
                statementProf.executeUpdate();
            }

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}

    }

    public void UPDATE_GROUP(String NewGroup_Name, String OldGroup_Name, String Path_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE essect_groups SET Group_Name=? where Group_Name=? And Path_Name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, NewGroup_Name);
            statement.setString(2, OldGroup_Name);
            statement.setString(3, Path_Name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());}

    }

    public void DELETE_GROUP(String Group_Name, String Path_Name) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            // First, check if the group is valid (Valid = 1)
            String checkSql = "SELECT Valid FROM essect_groups WHERE Group_Name = ? AND Path_Name = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, Group_Name);
            checkStatement.setString(2, Path_Name);

            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                int valid = resultSet.getInt("Valid");
                if (valid == 1) {
                    throw new SQLException("Group is valid and cannot be deleted.");
                }
            }

            // Proceed to delete the group if it is not valid (Valid = 0)
            String sql = "DELETE FROM essect_groups WHERE Group_Name = ? AND Path_Name = ? AND Valid = 0";
            PreparedStatement deleteStatement = connection.prepareStatement(sql);
            deleteStatement.setString(1, Group_Name);
            deleteStatement.setString(2, Path_Name);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public ObservableList<Subject> SELECT_SUBJECTS_BY_PATH(String Path_Name) {
        ObservableList<Subject> SubjectsList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Subject_Name FROM essect_subjects WHERE Path_Name=? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Path_Name);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String SubjectName = resultSet.getString("Subject_Name");

                Subject Subject_X = new Subject();
                Subject_X.setSubjectName(SubjectName);
                SubjectsList.add(Subject_X);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SubjectsList;
    }

    public ObservableList<Group> SELECT_ALL_GROUPS() {
        ObservableList<Group> GroupList = FXCollections.observableArrayList();

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT distinct Group_Name, Path_Name FROM essect_groups";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String GroupName = resultSet.getString("Group_Name");
                String PathName = resultSet.getString("Path_Name");

                Group Group_X = new Group();
                Group_X.setGroupeName(GroupName);
                Group_X.setPath(PathName);

                ObservableList<Subject> SubjectsList = FXCollections.observableArrayList();
                String sql2 = "SELECT Professor_Name, Subject_Name FROM essect_professors_affectation WHERE Group_Name=? AND Path_name=?";
                try (PreparedStatement statement2 = connection.prepareStatement(sql2)) {
                    statement2.setString(1, GroupName);
                    statement2.setString(2, PathName);

                    try (ResultSet resultSet2 = statement2.executeQuery()) {
                        while (resultSet2.next()) {
                            Subject Subject_X = new Subject();
                            Subject_X.setSubjectName(resultSet2.getString("Subject_Name"));
                            Subject_X.setPathname(PathName);
                            Professor Professor_X = new Professor(resultSet2.getString("Professor_Name"),"");
                            Subject_X.setAssignedProfessor(Professor_X);
                            SubjectsList.add(Subject_X);
                        }
                    }
                }
                Group_X.setPathSubjectList(SubjectsList);

                ObservableList<Student> studentList = FXCollections.observableArrayList();
                String sql3 = "SELECT Student_Username, Student_Path, Student_Group FROM essect_students WHERE Student_Group=? AND Student_Path=?";
                try (PreparedStatement statement3 = connection.prepareStatement(sql3)) {
                    statement3.setString(1, GroupName);
                    statement3.setString(2, PathName);

                    try (ResultSet resultSet3 = statement3.executeQuery()) {
                        while (resultSet3.next()) {
                            Student Student_X = new Student();
                            Student_X.setUsername(resultSet3.getString("Student_Username"));
                            Group Group_Y = new Group();
                            Group_Y.setGroupeName(resultSet3.getString("Student_Group"));
                            Group_Y.setPath(resultSet3.getString("Student_Path"));
                            Student_X.setGroup(Group_Y);
                            studentList.add(Student_X);
                        }
                    }
                }
                Group_X.setGroupStudentList(studentList);
                GroupList.add(Group_X);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return GroupList;
    }

    public ObservableList<Student> SELECT_UNAFFECTED_STUDENTS_BY_PATH(String Path_Name) {
        ObservableList<Student> Unaffected_StudentsList = FXCollections.observableArrayList(); // Initialize observable list
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT Student_Username,Student_Password FROM essect_students WHERE Student_Path=? AND Student_Group IS NULL";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Path_Name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String Studentusername = resultSet.getString("Student_Username");
                String Studentpassword = resultSet.getString("Student_Password");

                Student Student_X = new Student(Studentusername, Studentpassword);
                Path Path_X = new Path();
                Path_X.setPathname(Path_Name);
                Student_X.setPath(Path_X);
                Unaffected_StudentsList.add(Student_X);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Unaffected_StudentsList;
    }

    public ObservableList<Professor> SELECT_PROFESSORS_BY_SUBJECT(String Subject_Name) {
        ObservableList<Professor> ProfessorsList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT essect_professors.Professor_Username,essect_professors.Professor_Password FROM essect_professors,essect_professors_subjects WHERE essect_professors.Professor_Username=essect_professors_subjects.Professor_Name AND essect_professors_subjects.Subject_Name=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Subject_Name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String Professor_Username = resultSet.getString("Professor_Username");
                String Professor_Password = resultSet.getString("Professor_Password");
                Professor Professor_X = new Professor(Professor_Username,Professor_Password);
                ProfessorsList.add(Professor_X);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ProfessorsList;
    }

    public void UPDATE_PROF_AFFECTATION(String Newprofessor_Username, String OldProfessor_Username, String Subject_Name, String Group_Name) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "UPDATE essect_professors_affectation SET Professor_Name = ? WHERE Professor_Name = ? AND Subject_Name = ? AND Group_Name =? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Newprofessor_Username);
            statement.setString(2, OldProfessor_Username);
            statement.setString(3, Subject_Name);
            statement.setString(4, Group_Name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
