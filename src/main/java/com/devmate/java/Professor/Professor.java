package com.devmate.java.Professor;

import java.io.InputStream;
import java.sql.*;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import com.devmate.java.DatabaseManager;
import com.devmate.java.Exam;
import com.devmate.java.Exception.ExamsNumberExceededException;
import com.devmate.java.Exception.DeadlineExceededException;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Path_Groups_Subjects.Subject;
import com.devmate.java.Student.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Professor {

    private String Email;
    private String Username;
    private String Password;
    private ObservableList<Subject> Skills_List;
    private ObservableList<Group> Affectation_List;
    private int Total_Affectectation;

    public Professor(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getEmail() {
        return Email;
    }

    public void setSkills_List(ObservableList<Subject> SpecializationsListProfessor) {
        this.Skills_List = SpecializationsListProfessor;
    }

    public String getSkills_List() {
        if (Skills_List != null && !Skills_List.isEmpty()) {
            StringBuilder specializationsListBuilder = new StringBuilder();

            for (Subject subject : Skills_List) {
                specializationsListBuilder.append(subject.getSubjectName()).append(",");
            }

            // Remove the trailing comma
            specializationsListBuilder.deleteCharAt(specializationsListBuilder.length() - 1);

            return specializationsListBuilder.toString();
        }
        return ""; // Return an empty string if SpecializationsList is null or empty
    }

    public void setAffectation_List(ObservableList<Group> GroupListProfessor) {
        this.Affectation_List = GroupListProfessor;
    }

    public ObservableList<Group> getAffectation_List() {
        return Affectation_List;
    }

    public void setTotal_Affectectation(int count) {
        this.Total_Affectectation = count;
    }

    public int getTotal_Affectectation() {
        return Total_Affectectation;
    }

    public void INSERT_EXAM(Exam exam) throws ExamsNumberExceededException, SQLException {

        String sqlTotalExams = "SELECT TotalExams FROM essect_subjects WHERE Subject_Name = ?";
        String sqlVerify = "SELECT COUNT(*) FROM essect_exam WHERE Professor_Username = ? AND Path_Name = ? AND Group_Name = ? AND Subject_Name = ? AND Year = ?";
        String sqlInsert = "INSERT INTO essect_exam (ID,Exam_file, Exam_correction, Path_Name, Group_Name, Subject_Name, Professor_Username, Year) VALUES (?,?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement totalExamsStatement = connection.prepareStatement(sqlTotalExams);
             PreparedStatement verifyStatement = connection.prepareStatement(sqlVerify)) {

            // Get the TotalExams value for the subject
            totalExamsStatement.setString(1, exam.getSubjectname());
            ResultSet totalExamsResultSet = totalExamsStatement.executeQuery();

            if (!totalExamsResultSet.next()) {
                throw new SQLException("Subject not found");
            }

            int totalExamsLimit = totalExamsResultSet.getInt(1);

            // Verify the number of exams already inserted
            verifyStatement.setString(1, exam.getProfessorName());
            verifyStatement.setString(2, exam.getPathname());
            verifyStatement.setString(3, exam.getGroupname());
            verifyStatement.setString(4, exam.getSubjectname());
            int currentYear = Year.now().getValue();
            verifyStatement.setInt(5, currentYear);

            ResultSet verifyResultSet = verifyStatement.executeQuery();
            verifyResultSet.next(); // Move to the first row
            int currentExamsCount = verifyResultSet.getInt(1);

            // If the current exams count is less than the limit, proceed with insertion
            if (currentExamsCount < totalExamsLimit) {
                try (PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {

                    InputStream examPDFStream = exam.getExamPDF();
                    InputStream examCorrectionPDFStream = exam.getExamCorrectionPDF();

                    insertStatement.setInt(1, currentExamsCount+1);
                    insertStatement.setBlob(2, examPDFStream);
                    insertStatement.setBlob(3, examCorrectionPDFStream);
                    insertStatement.setString(4, exam.getPathname());
                    insertStatement.setString(5, exam.getGroupname());
                    insertStatement.setString(6, exam.getSubjectname());
                    insertStatement.setString(7, exam.getProfessorName());
                    insertStatement.setInt(8, currentYear);

                    insertStatement.executeUpdate();
                }
            } else {
                throw new ExamsNumberExceededException("The maximum number of exams for this subject has been reached.");
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public ObservableList<Exam> SELECT_EXAMS (String Professor_Username) {
        ObservableList<Exam> ExamsList = FXCollections.observableArrayList();
        String sql = "SELECT MainID,ID,Exam_file, Exam_correction, Path_Name, Group_Name, Subject_Name,Year FROM essect_exam WHERE Professor_Username = ?";

        try (Connection connection = DatabaseManager.getConnection()) {

            PreparedStatement stmtDistinct = connection.prepareStatement(sql);
                stmtDistinct.setString(1, Professor_Username);

                ResultSet rsDistinct = stmtDistinct.executeQuery();

                    while (rsDistinct.next()) {

                        Exam Exam_X = new Exam();
                        Exam_X.setExamId(rsDistinct.getInt("ID"));
                        Exam_X.setSubjectname(rsDistinct.getString("Subject_Name"));
                        Exam_X.setPathname(rsDistinct.getString("Path_Name"));
                        Exam_X.setGroupname(rsDistinct.getString("Group_Name"));
                        Exam_X.setExamYear(rsDistinct.getInt("Year"));
                        Exam_X.setProfessorName(Professor_Username);
                        Exam_X.setExamMainId(rsDistinct.getInt("MainID"));
                        Blob examFileBlob = rsDistinct.getBlob("Exam_file");
                        if (examFileBlob != null) {
                            InputStream examFileInputStream = examFileBlob.getBinaryStream();
                            Exam_X.setExamPDF(examFileInputStream);
                        }

                        Blob examCorrectionFileBlob = rsDistinct.getBlob("Exam_correction");
                        if (examCorrectionFileBlob != null) {
                            InputStream examCorrectionFileInputStream = examCorrectionFileBlob.getBinaryStream();
                            Exam_X.setExamCorrectionPDF(examCorrectionFileInputStream);
                        }

                        ExamsList.add(Exam_X);
                    }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ExamsList;
    }

    public ObservableList<Student> SELECT_STUDENTS_SCORES(Professor Professor_X) {
        ObservableList<Student> studentsList = FXCollections.observableArrayList();
        Map<String, Student> studentsMap = new HashMap<>();

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT essect_exam.MainID,essect_exam.ID,essect_exam.Group_Name, essect_exam.Path_Name, essect_exam.Subject_Name, essect_exam_results.Student_answer, essect_exam_results.Score, essect_exam_results.AI_response, essect_exam_results.Student_Username " +
                    "FROM essect_exam " +
                    "JOIN essect_exam_results ON essect_exam_results.IDExam = essect_exam.MainID " +
                    "WHERE essect_exam.Professor_Username = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Professor_X.getUsername());
            ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        String username = rs.getString("Student_Username");
                        Student Student_X = studentsMap.get(username);

                        if (Student_X == null) {
                            Student_X = new Student();
                            Group Group_X = new Group();
                            Group_X.setGroupeName(rs.getString("Group_Name"));
                            Path Path_X = new Path ();
                            Path_X.setPathname(rs.getString("Path_Name"));
                            Group_X.setPath(Path_X);
                            Student_X.setUsername(username);
                            Student_X.setGroup(Group_X);
                            Student_X.setExamsList(FXCollections.observableArrayList()); // Initialize and set examsList
                            studentsMap.put(username, Student_X);
                        }

                        Exam Exam_X = new Exam();
                        Exam_X.setSubjectname(rs.getString("Subject_Name"));
                        Exam_X.setExamAnswer(rs.getString("Student_answer"));
                        Exam_X.setScore(rs.getDouble("Score"));
                        Exam_X.setAiResponse(rs.getString("AI_response"));
                        Exam_X.setExamId(rs.getInt("ID"));
                        Exam_X.setExamMainId(rs.getInt("MainID"));

                        ObservableList<Exam> updatedExamsList = FXCollections.observableArrayList(Student_X.getExamsList());
                        updatedExamsList.add(Exam_X);
                        Student_X.setExamsList(updatedExamsList); // Replace the old list with the updated one
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        studentsList.addAll(studentsMap.values());
        return studentsList;
    }

    public void UPDATE_STUDENT_SCORE(Double NewScore, int ExamMainID, String Student_Username) throws SQLException, DeadlineExceededException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String StudentQuery = "SELECT FinalGrade FROM essect_students WHERE Student_Username = ?";
            PreparedStatement Studentstatement = connection.prepareStatement(StudentQuery);
            Studentstatement.setString(1, Student_Username);
            ResultSet Studentrs = Studentstatement.executeQuery();

            if (Studentrs.next()) {  // Check if a record exists
                Double finalGrade = Studentrs.getDouble("FinalGrade");
                if (Studentrs.wasNull()) {

                    String sql = "UPDATE essect_exam_results SET Score = ? WHERE IDExam = ? AND Student_Username=?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setDouble(1, NewScore);
                    statement.setInt(2, ExamMainID);
                    statement.setString(3, Student_Username);

                    statement.executeUpdate();
                } else {
                    throw new DeadlineExceededException("The deadline for updating the student's score has passed. Further modifications are not permitted.");

                }
            }

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void DELETE_EXAM(int ExamMainID,String Professor_Username) throws SQLException {
        String sql = "DELETE FROM essect_exam WHERE MainID=? and Professor_Username = ? ";

        try (Connection connection = DatabaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, ExamMainID);
            statement.setString(2, Professor_Username);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}




