package com.devmate.java;

import com.devmate.java.Student.Student;
import javafx.scene.control.Button;
import java.io.InputStream;
import java.time.LocalDateTime;

public class Exam extends Student {
    private String Subjectname;
    private String Pathname;
    private String Groupname;
    private LocalDateTime StartDate;
    private int Duration;
    private int ExamId;
    private InputStream ExamPDF;
    private InputStream ExamCorrectionPDF;
    private int ExamYear;
    private Double Score;
    private String AiResponse;
    private Student Condidate;
    private String ProfessorName;
    private String ExamAnswer;
    private int ExamMainId;

    public void setExamMainId(int ExamMainId) {
        this.ExamMainId = ExamMainId;
    }

    public int getExamMainId() {
        return ExamMainId;
    }

    public void setExamAnswer(String ExamAnswer) {
        this.ExamAnswer = ExamAnswer;
    }

    public String getExamAnswer() {
        return ExamAnswer;
    }

    public String getAiResponse() {
        return AiResponse;
    }

    public void setAiResponse(String AiResponse) {
        this.AiResponse = AiResponse;
    }

    public String getSubjectname() {
        return Subjectname;
    }

    public Double getScore() {
        return Score;
    }

    public void setScore(Double Score) {
        this.Score = Score;
    }

    public void setSubjectname(String subjectname) {
        this.Subjectname = subjectname;
    }

    public String getPathname() {
        return Pathname;
    }

    public void setPathname(String Pathname) {
        this.Pathname = Pathname;
    }

    public String getGroupname() {
        return Groupname;
    }

    public void setGroupname(String Groupname) {
        this.Groupname = Groupname;
    }

    public void setExamYear(int ExamYear) {
        this.ExamYear = ExamYear;
    }

    public int getExamYear() {
        return ExamYear;
    }

    public void setExamId(int ExamId) {
        this.ExamId = ExamId;
    }

    public int getExamId() {
        return ExamId;
    }

    public InputStream getExamCorrectionPDF() {
        return ExamCorrectionPDF;
    }

    public void setExamCorrectionPDF(InputStream ExamCorrectionPDF) {
        this.ExamCorrectionPDF = ExamCorrectionPDF;
    }

    public InputStream getExamPDF() {
        return ExamPDF;
    }

    public void setExamPDF(InputStream examPDF) {
        this.ExamPDF = examPDF;
    }

    public LocalDateTime getStartQuiz() {
        if (StartDate != null) {
            return StartDate;
        } else {
            return null; // or any other default value you prefer
        }
    }

    public void setStartQuiz(LocalDateTime start) {
        this.StartDate = start;
    }

    public int getDurationQuiz() {
        return Duration;
    }

    public void setDurationQuiz(int Duration) {
        this.Duration = Duration;
    }

    public void setCondidate(Student condidate) {
        this.Condidate = condidate;
    }

    public Student getCondidate() {
        return Condidate;
    }

    public void setProfessorName(String ProfessorName) {
        this.ProfessorName = ProfessorName;
    }

    public String getProfessorName() {
        return ProfessorName;
    }

    public Button getPassbutton() {
        return null;
    }

    public Button getViewExambutton() {
        return null;
    }

    public Button getViewCorrectbutton() {
        return null;
    }

    public Button getDeletebutton() {
        return null;
    }

    public Button getAiResponsebutton() {
        return null;
    }

    public Button getStudentExamAnswerbutton() {
        return null;
    }
}
