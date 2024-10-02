package com.devmate.java.Path_Groups_Subjects;
import com.devmate.java.Professor.Professor;

public class Subject {

    private String SubjectName;
    private Double Coefficient;
    private int TotalExams;
    private Path Path;
    private Professor AssignedProfessor;

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        this.SubjectName = subjectName;
    }

    public Double getSubjectCoefficient() {
        return Coefficient;
    }

    public void setSubjectCoefficient(Double Coefficient) {
        this.Coefficient = Coefficient;
    }

    public int getTotalExams() {
        return TotalExams;
    }

    public void setTotalExams(int totalExams) {
        this.TotalExams = totalExams;
    }

    public void setPathname(Path path) {
        this.Path=path;
    }

    public String getPathname() {
        return this.Path.getPathname();
    }

    public void setAssignedProfessor(Professor Prof) {
        this.AssignedProfessor = Prof;
    }

    public Professor getAssignedProfessor() {
        return this.AssignedProfessor;
    }

}
