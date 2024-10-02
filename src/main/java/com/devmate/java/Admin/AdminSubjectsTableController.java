package com.devmate.java.Admin;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Path_Groups_Subjects.Subject;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;

public class AdminSubjectsTableController  {

    @FXML
    private Pane Panel;
    @FXML
    private TreeTableView<Subject> Table;
    @FXML
    private TreeTableColumn<Subject, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Subject, String> CoefficientColumn;
    @FXML
    private TreeTableColumn<Subject, String> PathColumn;
    @FXML
    private TreeTableColumn<Subject, String> TotalExams;
    @FXML
    private MFXButton AddButton;
    @FXML
    private MFXButton UpdateButton;
    @FXML
    private MFXButton DeleteButton;
    @FXML
    private TextField newSubjectField;
    @FXML
    private MFXLegacyComboBox<String> PathCombo;
    @FXML
    private Spinner<Double> CoefficientSpinner;
    @FXML
    private Spinner<String> TotalExamsSpinner;
    @FXML
    private Label errorMessageLabel;

    private Admin Admin;
    private Boolean Theme;
    SpinnerValueFactory<String> value2Factory;
    ObservableList<Path> PathList2;
    ObservableList<Subject> combinedSubjectList;
    public void initialize(Admin Admin, Boolean Theme) {
        this.Admin = Admin;
        this.Theme = Theme;

        Extra_Option();

        PathCombo.getItems().clear();
        newSubjectField.setText("");
        AddButton.setDisable(false);
        AddButton.setVisible(true);
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
        DeleteButton.setDisable(true);
        DeleteButton.setVisible(false);

        SubjectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getSubjectName()));
        CoefficientColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getSubjectCoefficient().toString()));
        PathColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getPathname()));
        TotalExams.setCellValueFactory(data -> new SimpleStringProperty(Integer.toString(data.getValue().getValue().getTotalExams())));

        PathList2 = Admin.SELECT_ALL_PATHS("Subject");
        combinedSubjectList = FXCollections.observableArrayList();

        for (Path path : PathList2) {
           PathCombo.getItems().add(path.getPathname());
           combinedSubjectList.addAll(path.getSubjectsList());
        }

        TreeItem<Subject> rootItem = new TreeItem<>(new Subject());
        rootItem.setExpanded(true);

        for (Subject student : combinedSubjectList) {
            TreeItem<Subject> item = new TreeItem<>(student);
            rootItem.getChildren().add(item);
        }

        Table.setRoot(rootItem);
        Table.setShowRoot(false);


        AdminPathTableController.Specific_Changes(Table, Theme, Panel, this);

        AddButton.setOnAction(event -> {
            try {
                String selectedValue = TotalExamsSpinner.getValue();
                int ExamTotals = Integer.parseInt(selectedValue.replaceAll("\\D+", "").trim());
                if (!newSubjectField.getText().isEmpty()&&!PathCombo.getSelectionModel().getSelectedItem().isEmpty()){
                errorMessageLabel.setText("");
                Admin.CREATE_SUBJECT(newSubjectField.getText(), CoefficientSpinner.getValue(),String.valueOf(PathCombo.getSelectionModel().getSelectedItem()),ExamTotals);
                initialize(Admin, Theme); // Refresh the data
                }
                else if (newSubjectField.getText().isEmpty()){
                    errorMessageLabel.setText("Please enter Subject name");
                    newSubjectField.requestFocus();
                }
                else if (PathCombo.getSelectionModel().getSelectedItem().isEmpty()){
                    errorMessageLabel.setText("Please select the subject path ");
                    PathCombo.show();
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate")) { // SQLState code for integrity constraint violation
                    errorMessageLabel.setText("The subject name already exists.");
                }
            }
        });
    }

    public <T extends Subject> void Specific(TreeTableRow <T> row) {

        newSubjectField.setText(row.getItem().getSubjectName());
        String Subject_Name = newSubjectField.getText();
        CoefficientSpinner.getValueFactory().setValue(row.getItem().getSubjectCoefficient());

        PathCombo.getSelectionModel().clearSelection();

        if (PathCombo.getItems().contains(row.getItem().getPathname())) {
            PathCombo.getSelectionModel().select(row.getItem().getPathname());
        }

        String targetItem = String.valueOf(row.getItem().getTotalExams());

        String aux = targetItem + (Integer.parseInt(targetItem) > 1 ? " Exams" : " Exam");

        SpinnerValueFactory.ListSpinnerValueFactory<String> listValueFactory = (SpinnerValueFactory.ListSpinnerValueFactory<String>) value2Factory;
        ObservableList<String> items = listValueFactory.getItems();

        if (items.contains(aux)) {
            TotalExamsSpinner.getValueFactory().setValue(aux);
        }


        UpdateButton.setOnAction(event -> {
            try {
                String selectedValue = TotalExamsSpinner.getValue();
                int ExamTotals = Integer.parseInt(selectedValue.replaceAll("\\D+", "").trim());
                Admin.UPDATE_SUBJECT(Subject_Name, newSubjectField.getText(), CoefficientSpinner.getValue(), String.valueOf(PathCombo.getSelectionModel().getSelectedItem()),ExamTotals);
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate")) { // SQLState code for integrity constraint violation
                    errorMessageLabel.setText("The subject name already exists.");
                } else {
                    errorMessageLabel.setText("An error occurred: " + e.getMessage());
                }
            }
            initialize(Admin, Theme);
        });

        DeleteButton.setOnAction(event -> {
            try {
            Admin.DELETE_SUBJECT(Subject_Name);
        } catch (SQLException e) {
                if (e.getMessage().contains("Cannot delete")) { // SQLState code for integrity constraint violation
                errorMessageLabel.setText("The subject is associated with an active group");
            } else {
                errorMessageLabel.setText("An error occurred: " + e.getMessage());
            }
        }
            initialize(Admin, Theme);
        });
    }

    public void Extra_Option() {
        value2Factory =new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList("1 Exam", "2 Exams", "3 Exams"));

        TotalExamsSpinner.setValueFactory(value2Factory);

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 5.0, 1.0, 0.5);

        CoefficientSpinner.setValueFactory(valueFactory);
        CoefficientSpinner.getValueFactory().setValue(1.0);


    }

}
