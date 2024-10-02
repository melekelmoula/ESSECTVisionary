package com.devmate.java.Admin;

import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Path_Groups_Subjects.Subject;
import com.devmate.java.Professor.Professor;
import com.devmate.java.Student.Student;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.CheckComboBox;
import java.sql.SQLException;
import java.util.function.Consumer;

public class AdminGroupsTableController {

    @FXML
    private Pane Panel;
    @FXML
    private TreeTableView <Group> Table;
    @FXML
    private TreeTableColumn<Group, String> GroupColumn;
    @FXML
    private TreeTableColumn<Group, String> PathColumn;
    @FXML
    private TreeTableColumn<Group, String> StudentCount;
    @FXML
    private TextField NewGroupField;
    @FXML
    private MFXLegacyComboBox PathComboBox;
    @FXML
    private CheckComboBox StudentCheckComboBox;
    @FXML
    private TextField TextfieldDesign;
    @FXML
    private MFXLegacyComboBox ReplacingProfessorComboBox;
    @FXML
    private MFXButton AddButton;
    @FXML
    private MFXButton NextButton;
    @FXML
    private MFXButton BackButton;
    @FXML
    private MFXButton UpdateButton;
    @FXML
    private MFXButton DeleteButton;
    @FXML
    private HBox ProfessorAffectationContainer1;
    @FXML
    private HBox ProfessorAffectationContainer2;

    @FXML
    private Label errorMessageLabel;
    private Admin Admin;
    public Boolean Theme;

    public void initialize(Admin Admin , Boolean Theme) {

        this.Admin = Admin;
        this.Theme = Theme;
        Extra_Option();

        PathColumn.setCellValueFactory(param -> {
            Group professor = param.getValue().getValue();
            if (professor != null) {
                if (professor.getPathname() != null) {
                    return new SimpleStringProperty("    " + professor.getPathname());
                }
            }
            return new SimpleStringProperty("");
        });

        GroupColumn.setCellValueFactory(param -> {
            Group professor = param.getValue().getValue();
            if (professor != null&&professor.getGroupeName()!=null) {
                return new SimpleStringProperty(professor.getGroupeName() + "  ");
            }
            return new SimpleStringProperty("");
        });

        StudentCount.setCellValueFactory(param -> {
            Group group = param.getValue().getValue();
            if (group != null) {
                String groupName = group.getGroupeName();
                if (groupName != null && !groupName.isEmpty() && !groupName.contains("List")) {
                    Object studentCount = group.getGroupStudentsCount();
                    return new SimpleObjectProperty<>(studentCount).asString();
                }
            }
            return new SimpleObjectProperty<>("").asString();
        });

        ObservableList<String> pathsList = FXCollections.observableArrayList();
        ObservableList<Group> groupLists = Admin.SELECT_ALL_GROUPS();
        TreeItem<Group> root = new TreeItem<>(new Group()); // Create a root item

        for (Group groupX : groupLists) {
            String pathName = groupX.getPathname();

            if (!pathsList.contains(pathName)) {
                pathsList.add(pathName);

                TreeItem<Group> pathItem = new TreeItem<>(new Group() {
                    @Override
                    public String getPathname() {
                        return pathName;
                    }
                });
                root.getChildren().add(pathItem);

                for (Group groupY : groupLists) {
                    if (groupY.getPathname().equals(pathName)) {
                        TreeItem<Group> groupItem = new TreeItem<>(new Group() {
                            @Override
                            public String getGroupeName() {
                                return groupY.getGroupeName();
                            }
                            @Override
                            public Object getGroupStudentsCount() {
                                return groupY.getGroupStudentsCount();
                            }
                        });
                        pathItem.getChildren().add(groupItem);

                        TreeItem<Group> professorsItem = new TreeItem<>(new Group() {
                            @Override
                            public String getGroupeName() {
                                return "Professors List";
                            }
                        });
                        groupItem.getChildren().add(professorsItem);

                        TreeItem<Group> studentsItem = new TreeItem<>(new Group() {
                            @Override
                            public String getGroupeName() {
                                return "Students List";
                            }
                        });
                        groupItem.getChildren().add(studentsItem);

                        for (Subject subjectX : groupY.getPathSubjectList()) {
                            TreeItem<Group> subjectItem = new TreeItem<>(new Group() {
                                @Override
                                public String getGroupeName() {
                                    return subjectX.getSubjectName() + ":" + subjectX.getAssignedProfessor().getUsername();
                                }
                                @Override
                                public Object getGroupStudentsCount() {
                                    return "";
                                }
                            });
                            professorsItem.getChildren().add(subjectItem);
                        }

                        for (Student studentX : groupY.getGroupStudentList()) {
                            TreeItem<Group> studentItem = new TreeItem<>(new Group() {
                                @Override
                                public String getGroupeName() {
                                    return studentX.getUsername();
                                }
                                @Override
                                public Object getGroupStudentsCount() {
                                    return "";
                                }
                            });
                            studentsItem.getChildren().add(studentItem);
                        }
                    }
                }
            }
        }
        Table.setRoot(root);
        Table.setShowRoot(false); // Optionally, if you don't want to show the root item
        Table.getColumns().setAll(PathColumn,GroupColumn, StudentCount);

        final boolean[] allSelected = {false, false};
        final String[] Path_Name = new String[1];
        final boolean[] SubjectItemsCount = new boolean[1];

        PathComboBox.setOnAction(event -> {
            ProfessorAffectationContainer1.getChildren().clear();
            ProfessorAffectationContainer2.getChildren().clear();
            NextButton.setDisable(true);
            SubjectItemsCount[0]=false;
            if (PathComboBox.getSelectionModel().getSelectedItem() != null) {
                Path_Name[0] = (String) PathComboBox.getSelectionModel().getSelectedItem();
                ObservableList<Student> StudentList = Admin.SELECT_UNAFFECTED_STUDENTS_BY_PATH(Path_Name[0]);

                StudentCheckComboBox.getItems().clear();
                for (Student addedItem : StudentList) {
                    StudentCheckComboBox.getItems().add(addedItem.getUsername());
                }

                ObservableList<Subject> GroupList = Admin.SELECT_SUBJECTS_BY_PATH(Path_Name[0]);

                if (!GroupList.isEmpty()) {
                    SubjectItemsCount[0]=true;

                    ProfessorAffectationContainer1.setSpacing(20);

                    for (Subject group : GroupList.subList(0, GroupList.size()/2)) {
                        VBox subjectBox = new VBox();
                        subjectBox.setSpacing(5);

                        Label subjectname = new Label(group.getSubjectName());
                        subjectname.setId("subjectLabel");
                        subjectname.setTextFill(Color.WHITE); // Set text color to white

                        subjectname.hoverProperty().addListener((observable, oldValue, newValue) -> {

                            Tooltip tooltip = new Tooltip(group.getSubjectName());
                            subjectname.setTooltip(tooltip);

                        });

                        subjectBox.getChildren().add(subjectname); // Add subject name label to subjectBox
                        MFXLegacyComboBox<String> profComboBox = new MFXLegacyComboBox<>();
                        profComboBox.setId("subjectivities");
                        profComboBox.setUserData(group.getSubjectName());

                        ObservableList<Professor> profList = Admin.SELECT_PROFESSORS_BY_SUBJECT(group.getSubjectName());
                        for (Professor Professor_X : profList) {
                            profComboBox.getItems().add(Professor_X.getUsername()); // Add professors to ComboBox
                        }
                        profComboBox.setPrefWidth(150); // Set preferred width for ComboBox (adjust width as needed)

                        profComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {

                            for (Node node : ProfessorAffectationContainer1.getChildren()) {
                                if (node instanceof VBox) {
                                    for (Node child : ((VBox) node).getChildren()) {
                                        if (child instanceof MFXLegacyComboBox) {
                                            MFXLegacyComboBox<?> comboBox = (MFXLegacyComboBox<?>) child;
                                            if (comboBox.getValue() != null) {
                                                allSelected[0] = true;
                                            } else allSelected[0] = false;
                                        }
                                    }
                                }
                            }
                            if (allSelected[0]) {
                                NextButton.setDisable(false);
                            }
                        });

                        subjectBox.getChildren().add(profComboBox); // Add ComboBox to subjectBox
                        ProfessorAffectationContainer1.getChildren().add(subjectBox); // Add subjectRow to subjectcontainer

                    }

                    ProfessorAffectationContainer2.setSpacing(20); // Optional: Set spacing between subjects

                    for (Subject group2 : GroupList.subList(GroupList.size()/2, GroupList.size())) {

                        VBox subjectBox2 = new VBox(); // Create a VBox for each subject
                        subjectBox2.setSpacing(5); // Optional: Set spacing inside the subject VBox

                        Label subjectname2 = new Label(group2.getSubjectName());
                        subjectname2.setId("subjectLabel");
                        subjectname2.setTextFill(Color.WHITE); // Set text color to white
                        subjectname2.hoverProperty().addListener((observable, oldValue, newValue) -> {

                            Tooltip tooltip = new Tooltip(group2.getSubjectName());
                            subjectname2.setTooltip(tooltip);

                        });
                        subjectBox2.getChildren().add(subjectname2); // Add subject name label to subjectBox

                        MFXLegacyComboBox<String> profComboBox2 = new MFXLegacyComboBox<>();
                        profComboBox2.setId("subjectivities");
                        profComboBox2.setUserData(group2.getSubjectName());

                        ObservableList<Professor> profList = Admin.SELECT_PROFESSORS_BY_SUBJECT(group2.getSubjectName());
                        for (Professor Professor_X : profList) {
                            profComboBox2.getItems().add(Professor_X.getUsername()); // Add professors to ComboBox
                        }

                        profComboBox2.setPrefWidth(150); // Set preferred width for ComboBox (adjust width as needed)
                        profComboBox2.valueProperty().addListener((obs, oldVal, newVal) -> {

                            for (Node node : ProfessorAffectationContainer2.getChildren()) {
                                if (node instanceof VBox) {
                                    for (Node child : ((VBox) node).getChildren()) {
                                        if (child instanceof MFXLegacyComboBox) {
                                            MFXLegacyComboBox<?> comboBox = (MFXLegacyComboBox<?>) child;
                                            if (comboBox.getValue() != null) {
                                                allSelected[1] = true;
                                            } else allSelected[1] = false;
                                        }
                                    }
                                }
                            }
                            if (allSelected[1]) {
                                AddButton.setDisable(false);
                            }
                        });

                        subjectBox2.getChildren().add(profComboBox2); // Add ComboBox to subjectBox
                        ProfessorAffectationContainer2.getChildren().add(subjectBox2); // Add subjectRow to subjectcontainer
                    }
                }

            }
        });

                StudentCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
                    if (!StudentCheckComboBox.getCheckModel().isEmpty() && !PathComboBox.getSelectionModel().isEmpty() && !NewGroupField.getText().isEmpty()&&SubjectItemsCount[0]) {
                        NextButton.setDisable(false);
                    } else {
                        NextButton.setDisable(true);
                    }
                });

        NewGroupField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!NewGroupField.getText().isEmpty() && !StudentCheckComboBox.getCheckModel().isEmpty() && !PathComboBox.getSelectionModel().isEmpty()&& !NewGroupField.getText().isEmpty()&&SubjectItemsCount[0]) {
                NextButton.setDisable(false);
            } else {
                NextButton.setDisable(true);
            }
        });


        NextButton.setOnAction(event -> {
            if (!ProfessorAffectationContainer1.isVisible()) {
                if (allSelected[0]) {
                    NextButton.setDisable(false);
                } else NextButton.setDisable(true);
                NextButton.setVisible(true);
                NewGroupField.setDisable(true);
                NewGroupField.setVisible(false);
                PathComboBox.setDisable(true);
                PathComboBox.setVisible(false);
                StudentCheckComboBox.setDisable(true);
                StudentCheckComboBox.setVisible(false);
                ProfessorAffectationContainer1.setDisable(false);
                ProfessorAffectationContainer1.setVisible(true);
                TextfieldDesign.setVisible(false);
                BackButton.setDisable(false);
                BackButton.setVisible(true);
                AddButton.setVisible(false);
                AddButton.setDisable(true);
                NextButton.setLayoutX(830);


            } else if (ProfessorAffectationContainer1.isVisible()) {
                NewGroupField.setDisable(true);
                NewGroupField.setVisible(false);
                PathComboBox.setDisable(true);
                PathComboBox.setVisible(false);
                StudentCheckComboBox.setDisable(true);
                StudentCheckComboBox.setVisible(false);
                TextfieldDesign.setVisible(false);
                ProfessorAffectationContainer1.setDisable(true);
                ProfessorAffectationContainer1.setVisible(false);
                ProfessorAffectationContainer2.setDisable(false);
                ProfessorAffectationContainer2.setVisible(true);
                NextButton.setDisable(true);
                NextButton.setVisible(false);
                BackButton.setDisable(false);
                BackButton.setVisible(true);
                if (allSelected[1]) {
                    AddButton.setDisable(false);
                } else AddButton.setDisable(true);
                AddButton.setVisible(true);
            }
        });

        BackButton.setOnAction(event -> {
            if (ProfessorAffectationContainer1.isVisible()) {
                NewGroupField.setDisable(false);
                NewGroupField.setVisible(true);
                PathComboBox.setDisable(false);
                PathComboBox.setVisible(true);
                StudentCheckComboBox.setDisable(false);
                StudentCheckComboBox.setVisible(true);
                TextfieldDesign.setVisible(true);
                ProfessorAffectationContainer1.setDisable(true);
                ProfessorAffectationContainer1.setVisible(false);
                NextButton.setDisable(false);
                NextButton.setVisible(true);
                BackButton.setDisable(true);
                BackButton.setVisible(false);
                AddButton.setDisable(true);
                AddButton.setVisible(false);
                NextButton.setLayoutX(613);
            } else if (ProfessorAffectationContainer2.isVisible()) {
                NewGroupField.setDisable(true);
                NewGroupField.setVisible(false);
                PathComboBox.setDisable(true);
                PathComboBox.setVisible(false);
                StudentCheckComboBox.setDisable(true);
                StudentCheckComboBox.setVisible(false);
                TextfieldDesign.setVisible(false);
                ProfessorAffectationContainer1.setDisable(false);
                ProfessorAffectationContainer1.setVisible(true);
                ProfessorAffectationContainer2.setDisable(true);
                ProfessorAffectationContainer2.setVisible(false);
                NextButton.setDisable(false);
                NextButton.setVisible(true);
                BackButton.setDisable(false);
                BackButton.setVisible(true);
                AddButton.setDisable(true);
                AddButton.setVisible(false);
            }
        });

        AddButton.setOnAction(event -> {

            ObservableList<String> selectedStudents = StudentCheckComboBox.getCheckModel().getCheckedItems();
            for (String student : selectedStudents) {
                System.out.println(student);
            }

            ObservableList<Subject> selectedSubjects = FXCollections.observableArrayList();

            Consumer<Node> processVBoxChildren = (node) -> {
                if (node instanceof VBox) {
                    VBox subjectBox = (VBox) node;
                    for (Node child : subjectBox.getChildren()) {
                        if (child instanceof MFXLegacyComboBox) {
                            MFXLegacyComboBox<String> profComboBox = (MFXLegacyComboBox<String>) child;
                            Subject Subject_X = new Subject();
                            Subject_X.setSubjectName(profComboBox.getUserData().toString());
                            System.out.println(profComboBox.getUserData().toString());
                            Professor Professor_X = new Professor(profComboBox.getSelectionModel().getSelectedItem(),"");
                            Subject_X.setAssignedProfessor(Professor_X);
                            selectedSubjects.add(Subject_X);
                        }
                    }
                }
            };

            ProfessorAffectationContainer1.getChildren().forEach(processVBoxChildren);
            ProfessorAffectationContainer2.getChildren().forEach(processVBoxChildren);

            try {
                errorMessageLabel.setText("");
                Admin.CREATE_GROUP(NewGroupField.getText(), Path_Name[0], selectedStudents, selectedSubjects);
                initialize(Admin, Theme);

                for (TreeItem<Group> item0 : Table.getRoot().getChildren()) {

                    if (item0.getValue().getPathname().contains(Path_Name[0])) {
                        item0.setExpanded(true);

                    }
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate")) { // SQLState code for integrity constraint violation
                    errorMessageLabel.setText("The Group Name already exists.");
                }
                else {
                    errorMessageLabel.setText("An error occurred: " + e.getMessage());
                }            }
        });
        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);
    }

    public <T extends Group> void Specific(TreeTableRow<T> row) {
        Group selectedProfessor = row.getTreeItem().getValue();
        String name = row.getItem().getGroupeName();
        String path;
        Integer groupStudentsCount = -1;
        if (row.getItem().getGroupStudentsCount()!=null&&row.getItem().getGroupStudentsCount()!="") {
            groupStudentsCount= (Integer) row.getItem().getGroupStudentsCount();
        }

        if (name!=null && name.contains(":")) {
            if (row.getTreeItem().getParent().getParent().getParent().getValue().getPathname()!=null){
                path =row.getTreeItem().getParent().getParent().getParent().getValue().getPathname();
            } else {
                path = "";
            }
            String[] parts = name.split(":");

            ReplacingProfessorComboBox.getItems().clear();

            String SubjectName = parts[0];
            String Professorname = parts[1];

            ObservableList<Professor> profList = Admin.SELECT_PROFESSORS_BY_SUBJECT(SubjectName);

            for (Professor professor : profList) {
                if (!professor.getUsername().contains(Professorname)) {
                    ReplacingProfessorComboBox.getItems().add(professor.getUsername()); // Add professors to ComboBox
                }
            }



            StudentCheckComboBox.setDisable(true);
            StudentCheckComboBox.setVisible(false);
            PathComboBox.setDisable(true);
            PathComboBox.setVisible(false);
            NewGroupField.setDisable(true);
            NewGroupField.setVisible(false);
            NextButton.setVisible(false);
            TextfieldDesign.setDisable(true);
            TextfieldDesign.setVisible(false);
            ReplacingProfessorComboBox.setVisible(true);
            ReplacingProfessorComboBox.setDisable(false);
            DeleteButton.setDisable(true);
            DeleteButton.setVisible(false);

            UpdateButton.setOnAction(event -> {
                String GroupName = row.getTreeItem().getParent().getParent().getValue().getGroupeName();

                Admin.UPDATE_PROF_AFFECTATION(ReplacingProfessorComboBox.getSelectionModel().getSelectedItem().toString(),Professorname, SubjectName, GroupName);
                initialize(Admin, Theme);
                System.out.println(path);
                for (TreeItem<Group> item0 : Table.getRoot().getChildren()) {

                    if (item0.getValue().getPathname().contains(path)) {
                        item0.setExpanded(true);

                        for (TreeItem<Group> item1 : item0.getChildren()) {
                            if (item1.getValue().getGroupeName().contains(GroupName)){
                                item1.setExpanded(true);
                            }
                            for (TreeItem<Group> item2 : item1.getChildren()) {
                                if (item2.getValue().getGroupeName().contains("Professors")){
                                    item2.setExpanded(true);
                                }
                            }
                        }
                    }

                }
            });

        } else if (groupStudentsCount >= 0) {
            String PathName = row.getTreeItem().getParent().getValue().getPathname();
            String GroupName = row.getTreeItem().getValue().getGroupeName();

            ReplacingProfessorComboBox.setVisible(false);
            NewGroupField.setDisable(false);
            NewGroupField.setVisible(true);
            NewGroupField.setText(selectedProfessor.getGroupeName());

            String NewGroupName = NewGroupField.getText();
            DeleteButton.setDisable(false);
            DeleteButton.setVisible(true);
            UpdateButton.setDisable(false);
            UpdateButton.setVisible(true);
            PathComboBox.setVisible(false);
            PathComboBox.setDisable(true);
            StudentCheckComboBox.setDisable(true);
            StudentCheckComboBox.setVisible(false);
            TextfieldDesign.setDisable(true);
            TextfieldDesign.setVisible(false);
            NextButton.setDisable(true);
            NextButton.setVisible(false);
            AddButton.setDisable(true);
            AddButton.setVisible(false);

            UpdateButton.setOnAction(event -> {
                try {
                    Admin.UPDATE_GROUP(NewGroupField.getText(),NewGroupName,  PathName);
                } catch (SQLException e) {
                    if (e.getMessage().contains("Duplicate")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The Group Name already exists.");
                    }
                    else {
                        errorMessageLabel.setText("An error occurred: " + e.getMessage());
                    }
                }
                initialize(Admin, Theme);

                for (TreeItem<Group> item0 : Table.getRoot().getChildren()) {

                    if (item0.getValue().getPathname().contains(PathName)) {
                        item0.setExpanded(true);

                    }
                }
                ProfessorAffectationContainer1.getChildren().clear();
                ProfessorAffectationContainer2.getChildren().clear();
            });

            DeleteButton.setOnAction(event -> {
                try {
                    Admin.DELETE_GROUP(GroupName, PathName);
                } catch (SQLException e) {
                    errorMessageLabel.setText(e.getMessage());
                }
                initialize(Admin, Theme);
                for (TreeItem<Group> item0 : Table.getRoot().getChildren()) {

                    if (item0.getValue().getPathname().contains(PathName)) {
                        item0.setExpanded(true);

                    }
                }
                ProfessorAffectationContainer1.getChildren().clear();
                ProfessorAffectationContainer2.getChildren().clear();

            });

        } else {
            row.updateSelected(false);
            Extra_Option();
        }
    }

    public void Extra_Option() {
        ProfessorAffectationContainer1.getChildren().clear();
        ProfessorAffectationContainer2.getChildren().clear();
        NewGroupField.setDisable(false);
        NewGroupField.setVisible(true);
        DeleteButton.setDisable(true);
        DeleteButton.setVisible(false);
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
        NextButton.setLayoutX(613);
        ReplacingProfessorComboBox.setVisible(false);
        ReplacingProfessorComboBox.setDisable(true);
        TextfieldDesign.setDisable(false);
        TextfieldDesign.setVisible(true);
        NextButton.setDisable(true);
        NextButton.setVisible(true);
        AddButton.setDisable(true);
        AddButton.setVisible(false);
        StudentCheckComboBox.setDisable(false);
        StudentCheckComboBox.setVisible(true);
        StudentCheckComboBox.getCheckModel().clearChecks();
        StudentCheckComboBox.getItems().clear();
        PathComboBox.setDisable(false);
        PathComboBox.setVisible(true);
        PathComboBox.getItems().clear();
        PathComboBox.getSelectionModel().clearSelection();

        ObservableList<Path> PathList = Admin.SELECT_ALL_PATHS("path");

        for (Path path_X : PathList)
        {
            PathComboBox.getItems().add(path_X.getPathname());
        }

        ProfessorAffectationContainer1.setDisable(true);
        ProfessorAffectationContainer1.setVisible(false);
        ProfessorAffectationContainer2.setDisable(true);
        ProfessorAffectationContainer2.setVisible(false);
        NewGroupField.setText("");
        BackButton.setDisable(true);
        BackButton.setVisible(false);

    }
}
