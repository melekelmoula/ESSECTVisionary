package com.devmate.java.Student;
import com.devmate.java.DatabaseManager;
import com.devmate.java.Exam;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import javafx.collections.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class Student {

    private String Username;
    private String Password;
    private String Email;
    private String Gender;
    private LocalDate Birthdate;
    private Group Group;
    private Path Path;
    private Double FinalGrade;
    private ObservableList<Exam> ExamsList;

    public Student(String username, String password) {
        this.Username = username;
        this.Password = password;}

    public Student() {}

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Name) {
        this.Email = Name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Sex) {
        this.Gender = Sex;
    }

    public void setBirthdate(LocalDate Birthdate) {
        this.Birthdate = Birthdate;
    }

    public LocalDate getBirthdate() {
        return this.Birthdate;
    }

    public void setGroup(Group group) {
        this.Group = group;
    }

    public Group getGroup (){
        return this.Group;
    }

    public void setPath(Path path) {
        this.Path = path;
    }

    public Path getPath (){
        return this.Path;
    }

    public void setFinalGrade(Double FinalGrade) {
        this.FinalGrade = FinalGrade;
    }

    public Double getFinalGrade() {
        return this.FinalGrade;
    }


    public ObservableList<Exam> getExamsList() {
        return ExamsList;
    }

    public void setExamsList(ObservableList<Exam> examsList) {
        this.ExamsList = examsList;
    }

    public ObservableList<Exam> SELECT_EXAMFILES(Student Student_X) {
        String query = "SELECT MainID,ID, Subject_Name, Path_Name, Group_Name, Startdate, Duration, Exam_file, Professor_Username " +
                "FROM essect_exam " +
                "WHERE Path_Name = ? AND Group_Name = ? AND Year = YEAR(CURDATE()) " +
                "AND MainID NOT IN (SELECT IDExam FROM essect_exam_results WHERE Student_Username = ?)";

        ObservableList<Exam> examExams = FXCollections.observableArrayList();

        try (Connection connection = DatabaseManager.getConnection()) {
             PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, Student_X.getGroup().getPathname());
            statement.setString(2, Student_X.getGroup().getGroupeName());
            statement.setString(3, Student_X.getUsername());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Timestamp startDate = rs.getTimestamp("Startdate");
                Integer duration = (Integer) rs.getObject("Duration"); // Get the value as Integer object

                if (startDate != null && duration != null) {
                    LocalDateTime startDateTime = startDate.toLocalDateTime();

                    Exam Exam_X = new Exam();
                    Exam_X.setExamId(rs.getInt("ID"));
                    Exam_X.setSubjectname(rs.getString("Subject_Name"));
                    Exam_X.setPathname(rs.getString("Path_Name"));
                    Exam_X.setGroupname(rs.getString("Group_Name"));
                    Exam_X.setProfessorName(rs.getString("Professor_Username"));
                    Exam_X.setStartQuiz(startDateTime);
                    Exam_X.setDurationQuiz(duration);
                    Exam_X.setExamMainId(rs.getInt("MainID"));

                    Blob examFileBlob = rs.getBlob("Exam_file");
                    if (examFileBlob != null) {
                        InputStream examFileInputStream = examFileBlob.getBinaryStream();
                        Exam_X.setExamPDF(examFileInputStream);
                    }
                    examExams.add(Exam_X);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL error while retrieving files: " + e.getMessage());
        }

        return examExams;
    }


    public void SELECT_EXAM_CORRECTION(Exam StudentExam) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT Exam_correction FROM essect_exam WHERE MainID=?";
            PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, StudentExam.getExamMainId());
            System.out.println(StudentExam.getExamMainId());

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Blob examFileBlob = rs.getBlob("Exam_correction");
                            File ProfessorExamCorrectionFile = File.createTempFile("exam", ".pdf");
                            try (InputStream examFileInputStream = examFileBlob.getBinaryStream();
                                PDDocument document = PDDocument.load(examFileInputStream)) {
                                document.save(ProfessorExamCorrectionFile);
                                OllamaCALL(ProfessorExamCorrectionFile,StudentExam);
                            } finally {
                                ProfessorExamCorrectionFile.delete();
                            }

                    }
                }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void OllamaCALL(File ProfessorExamCorrectionFILE, Exam StudentExam) throws IOException {
        PDDocument document = PDDocument.load(ProfessorExamCorrectionFILE);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.getText(document);
        String ProfessorExamCorrection = pdfStripper.getText(document);

        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        String StudentExamAnswer = StudentExam.getExamAnswer();
        String MainFiles = "StartStudentAnswerSection"+StudentExamAnswer+"EndStudentAnswerSection"+"StartCorrectionSection"+ProfessorExamCorrection+"EndCorrectionSection";

        String ExamGuidelines ="Each exam consists of multiple exercises, with each exercise containing several questions. The student must answer every question in the exam. If a question or exercise is left unanswered or empty or not mentioned in student answers, it will be considered incorrect, and a score of 0 will be assigned for that specific question or exercise"
                                +"Each question in the exam is assigned a specific number of points, contributing to the overall score of 20. The total points available across all questions will sum up to 20, making the exam's total is 20";

        String TaskDescription="Your task is to compare the student's answers with the professor's corrections to determine the student's score. The student's answers will be provided between StartStudentAnswerSection and EndStudentAnswerSection. and the professor's corrections will be provided between the StartCorrectionSection and EndCorrectionSection."
                                +"re write whatsbetween the StartCorrectionSection and EndCorrectionSection as it it without changing anything "
                                +"1-Identify Missing Answers: If the student skips any question or exercise, it should be marked as incorrect with a score of 0. display the student answer and the professor correct answer and the score provided" +
                                "2-Compare each answer provided by the student with the corresponding correct answer from the professor's correction section if the student answer is correct he will get question speicifc number of points else 0" +
                                "3-Calculate Score For each correctly answered question, award the points assigned to that question.Sum up the points for all questions to calculate the total score you need to be as accurate as possible while doing the sum of the score ." +
                                "4-Output the Final Score:The final score will a number between 0 and 20 , reflecting the student's performance on the exam. mark the final score only which contain only numbers between ** you dont need to convert it to pourcentage ";

        // Create JSON input string
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("model", "llama3.1");
        jsonInput.put("prompt", MainFiles+ExamGuidelines+TaskDescription);
        jsonInput.put("stream", false);

        // Write request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            String responsetext = jsonResponse.getString("response");

            System.out.println(responsetext);

            INSERT_STUDENT_EXAM_SCORE(StudentExam,responsetext);

        } catch (IOException e) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line.trim());
                }
                System.err.println("Error Response: " + errorResponse.toString());
            }
        } finally {
            conn.disconnect();
        }

    }

    public void INSERT_STUDENT_EXAM_SCORE(Exam StudentExam,String AIResponse) {

        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(AIResponse);

        Double extractedScore = 0.0;
        String finalMatch = null;

        while (matcher.find()) {
            finalMatch = matcher.group(1);
        }

        if (finalMatch != null) {
            try {
                extractedScore = Double.parseDouble(finalMatch);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse the score: " + finalMatch);
            }
        }

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO essect_exam_results (IDExam, Student_answer, Score, AI_response,Student_Username) VALUES ( ?, ?, ?, ?, ?)";
            try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                statement2.setInt(1, StudentExam.getExamMainId());
                statement2.setString(2, StudentExam.getExamAnswer());
                statement2.setDouble(3, extractedScore);
                statement2.setString(4, AIResponse);
                statement2.setString(5, StudentExam.getCondidate().getUsername());
                statement2.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Student SELECT_STUDENT_RESULT_FROM_TABLE(Student Student_X) {

        try (Connection connection = DatabaseManager.getConnection()) {

            String selectFinalGradeSql = "SELECT FinalGrade FROM essect_students WHERE Student_Username = ?";
            try (PreparedStatement selectFinalGradeStatement = connection.prepareStatement(selectFinalGradeSql)) {
                selectFinalGradeStatement.setString(1, Student_X.getUsername());
                try (ResultSet resultSetFinalGrade = selectFinalGradeStatement.executeQuery()) {
                    if (resultSetFinalGrade.next()) {
                        Double finalGrade = resultSetFinalGrade.getDouble("FinalGrade");
                        if (!resultSetFinalGrade.wasNull()) {
                            Student_X.setFinalGrade(finalGrade);
                        }
                    }
                }
            }

            if (Student_X.getFinalGrade() != null) {
                String selectExamResultsSql = "SELECT e.ID,er.IDExam, er.Score, e.Subject_Name " +
                        "FROM essect_exam_results er " +
                        "JOIN essect_exam e ON er.IDExam = e.MainID " +
                        "WHERE er.Student_Username = ?";
                try (PreparedStatement selectExamResultsStatement = connection.prepareStatement(selectExamResultsSql)) {
                    selectExamResultsStatement.setString(1, Student_X.getUsername());
                    try (ResultSet resultSetExamResults = selectExamResultsStatement.executeQuery()) {
                        ObservableList<Exam> examsList = FXCollections.observableArrayList();

                        while (resultSetExamResults.next()) {
                            Double score = resultSetExamResults.getDouble("Score");
                            int examMainId = resultSetExamResults.getInt("IDExam");
                            String subject = resultSetExamResults.getString("Subject_Name");
                            int examID = resultSetExamResults.getInt("ID");

                            Exam Exam_X = new Exam();
                            Exam_X.setSubjectname(subject);
                            Exam_X.setExamId(examID);
                            Exam_X.setScore(score);
                            Exam_X.setExamMainId(examMainId);
                            examsList.add(Exam_X);
                        }

                        Student_X.setExamsList(examsList);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Student_X;
    }


        /* BACKUP WORKING AZURE
        public void chatGPT(String Text) {
        String azureOpenaiKey = "7212ff351fba443f9558993f69fb4568";
        String endpoint = "https://javaessect.openai.azure.com";
        String deploymentName = "gpt-35-turbo-0301";

        // Initialize the OpenAI client
        OpenAIClientBuilder builder  = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey));

        OpenAIClient client = builder.buildClient();

        List<ChatRequestMessage> prompts = new ArrayList<>();
        prompts.add(new ChatRequestUserMessage(Text+"combient le total de cette exam"));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentName, new ChatCompletionsOptions(prompts));

        for (ChatChoice choice : chatCompletions.getChoices()) {

            ChatResponseMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            System.out.println(message.getContent());
        }
        }
        */

}