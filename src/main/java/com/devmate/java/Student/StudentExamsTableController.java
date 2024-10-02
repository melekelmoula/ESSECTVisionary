package com.devmate.java.Student;
import com.devmate.java.Admin.AdminPathTableController;
import com.devmate.java.Exam;
import com.devmate.java.Path_Groups_Subjects.Subject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class StudentExamsTableController {
    @FXML
    private TextArea ExamAnswer;
    @FXML
    private MFXButton AddButton;
    @FXML
    private Pane Panel;
    @FXML
    private Pane EditingPanel;
    @FXML
    private TreeTableView<Exam> Table;
    @FXML
    private TreeTableColumn<Exam, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Exam, Button> PassColumn;
    @FXML
    private TreeTableColumn<Exam, String> StartDateColumn;
    @FXML
    private MFXProgressSpinner loadingIndicator;
    @FXML
    private TreeTableColumn<Exam, String> DurationColumn;
    @FXML
    private Label CountdownLabel;
    Timeline timeline;
    private Button ExamsButton;
    private Button ResultsButton;
    private Button Exit;
    @FXML
    private Label errorMessageLabel;

    public ImageView Logout;
    public MFXToggleButton ThemeButton;
    private Student Student_X;
    Stage pdfViewerStage = new Stage();
    Stage Main ;
    LocalDateTime quizDateTime;
    public void setExams2(Button ExamsButton, Button ResultsButton, MFXToggleButton ThemeButton, ImageView Logout, Button exitButton) {
        this.ExamsButton = ExamsButton;
        this.ResultsButton = ResultsButton;
        this.ThemeButton = ThemeButton;
        this.Logout = Logout;
        this.Exit = exitButton;

    }

    public void initialize(Student Student_X, Boolean Theme, Stage stage) {

        Table.setDisable(false);
        Table.setVisible(true);
        EditingPanel.setDisable(true);
        EditingPanel.setVisible(false);
        this.ExamsButton.setDisable(false);

        this.ExamsButton.setDisable(false);
        this.ResultsButton.setDisable(false);
        this.ThemeButton.setDisable(false);
        this.Logout.setDisable(false);
        ThemeButton.setVisible(true);
        Logout.setVisible(true);
        Effect effect = Logout.getEffect();
        if (effect instanceof ColorAdjust) {
            ((ColorAdjust) effect).setBrightness(1); // Adjust brightness
        }
        this.Student_X = Student_X;
        this.Main=stage;
        PassColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getPassbutton() != null) {
                return new SimpleObjectProperty<>(question.getPassbutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        SubjectColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getSubjectname() != null) {
                return new SimpleStringProperty(question.getSubjectname());
            } else {
                return new SimpleStringProperty("");
            }
        });

        SubjectColumn.setCellFactory(new Callback<TreeTableColumn<Exam, String>, TreeTableCell<Exam, String>>() {
            @Override
            public TreeTableCell<Exam, String> call(TreeTableColumn<Exam, String> param) {
                return new TreeTableCell<Exam, String>() {
                    private final ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("New.png")));

                    {
                        imageView.setFitWidth(40); // Adjust the width as needed
                        imageView.setFitHeight(20); // Adjust the height as needed
                        imageView.setPreserveRatio(true); // Preserve the aspect ratio of the image
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null || item.isEmpty()) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            TreeTableRow<Exam> row = getTreeTableRow();

                            if (row != null && row.getTreeItem() != null && !row.getTreeItem().getChildren().isEmpty()) {
                                if (row.getTreeItem().getChildren()!=null) {
                                    for (TreeItem<Exam> child : row.getTreeItem().getChildren()) {
                                        // Get today's date and the next two days
                                        LocalDate firstDay = LocalDate.now();
                                        LocalDate secondDay = firstDay.plusDays(1);
                                        LocalDate thirdDay = firstDay.plusDays(2);

                                        LocalDate quizDate = child.getValue().getStartQuiz().toLocalDate();


                                        // Check if the quiz date is one of the specified days
                                        if (quizDate.equals(firstDay) || quizDate.equals(secondDay) || quizDate.equals(thirdDay)) {
                                            setGraphic(imageView);  // Display the image if the date matches
                                        } else {
                                            setGraphic(null); // Clear the graphic if no match
                                        }
                                    }

                                }
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        });

        StartDateColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();

            if (question != null && question.getStartQuiz() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                String formattedDateTime = question.getStartQuiz().format(formatter);
                return new SimpleStringProperty(formattedDateTime);
            } else {
                // Return an empty property
                return new SimpleObjectProperty<>(null);
            }
        });


        DurationColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();

            if (question != null && question.getDurationQuiz() != 0) {
                return new SimpleStringProperty(question.getDurationQuiz() + "min");
            } else {
                return new SimpleStringProperty("");
            }
        });

        ObservableList<Exam> ExamsList = Student_X.SELECT_EXAMFILES(Student_X);

        TreeItem<Exam> root = new TreeItem<>();

        if (Student_X.getGroup() != null) {
            for (Subject Subject_X : Student_X.getGroup().getPathSubjectList()) {

                TreeItem<Exam> childItem2 = new TreeItem<>(new Exam() {
                    @Override
                    public String getSubjectname() {
                        return Subject_X.getSubjectName();
                    }


                });
                root.getChildren().add(childItem2);

                if (!ExamsList.isEmpty()) {
                    for (Exam Exam_X : ExamsList) {

                        if (Exam_X.getSubjectname().equals(Subject_X.getSubjectName()) && Exam_X.getGroupname().equals(Student_X.getGroup().getGroupeName()) && Exam_X.getPathname().equals(Student_X.getGroup().getPathname())) {

                            TreeItem<Exam> childItem4 = new TreeItem<>(new Exam() {

                                @Override
                                public Button getPassbutton() {
                                    Button PassButton = new Button("Pass Exam");
                                    LocalDateTime Now = LocalDateTime.now();
                                    int duration = Exam_X.getDurationQuiz();
                                    LocalDateTime endTime = Exam_X.getStartQuiz().plusMinutes(duration);
                                    final long[] remainingSeconds = {ChronoUnit.SECONDS.between(LocalTime.now(), endTime.toLocalTime())};

                                    if (Now.isBefore(Exam_X.getStartQuiz())) {
                                        PassButton.setDisable(true);
                                    }

                                    if (Theme) {
                                        PassButton.setStyle("-fx-background-color: #F23838;-fx-text-fill: #F8F8F8;");
                                    } else {
                                        PassButton.setStyle("-fx-background-color: #F23838;-fx-text-fill: #F8F8F8;");
                                    }

                                    PassButton.setOnAction(event -> {

                                        if (Exam_X.getExamPDF() != null) {

                                            if (Now.isAfter(Exam_X.getStartQuiz().plusMinutes(15))) {
                                                Exam exam_Y = new Exam();
                                                exam_Y.setExamId(Exam_X.getExamId());
                                                exam_Y.setProfessorName(Exam_X.getProfessorName());
                                                exam_Y.setPathname(Student_X.getGroup().getPathname());
                                                exam_Y.setGroupname(Student_X.getGroup().getGroupeName());
                                                exam_Y.setSubjectname(Exam_X.getSubjectname());
                                                exam_Y.setExamAnswer("Absent");
                                                exam_Y.setCondidate(Student_X);
                                                exam_Y.setExamMainId(Exam_X.getExamMainId());
                                                Student_X.INSERT_STUDENT_EXAM_SCORE(exam_Y,"Absent");
                                                initialize(Student_X, Theme, stage);
                                                errorMessageLabel.setText("Time limit to join the exam has been passed");
                                            }

                                            else {
                                                try {
                                                    ExamsButton.setDisable(true);
                                                    ResultsButton.setDisable(true);
                                                    ThemeButton.setDisable(true);
                                                    Logout.setDisable(true);
                                                    ThemeButton.setVisible(false);
                                                    Logout.setVisible(false);

                                                    PDDocument document = PDDocument.load(Exam_X.getExamPDF());
                                                    File tempFile = File.createTempFile("exam", ".pdf");
                                                    document.save(tempFile);

                                                    System.out.println("Temporary PDF file created: " + tempFile.getAbsolutePath());

                                                    if (tempFile.exists()) {
                                                        // Set up the ImageView for displaying the PDF
                                                        ImageView imageView = new ImageView();
                                                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                                                        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300); // Render first page
                                                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                                                        imageView.setImage(image);
                                                        imageView.setPreserveRatio(true);
                                                        imageView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2 - 150);

                                                        // Create a ScrollPane to allow scrolling
                                                        ScrollPane scrollPane = new ScrollPane(imageView);
                                                        scrollPane.setFitToHeight(true);

                                                        // Create a new Stage for the PDF viewer
                                                        pdfViewerStage.setTitle("PDF Viewer");

                                                        // Create a VBox to hold the ScrollPane
                                                        VBox root = new VBox(scrollPane);

                                                        // Set the scene with specific dimensions
                                                        Scene pdfViewerScene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 2 - 140, Screen.getPrimary().getVisualBounds().getHeight());
                                                        pdfViewerStage.setScene(pdfViewerScene);

                                                        // Position the stage on the left of the screen
                                                        pdfViewerStage.setX(0);
                                                        pdfViewerStage.setY(0);

                                                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                                                        double newX = screenBounds.getMaxX() - stage.getWidth();
                                                        stage.setX(newX);
                                                        pdfViewerStage.show();

                                                        tempFile.deleteOnExit();
                                                        document.close();
                                                        Table.setVisible(false);
                                                        Table.setDisable(true);
                                                        EditingPanel.setVisible(true);
                                                        EditingPanel.setDisable(false);

                                                        // Create a Timeline to update the CountdownLabel every second
                                                        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event1 -> {
                                                            remainingSeconds[0]--;

                                                            long hours = remainingSeconds[0] / 3600;
                                                            long minutes = (remainingSeconds[0] % 3600) / 60;
                                                            long seconds = remainingSeconds[0] % 60;

                                                            // Format the remaining time as "H:mm:ss"
                                                            String formattedRemainingTime = String.format("%d:%02d:%02d", hours, minutes, seconds);

                                                            // Update the CountdownLabel
                                                            CountdownLabel.setText(formattedRemainingTime);

                                                            // Stop the timeline if time is up
                                                            if (remainingSeconds[0] <= 0) {
                                                                timeline.stop();
                                                                CountdownLabel.setText("0:00:00"); // Ensure display shows zero time
                                                                AddButton.fire();
                                                            }
                                                        }));

                                                        // Set the timeline to run indefinitely until manually stopped
                                                        timeline.setCycleCount(Timeline.INDEFINITE);
                                                        timeline.play();

                                                        // Immediately display the initial remaining time
                                                        long hours = remainingSeconds[0] / 3600;
                                                        long minutes = (remainingSeconds[0] % 3600) / 60;
                                                        long seconds = remainingSeconds[0] % 60;
                                                        String formattedRemainingTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
                                                        CountdownLabel.setText(formattedRemainingTime);

                                                        AddButton.setOnMouseClicked(events -> {
                                                            timeline.stop(); // Stop the timeline immediately when AddButton is clicked
                                                            loadingIndicator.setVisible(true); // Show the loading spinner
                                                            AddButton.setVisible(false);
                                                            Task<Void> task = new Task<Void>() {
                                                                @Override
                                                                protected Void call() throws Exception {
                                                                    Exam exam_Y = new Exam();
                                                                    exam_Y.setExamId(Exam_X.getExamId());
                                                                    exam_Y.setExamMainId(Exam_X.getExamMainId());
                                                                    exam_Y.setProfessorName(Exam_X.getProfessorName());
                                                                    exam_Y.setPathname(Student_X.getGroup().getPathname());
                                                                    exam_Y.setGroupname(Student_X.getGroup().getGroupeName());
                                                                    exam_Y.setSubjectname(Exam_X.getSubjectname());
                                                                    exam_Y.setExamAnswer(ExamAnswer.getText());
                                                                    exam_Y.setCondidate(Student_X);
                                                                    Student_X.SELECT_EXAM_CORRECTION(exam_Y);
                                                                    timeline.stop();
                                                                    return null;
                                                                }
                                                            };

                                                            task.setOnSucceeded(e -> {
                                                                loadingIndicator.setVisible(false); // Hide the loading spinner
                                                                pdfViewerStage.close();
                                                                initialize(Student_X, Theme, stage); // Reinitialize the UI after success
                                                            });

                                                            task.setOnFailed(e -> {
                                                                loadingIndicator.setVisible(false); // Hide the loading spinner
                                                                pdfViewerStage.close();
                                                                initialize(Student_X, Theme, stage);
                                                            });

                                                            new Thread(task).start();
                                                        });

                                                    } else {
                                                        System.err.println("The file does not exist: ");
                                                    }

                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        }
                                    });

                                    return PassButton;
                                }

                                    @Override
                                public int getDurationQuiz() {
                                    return Exam_X.getDurationQuiz();
                                }

                                @Override
                                public LocalDateTime getStartQuiz() {
                                    return Exam_X.getStartQuiz();
                                }

                            });

                            childItem2.getChildren().add(childItem4);
                        }
                    }
                }
            }

            Table.setRoot(root);
            Table.setShowRoot(false); // Optionally, if you don't want to show the root item
            Table.getColumns().setAll(SubjectColumn, StartDateColumn, DurationColumn, PassColumn);

            Exit.setOnAction(event -> {
                pdfViewerStage.close();
            });

            AdminPathTableController.Specific_Changes(Table, Theme, Panel, this);
        }
    }


    public <T extends Exam> void Specific(TreeTableRow<T> row) {
        if (row.getItem().getStartQuiz() ==null ) {
            row.updateSelected(false);
        }
    }

}