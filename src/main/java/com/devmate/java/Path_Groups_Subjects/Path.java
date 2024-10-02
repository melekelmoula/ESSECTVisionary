package com.devmate.java.Path_Groups_Subjects;
import com.devmate.java.Student.Student;
import javafx.collections.ObservableList;

public class Path {

    private String Pathname;
    private ObservableList<Group> Groupslist;
    private ObservableList<Subject> SubjectsList;
    private ObservableList<Student> Studentslist;

    public ObservableList<Group> getGroupslist() {
        return Groupslist;
    }

    public void setGroupslist(ObservableList<Group> Grouplist) {
        this.Groupslist = Grouplist;
    }

    public ObservableList<Student> getStudentslist() {
        return Studentslist;
    }

    public void setStudentslist(ObservableList<Student> Studentlist) {
        this.Studentslist = Studentlist;
    }

    public ObservableList<Subject> getSubjectsList() {
        return SubjectsList;
    }

    public void setSubjectsList(ObservableList<Subject> SubjectList) {
        this.SubjectsList = SubjectList;
    }

    public void setPathname(String Path) {
        this.Pathname = Path;
    }

    public String getPathname() {
        return this.Pathname;
    }

}
