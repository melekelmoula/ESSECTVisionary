package com.devmate.java.Path_Groups_Subjects;
import com.devmate.java.Student.Student;
import javafx.collections.ObservableList;

public class Group {

    private String GroupeName;
    private ObservableList<Student> GroupStudentList;
    Path Path ;

    public String getPathname() {
        return this.Path.getPathname(); // Calls the getPath method from Path class
    }

    public void setPath(Path path) {
        this.Path=path;
    }

    public String getGroupeName() {
        return this.GroupeName;
    }

    public void setGroupeName(String Group) {
        this.GroupeName = Group;
    }

    public void setGroupStudentList(ObservableList<Student> StudentList) {
        this.GroupStudentList = StudentList;
    }

    public ObservableList<Student> getGroupStudentList() {
        return GroupStudentList;
    }

    public Object getGroupStudentsCount() {
        if (getGroupStudentList()!=null) {
            return getGroupStudentList().size();
        }
        else return null;
    }

    public ObservableList<Subject> getSubjectsList() {
        if (this.Path != null) {
            return this.Path.getSubjectsList();
        } else {
            return null;
        }
    }

    
}
