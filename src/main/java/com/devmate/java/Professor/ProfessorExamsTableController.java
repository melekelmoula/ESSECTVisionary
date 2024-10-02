package com.devmate.java.Professor;
import com.devmate.java.Admin.AdminPathTableController;
import com.devmate.java.Exam;
import com.devmate.java.Exception.ExamsNumberExceededException;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Subject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.utils.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class ProfessorExamsTableController {
    @FXML
    public Pane Panel;
    @FXML
    public Pane EditingPanel;
    @FXML
    private TreeTableView<Exam> Table;
    @FXML
    private TreeTableColumn<Exam, String> GroupColumn;
    @FXML
    private TreeTableColumn<Exam, String> PathColumn;
    @FXML
    private TreeTableColumn<Exam, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Exam, String> IDColumn;
    @FXML
    private TreeTableColumn<Exam, String> MainIDColumn;
    @FXML
    private TreeTableColumn<Exam, String> YearColumn;
    @FXML
    private TreeTableColumn<Exam, Button> ExamFileColumn;
    @FXML
    private TreeTableColumn<Exam, Button> ExamCorrectionFileColumn;
    @FXML
    private MFXLegacyComboBox<String> PathComboBox;
    @FXML
    private MFXLegacyComboBox<String> GroupComboBox;
    @FXML
    private MFXLegacyComboBox<String> SubjectComboBox;
    @FXML
    private MFXButton AddButton;
    @FXML
    private MFXButton DeleteButton;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Label ExamLabel;
    @FXML
    private Label CorrectionLabel;
    @FXML
    private Button uploadCorrectionButton;
    @FXML
    private Button uploadExamButton;

    private boolean Theme;
    private Professor Professor_X;
    private File examFile;
    private File examCorrectionFile;
    Stage ExamViewerStage = new Stage() ;
    Stage CorrectionViewerStage = new Stage() ;
    ObservableList<String> PathList = FXCollections.observableArrayList();

    public void initialize(Professor Professor_X, Boolean Theme) {

        this.Professor_X = Professor_X;
        this.Theme=Theme;
        PathList.clear();

        Extra_Option();

        ExamFileColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getViewExambutton() != null) {
                return new SimpleObjectProperty<>(question.getViewExambutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        ExamCorrectionFileColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getViewCorrectbutton() != null) {
                return new SimpleObjectProperty<>(question.getViewCorrectbutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        /* Alternative
        DeleteColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            if (question != null && question.getViewCorrectbutton() != null) {
                return new SimpleObjectProperty<>(question.getDeletebutton());
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

         */

        YearColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            // Check if question or exam year is null, and handle accordingly
            if (question != null && question.getExamYear() != 0) {
                return new SimpleStringProperty(String.valueOf(question.getExamYear()));
            }
            return new SimpleStringProperty(""); // Return an empty string if no year is available
        });

        IDColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            // Check if question or exam year is null, and handle accordingly
            if (question != null && question.getExamId() != 0) {
                return new SimpleStringProperty(String.valueOf(question.getExamId()));
            }
            return new SimpleStringProperty(""); // Return an empty string if no year is available
        });

        MainIDColumn.setCellValueFactory(param -> {
            Exam question = param.getValue().getValue();
            // Check if question or exam year is null, and handle accordingly
            if (question != null && question.getExamMainId() != 0) {
                return new SimpleStringProperty(String.valueOf(question.getExamMainId()));
            }
            return new SimpleStringProperty(""); // Return an empty string if no year is available
        });

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


        ObservableList<Exam> ExamsList = Professor_X.SELECT_EXAMS(this.Professor_X.getUsername());

        TreeItem<Exam> root = new TreeItem<>();

        for (Group Groups_paths : this.Professor_X.getAffectation_List()) {

            if (!PathList.contains(Groups_paths.getPathname())) {

                TreeItem<Exam> childItem2 = new TreeItem<>(new Exam() {
                    @Override
                    public String getPathname() {
                        return Groups_paths.getPathname();
                    }
                });
                root.getChildren().add(childItem2);
                PathList.add(Groups_paths.getPathname());

                for (Group Groups : this.Professor_X.getAffectation_List()) {
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

                                for (Exam Quiz_X : ExamsList) {
                                    if (Quiz_X.getPathname().contains(Groups_paths.getPathname())&&Quiz_X.getGroupname().contains(Groups.getGroupeName())&&Quiz_X.getSubjectname().contains(Groups_Subject.getSubjectName())) {

                                        TreeItem<Exam> childItem6 = new TreeItem<>(new Exam() {

                                            @Override
                                            public int getExamMainId() {
                                                return Quiz_X.getExamMainId();
                                            }

                                            @Override
                                            public int getExamId() {
                                                return Quiz_X.getExamId();
                                            }

                                            @Override
                                            public int getExamYear() {
                                                return Quiz_X.getExamYear();
                                            }

                                            @Override
                                            public Button getViewExambutton() {
                                                Button ViewExamButton = new Button("View Exam");

                                                String buttonStyle = "-fx-background-color: #F23838; -fx-text-fill: #F8F8F8;";
                                                ViewExamButton.setStyle(buttonStyle);

                                                final byte[][] pdfBytes = {null};

                                                ViewExamButton.setOnAction(event -> {
                                                        if (ExamViewerStage !=null)
                                                        {
                                                            ExamViewerStage.close();
                                                        }

                                                        try {
                                                            if (pdfBytes[0] == null)
                                                            {
                                                            InputStream examInputStream = Quiz_X.getExamPDF();
                                                            pdfBytes[0] = examInputStream.readAllBytes();
                                                            }

                                                            ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes[0]);
                                                            PDDocument doc = PDDocument.load(bais);

                                                            File tempFile = File.createTempFile("exam", ".pdf");
                                                            FileOutputStream fos = new FileOutputStream(tempFile);
                                                            doc.save(fos);

                                                            ImageView imageView = new ImageView();
                                                            PDFRenderer pdfRenderer = new PDFRenderer(doc);
                                                            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300); // Render first page
                                                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                                                            imageView.setImage(image);
                                                            imageView.setPreserveRatio(true);
                                                            imageView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2 - 150);

                                                            ScrollPane scrollPane = new ScrollPane(imageView);
                                                            scrollPane.setFitToHeight(true);

                                                            ExamViewerStage.setTitle("PDF Viewer");

                                                            VBox root = new VBox(scrollPane);
                                                            Scene pdfViewerScene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 2 - 140, Screen.getPrimary().getVisualBounds().getHeight());
                                                            ExamViewerStage.setScene(pdfViewerScene);

                                                            ExamViewerStage.setX(0);
                                                            ExamViewerStage.setY(0);
                                                            ExamViewerStage.show();
                                                            doc.close();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                });

                                                return ViewExamButton;
                                            }

                                            @Override
                                            public Button getViewCorrectbutton() {
                                                Button ViewCorrectionButton = new Button("View Correction");

                                                String buttonStyle = "-fx-background-color: #F23838; -fx-text-fill: #F8F8F8;";
                                                ViewCorrectionButton.setStyle(buttonStyle);

                                                final byte[][] pdfBytes = {null};

                                                ViewCorrectionButton.setOnAction(event -> {
                                                    if (CorrectionViewerStage !=null)
                                                    {
                                                        CorrectionViewerStage.close();
                                                    }

                                                    try {
                                                        if (pdfBytes[0] == null)
                                                        {
                                                            InputStream examInputStream = Quiz_X.getExamCorrectionPDF();
                                                            pdfBytes[0] = examInputStream.readAllBytes();
                                                        }

                                                        ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes[0]);
                                                        PDDocument doc = PDDocument.load(bais);

                                                        File tempFile = File.createTempFile("exam", ".pdf");

                                                        FileOutputStream fos = new FileOutputStream(tempFile);
                                                        doc.save(fos);

                                                        ImageView imageView = new ImageView();
                                                        PDFRenderer pdfRenderer = new PDFRenderer(doc);
                                                        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300); // Render first page
                                                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                                                        imageView.setImage(image);
                                                        imageView.setPreserveRatio(true);
                                                        imageView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2 - 150);

                                                        ScrollPane scrollPane = new ScrollPane(imageView);
                                                        scrollPane.setFitToHeight(true);

                                                        CorrectionViewerStage.setTitle("PDF Viewer");

                                                        VBox root = new VBox(scrollPane);
                                                        Scene pdfViewerScene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 2 - 140, Screen.getPrimary().getVisualBounds().getHeight());
                                                        CorrectionViewerStage.setScene(pdfViewerScene);

                                                        CorrectionViewerStage.setX(Screen.getPrimary().getVisualBounds().getWidth() - imageView.getFitWidth());
                                                        CorrectionViewerStage.setY(0);
                                                        CorrectionViewerStage.show();
                                                        doc.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace(); // Handle exception gracefully
                                                    }
                                                });
                                                return ViewCorrectionButton;
                                            }

                                            /* Alternative
                                            @Override
                                            public Button getDeletebutton() {
                                                Button DeleteButton = new Button("X");  // Set the button text to "X"
                                                DeleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 22px; -fx-font-family: Arial;");

                                                DeleteButton.setOnAction(event -> {
                                                    try {
                                                        Professor_X.DELETE_EXAM(Quiz_X);
                                                        initialize(Professor_X,Theme);
                                                        for (TreeItem<Exam> item0 : Table.getRoot().getChildren()) {

                                                            if (item0.getValue().getPathname().contains(Quiz_X.getPathname())) {
                                                                item0.setExpanded(true);

                                                                for (TreeItem<Exam> item1 : item0.getChildren()) {
                                                                    if (item1.getValue().getGroupname().contains(Quiz_X.getGroupname())){
                                                                        item1.setExpanded(true);
                                                                    }
                                                                    for (TreeItem<Exam> item2 : item1.getChildren()) {
                                                                        if (item2.getValue().getSubjectname().contains(Quiz_X.getSubjectname())){
                                                                            item2.setExpanded(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } catch (SQLException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                });

                                                return DeleteButton;
                                            }

                                             */
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

        Table.setRoot(root);
        Table.setShowRoot(false); // Optionally, if you don't want to show the root item
        Table.getColumns().setAll(PathColumn,GroupColumn, SubjectColumn,MainIDColumn,IDColumn,YearColumn,ExamFileColumn,ExamCorrectionFileColumn);
        Table.getSortOrder().add(YearColumn);  // Add the YearColumn to the sort order
        YearColumn.setSortType(TreeTableColumn.SortType.ASCENDING); // Set the sort type to ascending
        Table.sort();

        List<String> sortedItems = new ArrayList<>(PathComboBox.getItems());
        Collections.sort(sortedItems);
        PathComboBox.getItems().setAll(sortedItems);

        final String[] PathSelection = new String[1];
        final String[] GroupSelection = new String[1];

        PathComboBox.setOnAction(event -> {
            GroupComboBox.getItems().clear();
            if (PathComboBox.getSelectionModel().getSelectedItem() != null) {
                PathSelection[0] = PathComboBox.getSelectionModel().getSelectedItem();

                for (Group Groups_paths : this.Professor_X.getAffectation_List()) {
                    if (Groups_paths.getPathname().contains(PathSelection[0])) {
                        if (!GroupComboBox.getItems().contains(Groups_paths.getGroupeName())) {
                            GroupComboBox.getItems().add(Groups_paths.getGroupeName());
                        }
                    }
                }

                List<String> sortedItems2 = new ArrayList<>(GroupComboBox.getItems());
                Collections.sort(sortedItems2);
                GroupComboBox.getItems().setAll(sortedItems2);
            }
        });

        GroupComboBox.setOnAction(event -> {
            SubjectComboBox.getItems().clear();

            if (GroupComboBox.getSelectionModel().getSelectedItem() != null) {
                GroupSelection[0] = GroupComboBox.getSelectionModel().getSelectedItem();

                List<String> itemsToAdd = new ArrayList<>();

                for (Group Groups_paths : this.Professor_X.getAffectation_List()) {
                    if (Groups_paths.getPathname().contains(PathSelection[0])&&Groups_paths.getGroupeName().contains(GroupSelection[0])) {
                        for (Subject Group_subject : Groups_paths.getPathSubjectList()) {
                            if (!itemsToAdd.contains(Group_subject.getSubjectName())) {
                                itemsToAdd.add(Group_subject.getSubjectName());
                            }
                        }
                    }
                }
                Collections.sort(itemsToAdd);
                SubjectComboBox.getItems().addAll(itemsToAdd);
            }
        });

        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);
    }

    public <T extends Exam> void Specific(TreeTableRow<T> row) {

        if (row.getItem().getExamYear() != 0)  // if Double is null it returns 0 by default
        {
            PathComboBox.setDisable(true);
            PathComboBox.setVisible(false);

            GroupComboBox.setDisable(true);
            GroupComboBox.setVisible(false);

            SubjectComboBox.setDisable(true);
            SubjectComboBox.setVisible(false);

            AddButton.setDisable(true);
            AddButton.setVisible(false);

            ExamLabel.setDisable(true);
            ExamLabel.setVisible(false);

            CorrectionLabel.setDisable(true);
            CorrectionLabel.setVisible(false);

            uploadCorrectionButton.setDisable(true);
            uploadCorrectionButton.setVisible(false);

            uploadExamButton.setDisable(true);
            uploadExamButton.setVisible(false);

            DeleteButton.setVisible(true);
            DeleteButton.setDisable(false);

            DeleteButton.setOnAction(event -> {
                String Subject = row.getTreeItem().getParent().getValue().getSubjectname();
                String Group = row.getTreeItem().getParent().getParent().getValue().getGroupname();
                String Path = row.getTreeItem().getParent().getParent().getParent().getValue().getPathname();

                int ExamMainId = row.getItem().getExamMainId();

                try {
                    Professor_X.DELETE_EXAM(ExamMainId,Professor_X.getUsername());
                } catch (SQLException e) {
                    if (e.getMessage().contains("delete or update a parent row")) {
                        errorMessageLabel.setText("Current year exams cannot be deleted once they have been passed.");
                    }
                }

                initialize(Professor_X, Theme);

                for (TreeItem<Exam> item0 : Table.getRoot().getChildren()) {

                    if (item0.getValue().getPathname().contains(Path)) {
                        item0.setExpanded(true);

                        for (TreeItem<Exam> item1 : item0.getChildren()) {
                            if (item1.getValue().getGroupname().contains(Group)) {
                                item1.setExpanded(true);
                            }
                            for (TreeItem<Exam> item2 : item1.getChildren()) {
                                if (item2.getValue().getSubjectname().contains(Subject)) {
                                    item2.setExpanded(true);
                                }
                            }
                        }
                    }
                }
            });
            }
            else {
            row.updateSelected(false);
            Extra_Option();
            }
    }

    public void Extra_Option() {

        Platform.runLater(() -> {
            PathComboBox.getItems().clear();
            for (String Pathname : PathList) {
                if (!PathComboBox.getItems().contains(Pathname)) {
                    PathComboBox.getItems().add(Pathname);
                }
            }
        });

        PathComboBox.getSelectionModel().clearSelection();
        PathComboBox.setDisable(false);
        PathComboBox.setVisible(true);

        GroupComboBox.setDisable(false);
        GroupComboBox.setVisible(true);

        SubjectComboBox.setDisable(false);
        SubjectComboBox.setVisible(true);

        AddButton.setDisable(false);
        AddButton.setVisible(true);

        ExamLabel.setDisable(false);
        ExamLabel.setVisible(true);

        CorrectionLabel.setDisable(false);
        CorrectionLabel.setVisible(true);

        uploadCorrectionButton.setDisable(false);
        uploadCorrectionButton.setVisible(true);

        uploadExamButton.setDisable(false);
        uploadExamButton.setVisible(true);

        DeleteButton.setVisible(false);
        DeleteButton.setDisable(true);

        examFile = null;
        examCorrectionFile=null;

        ExamLabel.setText("Exam File");
        CorrectionLabel.setText("Correction File");

    }

    @FXML
    private void handleUploadExam() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        examFile = fileChooser.showOpenDialog(new Stage());
        if (examFile != null) {
            String fileName = examFile.getName();
            ExamLabel.setText(fileName);
        }
    }

    @FXML
    private void handleUploadCorrection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        examCorrectionFile = fileChooser.showOpenDialog(new Stage());
        if (examCorrectionFile != null) {
            String fileName = examCorrectionFile.getName();
            CorrectionLabel.setText(fileName);
        }
    }

    @FXML
    private void handleSubmit() throws FileNotFoundException {
        if (examFile != null && examCorrectionFile != null) {
            Exam Exam_X = new Exam();
            Exam_X.setPathname(PathComboBox.getSelectionModel().getSelectedItem());
            Exam_X.setGroupname(GroupComboBox.getSelectionModel().getSelectedItem()) ;
            Exam_X.setSubjectname(SubjectComboBox.getSelectionModel().getSelectedItem());

            FileInputStream ExamStudentVersion = new FileInputStream(examFile);
            Exam_X.setExamPDF(ExamStudentVersion);
            FileInputStream ExamCorrectionVersion = new FileInputStream(examCorrectionFile);
            Exam_X.setExamCorrectionPDF(ExamCorrectionVersion);
            Exam_X.setProfessorName(Professor_X.getUsername());

            // Call the method to save the files to the database
            try {
                Professor_X.INSERT_EXAM(Exam_X);
                errorMessageLabel.setText("");
            } catch (ExamsNumberExceededException e) {
                errorMessageLabel.setText(e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initialize(Professor_X, Theme);

            for (TreeItem<Exam> item0 : Table.getRoot().getChildren()) {

                if (item0.getValue().getPathname().contains(Exam_X.getPathname())) {
                    item0.setExpanded(true);

                    for (TreeItem<Exam> item1 : item0.getChildren()) {
                        if (item1.getValue().getGroupname().contains(Exam_X.getGroupname())){
                            item1.setExpanded(true);
                        }
                        for (TreeItem<Exam> item2 : item1.getChildren()) {
                            if (item2.getValue().getSubjectname().contains(Exam_X.getSubjectname())){
                                item2.setExpanded(true);
                            }
                        }
                    }
                }
            }
        } else {
            errorMessageLabel.setText("Please upload both files before submitting.");
        }
    }

}