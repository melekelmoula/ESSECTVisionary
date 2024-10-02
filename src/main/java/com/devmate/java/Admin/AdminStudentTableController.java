package com.devmate.java.Admin;

import com.devmate.java.EmailUtil;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Student.Student;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.legacy.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdminStudentTableController {
    @FXML
    private Pane Panel;
    @FXML
    private Pane EditingPanel;
    @FXML
    private TreeTableView<Student> Table;
    @FXML
    private TreeTableColumn<Student, String> usernameColumn;
    @FXML
    private TreeTableColumn<Student, String> passwordColumn;
    @FXML
    private TreeTableColumn<Student, String> nameColumn;
    @FXML
    private TreeTableColumn<Student, String> pathColumn;
    @FXML
    private TreeTableColumn<Student, String> groupColumn;
    @FXML
    private TreeTableColumn<Student, String> GenderColumn;
    @FXML
    private TreeTableColumn<Student, String> BirthdayColumn;
    @FXML
    private TreeTableColumn<Student, String> ScoreColumn;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newEmailField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private MFXLegacyComboBox<String> GroupComboBox;
    @FXML
    private MFXLegacyComboBox<String> PathComboBox;
    @FXML
    private MFXDatePicker Birthdate;
    @FXML
    private MFXLegacyComboBox GenderCombobox;
    @FXML
    private MFXButton AddButton;
    @FXML
    private MFXButton UpdateButton;
    @FXML
    private MFXButton DeleteButton;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private MFXProgressSpinner loadingIndicator;
    private Admin Admin;
    private Boolean Theme;
    ObservableList<Path> SqlReturn =null;

    public void initialize(com.devmate.java.Admin.Admin Admin, Boolean Theme) {

        this.Admin = Admin;
        this.Theme=Theme;


        Extra_Option();
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getUsername()));
        passwordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getPassword()));

        pathColumn.setCellValueFactory(data -> {
            Student student = data.getValue().getValue();

            if (student.getGroup() != null) {
                return new SimpleStringProperty(String.valueOf(student.getGroup().getPathname()));
            }
            if (student.getGroup() == null&&student.getPath()!=null) {
                return new SimpleStringProperty(String.valueOf(student.getPath().getPathname()));
            }
            else {
            return new SimpleStringProperty("Not Affected");}
        });

        groupColumn.setCellValueFactory(data -> {
            Student student = data.getValue().getValue(); // Assuming data.getValue() returns a StudentModel object
            return new SimpleStringProperty(student != null && student.getGroup() != null
                    ? String.valueOf(student.getGroup().getGroupeName())
                    : "Not Affected");
        });

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getEmail()));

        GenderColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getGender()));

        BirthdayColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getValue().getBirthdate())));

        ScoreColumn.setCellValueFactory(data -> {
            Student student = data.getValue().getValue();

            if (student.getFinalGrade() != null&&student.getFinalGrade() != 0.0) {
                return new SimpleStringProperty(String.valueOf(student.getFinalGrade()));
            }
            else {
                return new SimpleStringProperty("Not Calculated");}
        });

        SqlReturn = Admin.SELECT_ALL_PATHS("Group");

        ObservableList<String> Pathlist = SqlReturn.stream()
                .map(Path::getPathname)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        PathComboBox.setItems(Pathlist);

        AddButton.setOnAction(event -> {
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            String newEmail = newEmailField.getText();
            String sexValue = (String) GenderCombobox.getSelectionModel().getSelectedItem();
            LocalDate birthdateValue = Birthdate.getValue();  // Assuming birthdate is a DatePicker
            String Path = PathComboBox.getSelectionModel().getSelectedItem();

            Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

            try {
                boolean containsLetterAndNumbers = newUsername.matches("[A-Za-z0-9]+");
                if (containsLetterAndNumbers && emailPattern.matcher(newEmail).matches() && !newPassword.isEmpty() && sexValue != null && birthdateValue != null && Path != null) {
                    errorMessageLabel.setText("");
                    loadingIndicator.setVisible(true); // Show the loading spinner
                    AddButton.setVisible(false);
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Admin.CREATE_STUDENT(newUsername, newPassword, newEmail, Path, sexValue, birthdateValue);

                            String studentEmail = newEmail;
                            String subject = "Your new account information";
                            String body = "Dear " + newUsername + ",\n\nYour account has been created successfully.\n\nUsername: " + newUsername + "\nPassword: " + newPassword;
                            EmailUtil.sendEmail(studentEmail, subject, body);
                            return null;
                        }
                    };

                    task.setOnSucceeded(e -> {
                        loadingIndicator.setVisible(false); // Hide the loading spinner
                        initialize(Admin, Theme); // Reinitialize the UI after success
                    });

                    task.setOnFailed(e -> {
                        loadingIndicator.setVisible(false); // Hide the loading spinner
                        Throwable exception = task.getException();
                        if (exception instanceof SQLException) {
                            if (exception.getMessage().contains("Duplicate") && exception.getMessage().contains("PRIMARY")) {
                                errorMessageLabel.setText("The Student username already exists.");
                            } else if (exception.getMessage().contains("Duplicate") && exception.getMessage().contains("Email")) {
                                errorMessageLabel.setText("The Student Email already exists.");
                            } else {
                                errorMessageLabel.setText("An error occurred: " + exception.getMessage());
                            }
                        } else {
                            errorMessageLabel.setText("An unexpected error occurred.");
                        }
                    });

                    new Thread(task).start();
                } else {
                    // Validate and handle errors
                    if (newUsername.isEmpty()) {
                        errorMessageLabel.setText("Username cannot be empty");
                        newUsernameField.requestFocus();
                    } else if (!containsLetterAndNumbers) {
                        errorMessageLabel.setText("Username must contain characters and numbers only");
                        newUsernameField.requestFocus();
                    } else if (newPassword.isEmpty()) {
                        errorMessageLabel.setText("Password cannot be empty");
                        newPasswordField.requestFocus();
                    } else if (newEmail.isEmpty()) {
                        errorMessageLabel.setText("Email cannot be empty");
                        newEmailField.requestFocus();
                    } else if (!emailPattern.matcher(newEmail).matches()) {
                        errorMessageLabel.setText("Email format is incorrect");
                        newEmailField.requestFocus();
                    } else if (birthdateValue == null) {
                        errorMessageLabel.setText("Birthdate cannot be empty.");
                        Birthdate.requestFocus();
                    } else if (Path == null) {
                        errorMessageLabel.setText("Path cannot be empty");
                        PathComboBox.show();
                    } else if (sexValue == null) {
                        errorMessageLabel.setText("Sex cannot be empty");
                        GenderCombobox.show();
                    }
                }
            } catch (Exception e) {
                loadingIndicator.setVisible(false); // Hide spinner in case of an unexpected exception
                errorMessageLabel.setText("An unexpected error occurred: " + e.getMessage());
                AddButton.setVisible(true);

            }
        });

        TreeItem<Student> rootItem = new TreeItem<>(new Student());
        rootItem.setExpanded(true);

        for (Student student : Admin.SELECT_ALL_STUDENTS()) {
            TreeItem<Student> item = new TreeItem<>(student);
            rootItem.getChildren().add(item);
        }

        Table.setRoot(rootItem);
        Table.setShowRoot(false);

        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);

    }

    public <T> void Specific(TreeTableRow<T> row) {

        Student selectedProfessor = (Student) row.getItem();

        Birthdate.setValue(selectedProfessor.getBirthdate());

        newEmailField.setText(selectedProfessor.getEmail());
        newUsernameField.setText(selectedProfessor.getUsername());
        newPasswordField.setText(selectedProfessor.getPassword());
        GenderCombobox.getSelectionModel().select(selectedProfessor.getGender());


        String aux = newUsernameField.getText();

        PathComboBox.getSelectionModel().clearSelection();
        GroupComboBox.getSelectionModel().clearSelection();

        String selectedPathName = (selectedProfessor.getGroup() != null) ? selectedProfessor.getGroup().getPathname() :
                (selectedProfessor.getPath() != null ? selectedProfessor.getPath().getPathname() : "Not Affected");

        if (PathComboBox.getItems().contains(selectedPathName)) {
            PathComboBox.getSelectionModel().select(selectedPathName);
        }

        if (PathComboBox.getSelectionModel().getSelectedItem() != null) {
            String selectedPath = PathComboBox.getSelectionModel().getSelectedItem();

            ObservableList<Path> filteredPathList = SqlReturn.stream()
                    .filter(path -> selectedPath.equals(path.getPathname()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            if (!filteredPathList.isEmpty()) {
                Path selectedPathObject = filteredPathList.get(0); // Get the first (and expected single) Path object

                ObservableList<String> groupNames = FXCollections.observableArrayList();
                if (selectedPathObject.getGroupslist() != null) {
                    groupNames.addAll(selectedPathObject.getGroupslist().stream()
                            .map(Group::getGroupeName) // Assuming Group has a getName() method
                            .collect(Collectors.toList()));
                }
                GroupComboBox.setItems(groupNames);
            } else {
                GroupComboBox.setItems(FXCollections.observableArrayList()); // Empty list
            }
        }

        String selectedGroupName = (selectedProfessor.getGroup() != null)
                ? selectedProfessor.getGroup().getGroupeName()
                : "Not Affected";

        if (GroupComboBox.getItems().contains(selectedGroupName)) {
            GroupComboBox.getSelectionModel().select(selectedGroupName);
        }

        PathComboBox.setOnAction(event -> {
            if (PathComboBox.getSelectionModel().getSelectedItem()!=null){
            String selectedPath = PathComboBox.getSelectionModel().getSelectedItem();

            ObservableList<Path> filteredPathList = SqlReturn.stream()
                    .filter(path -> selectedPath.equals(path.getPathname()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            if (!filteredPathList.isEmpty()) {
                Path selectedPathObject = filteredPathList.get(0); // Get the first (and expected single) Path object

                ObservableList<String> groupNames = FXCollections.observableArrayList();
                if (selectedPathObject.getGroupslist() != null) {
                    groupNames.addAll(selectedPathObject.getGroupslist().stream()
                            .map(Group::getGroupeName) // Assuming Group has a getName() method
                            .collect(Collectors.toList()));
                }
                GroupComboBox.setItems(groupNames);
            } else {
                GroupComboBox.setItems(FXCollections.observableArrayList()); // Empty list
            }
            }
        });

        newEmailField.setPrefWidth(111);
        newPasswordField.setPrefWidth(111);
        newUsernameField.setPrefWidth(111);
        PathComboBox.setPrefWidth(81);
        Birthdate.setPrefWidth(111);
        GenderCombobox.setPrefWidth(81);
        GroupComboBox.setPrefWidth(81);
        newUsernameField.setLayoutX(27);
        newPasswordField.setLayoutX(147);
        newEmailField.setLayoutX(266);
        PathComboBox.setLayoutX(596);
        Birthdate.setLayoutX(383);
        GenderCombobox.setLayoutX(502);
        GroupComboBox.setLayoutX(692);

        GroupComboBox.setDisable(false);
        GroupComboBox.setVisible(true);

        UpdateButton.setOnAction(event -> {
            try {
                Admin.UPDATE_STUDENT(aux,newUsernameField.getText(),newPasswordField.getText(), newEmailField.getText(),PathComboBox.getSelectionModel().getSelectedItem(),GroupComboBox.getSelectionModel().getSelectedItem(), GenderCombobox.getSelectionModel().getSelectedItem().toString(), Birthdate.getValue());
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate")&&e.getMessage().contains("PRIMARY")) { // SQLState code for integrity constraint violation
                    errorMessageLabel.setText("The Student username already exists.");
                }
                else if (e.getMessage().contains("Duplicate")&&e.getMessage().contains("Email")) { // SQLState code for integrity constraint violation
                    errorMessageLabel.setText("The Student Email already exists.");
                }
                else {
                    errorMessageLabel.setText("An error occurred: " + e.getMessage());
                }
            }
            catch (Exception e) {
                errorMessageLabel.setText(e.getMessage());
            }
            initialize(Admin,Theme);
        });
        DeleteButton.setOnAction(event -> {
            try {
                Admin.DELETE_STUDENT(newUsernameField.getText());
            } catch (SQLException e) {
                errorMessageLabel.setText("An error occurred: " + e.getMessage());
            }
            initialize(Admin,Theme);
        });
    }

    public void Extra_Option() {

        for (Node node : EditingPanel.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear();
            } else if (node instanceof CheckComboBox<?>) {
                ((CheckComboBox<?>) node).getCheckModel().clearChecks();
            } else if (node instanceof ComboBox<?>) {
                ((ComboBox<?>) node).getSelectionModel().clearSelection();
            }
        }
        AddButton.setDisable(false);
        AddButton.setVisible(true);
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
        DeleteButton.setDisable(true);
        DeleteButton.setVisible(false);
        GroupComboBox.setDisable(true);
        GroupComboBox.setVisible(false);

        newEmailField.setPrefWidth(152);
        newPasswordField.setPrefWidth(120);
        newUsernameField.setPrefWidth(132);
        PathComboBox.setPrefWidth(81);
        Birthdate.setPrefWidth(170);
        GenderCombobox.setPrefWidth(81);
        GroupComboBox.setPrefWidth(81);

        newUsernameField.setLayoutX(27);
        newPasswordField.setLayoutX(165);
        newEmailField.setLayoutX(290);
        PathComboBox.setLayoutX(620);
        Birthdate.setLayoutX(445);
        GenderCombobox.setLayoutX(715);

        GenderCombobox.getItems().clear();
        GenderCombobox.getItems().addAll("Male", "Female");

        Birthdate.setValue(null);

    }
}