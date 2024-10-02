package com.devmate.java.Student;
import com.devmate.java.Admin.AdminPathTableController;
import com.devmate.java.EmailUtil;
import com.devmate.java.Exam;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Subject;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StudentResultTableController {

    @FXML
    private AnchorPane Panel;
    @FXML
    private TreeTableView<Exam> Table;
    @FXML
    private TreeTableColumn<Exam, String> ScoreColumn;
    @FXML
    private TreeTableColumn<Exam, String> QuiznoColumn;
    @FXML
    private TreeTableColumn<Exam, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Exam, String> Coef;
    @FXML
    private Label GradeLabel;
    @FXML
    private MFXButton downloadPDFButton;
    private Student Student_X;

    public void initialize(Student Student_X, Boolean Theme) {

        this.Student_X = Student_X;

        SubjectColumn.setCellValueFactory(param -> {
            Exam DetailsGrade = param.getValue().getValue();

            if (DetailsGrade != null) {
                return new SimpleStringProperty(DetailsGrade.getSubjectname());
            } else {
                return new SimpleStringProperty("");
            }

        });

        Coef.setCellValueFactory(param -> {
            Exam DetailsGrade = param.getValue().getValue();
            if (DetailsGrade != null && DetailsGrade.getExamMainId() != 0) {
                return new SimpleStringProperty(String.valueOf(DetailsGrade.getExamMainId()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        QuiznoColumn.setCellValueFactory(param -> {
            Exam DetailsGrade = param.getValue().getValue();
            if (DetailsGrade != null && DetailsGrade.getExamId() != 0) {
                return new SimpleStringProperty(String.valueOf(DetailsGrade.getExamId()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        ScoreColumn.setCellValueFactory(param -> {
            Exam DetailsGrade = param.getValue().getValue();

            if (DetailsGrade != null && DetailsGrade.getSubjectname() == null) {
                return new SimpleStringProperty(String.valueOf(DetailsGrade.getScore()));
            } else {
                return new SimpleStringProperty("");
            }
        });


        Student_X = Student_X.SELECT_STUDENT_RESULT_FROM_TABLE(Student_X);

        TreeItem<Exam> root = new TreeItem<>();

        if (Student_X.getGroup() != null) {
            for (Subject Subject_x : this.Student_X.getGroup().getPathSubjectList()) {
                TreeItem<Exam> childItem = new TreeItem<>(new Exam() {
                    @Override
                    public String getSubjectname() {
                        return Subject_x.getSubjectName();
                    }

                    @Override
                    public int getExamMainId() {
                        return Subject_x.getSubjectCoefficient().intValue();
                    }

                });
                root.getChildren().add(childItem);

                if (Student_X.getExamsList() != null && Student_X.getFinalGrade() != null) {
                    for (Exam Quiz_X : this.Student_X.getExamsList()) {

                        downloadPDFButton.setVisible(true);
                        downloadPDFButton.setDisable(false);

                        if (Quiz_X.getSubjectname().equals(Subject_x.getSubjectName())) {
                            TreeItem<Exam> childItem2 = new TreeItem<>(new Exam() {
                                @Override
                                public Double getScore() {
                                    return Quiz_X.getScore();
                                }

                                @Override
                                public int getExamId() {
                                    return Quiz_X.getExamId();
                                }
                            });
                            childItem.getChildren().add(childItem2);
                            childItem.setExpanded(true);
                        }
                    }

                    double grade = this.Student_X.getFinalGrade();
                    String gradeText = "Grade: " + grade + (grade >= 10 ? " - Passed" : " - Failed");
                    GradeLabel.setText(gradeText);

                }
                Table.setRoot(root);
                Table.setShowRoot(false); // Optionally, if you don't want to show the root item
                Table.getColumns().setAll(SubjectColumn,Coef, QuiznoColumn, ScoreColumn);
            }
        }

        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);
        downloadPDFButton.setOnAction(event -> generatePDF()); // Set the action for the button

    }

    public <T extends Exam> void Specific(TreeTableRow<T> row) {
            row.updateSelected(false);
    }

    private void generatePDF() {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(); // Use A4 size for a standard page
            document.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
                // Load the logo image
                InputStream logoStream = getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Logo.png");
                if (logoStream == null) {
                    throw new IOException("Logo image not found in resources");
                }

                // Save InputStream to a file
                File tempFile = File.createTempFile("logo", ".png");
                ImageIO.write(ImageIO.read(logoStream), "png", tempFile);

                // Create PDImageXObject from file path
                PDImageXObject logo = PDImageXObject.createFromFile(tempFile.getAbsolutePath(), document);

                float logoWidth = 190; // Width of the logo
                float logoHeight = 50; // Height of the logo
                float logoX = (pageWidth - logoWidth) / 2;
                float logoY = pageHeight - logoHeight - 50; // Position from the top

                contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

                // Add text
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();

                String studyYearText = "Study Year: " + java.time.Year.now().getValue(); // Current year
                float studyYearTextWidth = PDType1Font.HELVETICA.getStringWidth(studyYearText) / 1000 * 12; // Calculate width
                float studyYearX = (pageWidth - studyYearTextWidth) / 2; // Center horizontally
                contentStream.newLineAtOffset(studyYearX, logoY - 20); // Position below the logo
                contentStream.showText(studyYearText);
                contentStream.endText();

                // Add student username, group name, and path name
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(10, pageHeight - 190); // Position text from the left and top
                contentStream.showText("Student: " + this.Student_X.getUsername() +
                        "   Group: " + this.Student_X.getGroup().getGroupeName() +
                        "   Path: " + this.Student_X.getGroup().getPathname());
                contentStream.endText();

                // Add results
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(10, pageHeight - 230); // Adjust starting position if needed
                contentStream.showText("Results:");
                contentStream.endText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(10, pageHeight - 260); // Adjust starting position if needed
                for (TreeItem<Exam> rootItem : Table.getRoot().getChildren()) {
                    contentStream.showText("Subject: " + rootItem.getValue().getSubjectname() + "  Coefficient: " + rootItem.getValue().getExamMainId() );
                    contentStream.newLineAtOffset(0, -15); // Move down for spacing
                    for (TreeItem<Exam> quizItem : rootItem.getChildren()) {
                        contentStream.showText("Exam ID: " + quizItem.getValue().getExamId() +
                                "  Score: " + quizItem.getValue().getScore());
                        contentStream.newLineAtOffset(0, -15); // Move down for spacing
                    }
                    contentStream.newLineAtOffset(0, -10); // Extra space between subjects
                }
                contentStream.endText();

                // Add grade text
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.beginText();
                Double finalGrade = this.Student_X.getFinalGrade();
                String resultText = finalGrade >= 10 ? "Passed" : "Failed";
                String gradeText = "Grade: " + finalGrade + " - " + resultText;
                float gradeTextWidth = PDType1Font.HELVETICA.getStringWidth(gradeText) / 1000 * 14; // Calculate width
                float gradeX = (pageWidth - gradeTextWidth) / 2; // Center horizontally
                float gradeY = 50; // Example starting position from top, adjust as needed
                contentStream.newLineAtOffset(gradeX, gradeY); // Set the X position for the centered text
                contentStream.showText(gradeText);
                contentStream.endText();
            }

            Stage tempStage = new Stage(); // Create a temporary stage for FileChooser

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("Essect_Results.pdf"); // Set the default file name
            File file = fileChooser.showSaveDialog(tempStage);

            if (file != null) {
                document.save(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}