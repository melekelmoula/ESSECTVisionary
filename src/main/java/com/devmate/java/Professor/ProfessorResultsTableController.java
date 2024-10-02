package com.devmate.java.Professor;

import com.devmate.java.Admin.AdminPathTableController;
import com.devmate.java.Exam;
import com.devmate.java.Exception.DeadlineExceededException;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Subject;
import com.devmate.java.Student.Student;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProfessorResultsTableController {
    @FXML
    public Pane Panel;
    @FXML
    public Pane EditingPanel;

    @FXML
    private Spinner newscorespinner;
    @FXML
    private TreeTableView<Exam> Table;
    @FXML
    private TreeTableColumn<Exam, String> GroupColumn;
    @FXML
    private TreeTableColumn<Exam, String> PathColumn;
    @FXML
    private TreeTableColumn<Exam, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Exam, String> StudentColumn;
    @FXML
    private TreeTableColumn<Exam, Integer> ExamIDColumn;
    @FXML
    private TreeTableColumn<Exam, Integer> MainIDColumn;
    @FXML
    private TreeTableColumn<Exam, Double> ScoreColumn;
    @FXML
    private TreeTableColumn<Exam, Button> StudentAnswerColumn;
    @FXML
    private TreeTableColumn<Exam, Button> AiColumn;
    @FXML
    private MFXButton UpdateButton;
    @FXML
    private Label errorMessageLabel;

    private boolean Theme;
    private Professor Professor_x;
    ObservableList<String> PathList = FXCollections.observableArrayList();
    Stage ExamAnswersViewerStage = new Stage() ;
    Stage AiResponseViewerStage = new Stage() ;

    public void initialize(Professor Professor_X, Boolean Theme) {

        this.Professor_x = Professor_X;
        this.Theme =Theme;

        PathList.clear();
        Extra_Option();

        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 20.0, 0.0, 0.25);

        newscorespinner.setValueFactory(valueFactory);


        GroupColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            return new SimpleStringProperty(question != null && question.getGroupname() != null ? question.getGroupname() : "");
        });

        PathColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            return new SimpleStringProperty(question != null && question.getPathname() != null ? question.getPathname() : "");
        });

        SubjectColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            return new SimpleStringProperty(question != null ? question.getSubjectname() : "");
        });

        StudentColumn.setCellValueFactory(param -> {
            Student question = param.getValue().getValue();
            return new SimpleStringProperty(question != null ? question.getUsername() : "");
        });

        ExamIDColumn.setCellValueFactory(param -> {
            Exam student = param.getValue().getValue();

            if (student != null&&student.getExamId()!=0) {
                return new SimpleObjectProperty<>(student.getExamId());

            }
            return new SimpleObjectProperty<>(null);
        });

        MainIDColumn.setCellValueFactory(param -> {
            Exam student = param.getValue().getValue();

            if (student != null&&student.getExamMainId()!=0) {
                return new SimpleObjectProperty<>(student.getExamMainId());

            }
            return new SimpleObjectProperty<>(null);
        });

        ScoreColumn.setCellValueFactory(param -> {
            Exam student = param.getValue().getValue();

            if (student != null) {
                return new SimpleObjectProperty<>(student.getScore());

            }
            return new SimpleObjectProperty<>(null);
        });


        StudentAnswerColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getStudentExamAnswerbutton() != null) {
                return new SimpleObjectProperty<>(question.getStudentExamAnswerbutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        AiColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getAiResponsebutton() != null) {
                return new SimpleObjectProperty<>(question.getAiResponsebutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        ObservableList<Student> ExamsList = Professor_X.SELECT_STUDENTS_SCORES(Professor_x);

        TreeItem<Exam> root = new TreeItem<>();

        for (Group Groups_paths : Professor_x.getAffectation_List()) {

            if (!PathList.contains(Groups_paths.getPathname())) {

                TreeItem<Exam> childItem2 = new TreeItem<>(new Exam() {
                    @Override
                    public String getPathname() {
                        return Groups_paths.getPathname();
                    }
                });
                root.getChildren().add(childItem2);
                PathList.add(Groups_paths.getPathname());

                for (Group Groups : Professor_x.getAffectation_List()) {
                    if (Groups.getPathname().contains(Groups_paths.getPathname())) {
                        TreeItem<Exam> childItem3 = new TreeItem<>(new Exam() {
                            @Override
                            public String getGroupname() {
                                return Groups.getGroupeName();
                            }

                        });
                        childItem2.getChildren().add(childItem3);

                        for (Subject Groups_Subject : Groups.getPathSubjectList()) {
                            if (Groups_Subject.getPathname().contains(Groups_paths.getPathname())) {

                                TreeItem<Exam> childItem26 = new TreeItem<>(new Exam() {
                                    @Override

                                    public String getSubjectname() {
                                        return Groups_Subject.getSubjectName();
                                    }

                                });
                                childItem3.getChildren().add(childItem26);

                                for (Student Quiz_X : ExamsList) {
                                    if (Quiz_X.getGroup().getPathname().contains(Groups_paths.getPathname()) && Quiz_X.getGroup().getGroupeName().contains(Groups.getGroupeName())) {

                                        for (Exam Exam_X : Quiz_X.getExamsList()) {
                                            if (Exam_X.getSubjectname().contains(Groups_Subject.getSubjectName())) {
                                                TreeItem<Exam> childItem6 = new TreeItem<>(new Exam() {
                                                    @Override
                                                    public String getUsername() {
                                                        return Quiz_X.getUsername();
                                                    }

                                                    @Override
                                                    public int getExamMainId() {
                                                        return Exam_X.getExamMainId();
                                                    }

                                                    @Override
                                                    public int getExamId() {
                                                        return Exam_X.getExamId();
                                                    }

                                                    @Override
                                                    public Double getScore() {
                                                        return Exam_X.getScore();
                                                    }

                                                    @Override
                                                    public Button getStudentExamAnswerbutton() {
                                                        Button ViewExamButton = new Button("Student Responses");

                                                        String buttonStyle = "-fx-background-color: #F23838; -fx-text-fill: #F8F8F8;";
                                                        ViewExamButton.setStyle(buttonStyle);

                                                        ViewExamButton.setOnAction(event -> {
                                                            if (ExamAnswersViewerStage != null) {
                                                                ExamAnswersViewerStage.close();
                                                            }

                                                            TextArea textArea = new TextArea();
                                                            textArea.setEditable(false); // Make it read-only if desired

                                                            // Set the long string text
                                                            String longText = Exam_X.getExamAnswer();
                                                            textArea.setText(longText);

                                                            // Optional: Wrap TextArea in a ScrollPane
                                                            ScrollPane scrollPane = new ScrollPane(textArea);
                                                            scrollPane.setFitToWidth(true); // Adjust width to fit the pan

                                                            ExamAnswersViewerStage.setTitle("PDF Viewer");

                                                            VBox root = new VBox(scrollPane);

                                                            Scene pdfViewerScene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 2 - 140, Screen.getPrimary().getVisualBounds().getHeight());
                                                            ExamAnswersViewerStage.setScene(pdfViewerScene);
                                                            textArea.prefHeightProperty().bind(pdfViewerScene.heightProperty());

                                                            // Set the position of the stage on the screen
                                                            ExamAnswersViewerStage.setX(0);
                                                            ExamAnswersViewerStage.setY(0);
                                                            ExamAnswersViewerStage.show();

                                                        });
                                                        return ViewExamButton;
                                                    }

                                                    @Override
                                                    public Button getAiResponsebutton() {
                                                        Button ViewExamButton = new Button("Ai Response");

                                                        String buttonStyle = "-fx-background-color: #F23838; -fx-text-fill: #F8F8F8;";
                                                        ViewExamButton.setStyle(buttonStyle);

                                                        ViewExamButton.setOnAction(event -> {
                                                            if (AiResponseViewerStage != null) {
                                                                AiResponseViewerStage.close();
                                                            }

                                                            TextArea textArea = new TextArea();
                                                            textArea.setEditable(false); // Make it read-only if desired

                                                            // Set the long string text
                                                            String longText = Exam_X.getAiResponse();
                                                            textArea.setText(longText);

                                                            // Optional: Wrap TextArea in a ScrollPane
                                                            ScrollPane scrollPane = new ScrollPane(textArea);
                                                            scrollPane.setFitToWidth(true); // Adjust width to fit the pan

                                                            AiResponseViewerStage.setTitle("PDF Viewer");

                                                            VBox root = new VBox(scrollPane);
                                                            Scene pdfViewerScene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 2 - 140, Screen.getPrimary().getVisualBounds().getHeight());
                                                            AiResponseViewerStage.setScene(pdfViewerScene);
                                                            textArea.prefHeightProperty().bind(pdfViewerScene.heightProperty());

                                                            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                                                            AiResponseViewerStage.setX(screenBounds.getMaxX() - pdfViewerScene.getWidth());
                                                            AiResponseViewerStage.setY(0);
                                                            // Set the position of the stage on the screen
                                                            AiResponseViewerStage.setY(0);
                                                            AiResponseViewerStage.show();
                                                        });
                                                        return ViewExamButton;
                                                    }

                                                });
                                                childItem26.getChildren().add(childItem6);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        Table.setRoot(root);
        Table.setShowRoot(false); // Optionally, if you don't want to show the root item
        Table.getColumns().setAll(PathColumn,GroupColumn, SubjectColumn,StudentColumn,MainIDColumn,ExamIDColumn,ScoreColumn,StudentAnswerColumn,AiColumn);


        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);

    }

    public <T extends Exam> void Specific(TreeTableRow<T> row) {
        if (row.getItem().getAiResponsebutton()==null)
        {
            row.updateSelected(false);
            Extra_Option();
        }
        else {
            newscorespinner.setDisable(false);
            newscorespinner.setVisible(true);
            UpdateButton.setDisable(false);
            UpdateButton.setVisible(true);
            newscorespinner.getValueFactory().setValue(row.getTreeItem().getValue().getScore());


            UpdateButton.setOnAction(event -> {
                String Path =row.getTreeItem().getParent().getParent().getParent().getValue().getPathname();
                String Group =row.getTreeItem().getParent().getParent().getValue().getGroupname();
                String Subject =row.getTreeItem().getParent().getValue().getSubjectname();
                Double NewScore = Double.valueOf(newscorespinner.getValue().toString());
                int ExamMainID = row.getItem().getExamMainId();
                String Student_Username = row.getItem().getUsername();


                try {
                    Professor_x.UPDATE_STUDENT_SCORE(NewScore,ExamMainID,Student_Username);
                    initialize(Professor_x,Theme);
                    for (TreeItem<Exam> item0 : Table.getRoot().getChildren()) {

                        if (item0.getValue().getPathname().contains(Path)) {
                            item0.setExpanded(true);

                            for (TreeItem<Exam> item1 : item0.getChildren()) {
                                if (item1.getValue().getGroupname().contains(Group)){
                                    item1.setExpanded(true);
                                }
                                for (TreeItem<Exam> item2 : item1.getChildren()) {
                                    if (item2.getValue().getSubjectname().contains(Subject)){
                                        item2.setExpanded(true);
                                    }
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (DeadlineExceededException e) {
                    errorMessageLabel.setText(e.getMessage());
                }
            });
        }
    }

    public void Extra_Option() {
        errorMessageLabel.setText("");
        newscorespinner.setDisable(true);
        newscorespinner.setVisible(false);
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
    }

}