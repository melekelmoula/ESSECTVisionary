package com.devmate.java.Admin;

import com.devmate.java.EmailUtil;
import com.devmate.java.Path_Groups_Subjects.Group;
import com.devmate.java.Path_Groups_Subjects.Path;
import com.devmate.java.Path_Groups_Subjects.Subject;
import com.devmate.java.Professor.Professor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class AdminProfessorTableController {

    @FXML
    private Pane Panel;
    @FXML
    private Pane EditingPanel;
    @FXML
    private TreeTableView<Professor> Table;
    @FXML
    private TreeTableColumn<Professor, String> UsernameColumn;
    @FXML
    private TreeTableColumn<Professor, String> PasswordColumn;
    @FXML
    private TreeTableColumn<Professor, String> EmailColumn;
    @FXML
    private TreeTableColumn<Professor, String> SubjectColumn;
    @FXML
    private TreeTableColumn<Professor, String> CountColumn;
    @FXML
    private CheckComboBox<String> ExpertiseAreaComboBox;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ExpertiseAreaTextfield;
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
    Boolean Theme;
    private Admin Admin;
    ObservableList<String> ExpertiseAreaComboBoxCheckList;

    public void initialize(Admin Admin, Boolean Theme) {

        this.Theme =Theme;
        this.Admin =Admin;

        Extra_Option();

        UsernameColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getValue();
            return professor != null ? new SimpleStringProperty(professor.getUsername()) : new SimpleStringProperty("");
        });

        PasswordColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getValue();
            return professor != null ? new SimpleStringProperty(professor.getPassword()) : new SimpleStringProperty("");
        });

        EmailColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getValue();
            return professor != null ? new SimpleStringProperty(professor.getEmail()) : new SimpleStringProperty("");
        });

        SubjectColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getValue();

            return professor != null ? new SimpleStringProperty(professor.getSkills_List()) : new SimpleStringProperty("");
        });

        CountColumn.setCellValueFactory(param -> {
            Professor professor = param.getValue().getValue();
            if (professor != null && professor.getEmail()!=null) {
                return new SimpleStringProperty(String.valueOf(professor.getTotal_Affectectation()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        ObservableList<Professor> professors = this.Admin.SELECT_ALL_PROFESSORS();


        TreeItem<Professor> root = new TreeItem<>();

        for (Professor professor : professors) {
            TreeItem<Professor> professorItem = new TreeItem<>(professor);
            ObservableList<String> pathsList = FXCollections.observableArrayList();

            for (Group Group_X : professor.getAffectation_List()) {
                if (!pathsList.contains(Group_X.getPathname())) {
                    TreeItem<Professor> item = new TreeItem<>(new Professor("", "") {
                        @Override
                        public String getUsername() {
                            return Group_X.getPathname();
                        }
                    });

                professorItem.getChildren().add(item);
                pathsList.add(Group_X.getPathname());

                for (Group Group_Y : professor.getAffectation_List()) {
                    if (Group_Y.getPathname()==Group_X.getPathname()) {

                    TreeItem<Professor> item2 = new TreeItem<>(new Professor("", "") {
                        @Override
                        public String getUsername() {
                            return Group_Y.getGroupeName();
                        }
                    });
                    item.getChildren().add(item2);

                  for (Subject Subject_X : Group_Y.getPathSubjectList()) {

                        TreeItem<Professor> subjectItem = new TreeItem<>(new Professor("", "") {
                        @Override
                        public String getUsername() {
                            return Subject_X.getSubjectName();
                        }
                    });
                      item2.getChildren().add(subjectItem);
                    }
                }
                }
            }
        }
            root.getChildren().add(professorItem);
    }

        Table.setRoot(root);
        Table.setShowRoot(false); // Optionally, if you don't want to show the root item
        Table.getColumns().setAll(UsernameColumn, PasswordColumn, EmailColumn, SubjectColumn, CountColumn);

        EditingPanel.getParent().setOnMouseClicked(event -> {
            EditingPanel.getParent().requestFocus(); // Request focus on the parent node
        });

        ObservableList<String> subjects = FXCollections.observableArrayList(ExpertiseAreaComboBox.getItems());

        ExpertiseAreaComboBoxCheckList = FXCollections.observableArrayList();

        ExpertiseAreaComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (String addedItem : change.getAddedSubList()) {
                        if (!ExpertiseAreaComboBoxCheckList.contains(addedItem)) {
                            ExpertiseAreaComboBoxCheckList.add(addedItem);
                        }
                    }
                }
                if (change.wasRemoved()) {
                        ExpertiseAreaComboBoxCheckList.removeAll(change.getRemoved());
                }
            }
        });

        ExpertiseAreaTextfield.setOnKeyPressed(eventSS -> {

            String text = ExpertiseAreaTextfield.getText();

                ObservableList<String> filteredSubjects = subjects.filtered(subject -> subject.contains(text));
                ExpertiseAreaComboBox.getItems().setAll(filteredSubjects);
                ExpertiseAreaComboBox.isResizable();
                ExpertiseAreaComboBox.getParent().layout();

            Set<String> mainCheckSet = new HashSet<>(ExpertiseAreaComboBoxCheckList);

            for (String addedItem : ExpertiseAreaComboBox.getItems()) {
                if (mainCheckSet.contains(addedItem)) {
                    ExpertiseAreaComboBox.getCheckModel().check(addedItem);
                    ExpertiseAreaComboBox.getCheckModel().check(addedItem);
                }
                else {
                    ExpertiseAreaComboBox.getCheckModel().clearCheck(addedItem);
                    ExpertiseAreaComboBox.getCheckModel().clearCheck(addedItem);
                }
            }
            ExpertiseAreaComboBox.show();

        });

        ExpertiseAreaComboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> ExpertiseAreaComboBox.requestFocus());

        AddButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            ArrayList<String> selectedSubjectsList = new ArrayList<>(ExpertiseAreaComboBox.getCheckModel().getCheckedItems());

            try {
                Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
                boolean containsLetterAndNumbers = username.matches("[A-Za-z0-9]+");

                if (containsLetterAndNumbers && emailPattern.matcher(email).matches() && !password.isEmpty() && !selectedSubjectsList.isEmpty()) {
                    errorMessageLabel.setText("");
                    loadingIndicator.setVisible(true); // Show the loading spinner
                    AddButton.setVisible(false);
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Admin.CREATE_PROFESSOR(username, password, email, selectedSubjectsList);
                            String professorEmail = email;
                            String subject = "Your new account information";
                            String body = "Dear " + username + ",\n\nYour account has been created successfully.\n\nUsername: " + username + "\nPassword: " + password;
                            EmailUtil.sendEmail(professorEmail, subject, body);
                            return null;
                        }
                    };

                    task.setOnSucceeded(e -> {
                        loadingIndicator.setVisible(false); // Hide the loading spinner

                        // Clear and reset fields after successful operation
                        ExpertiseAreaTextfield.setText("");
                        ExpertiseAreaComboBox.getCheckModel().clearChecks();
                        ExpertiseAreaComboBox.getItems().setAll(subjects);
                        initialize(Admin, Theme);
                    });

                    task.setOnFailed(e -> {
                        loadingIndicator.setVisible(false); // Hide the loading spinner
                        Throwable exception = task.getException();
                        if (exception instanceof SQLException) {
                            if (exception.getMessage().contains("Duplicate") && exception.getMessage().contains("PRIMARY")) {
                                errorMessageLabel.setText("The Professor username already exists.");
                            } else if (exception.getMessage().contains("Duplicate") && exception.getMessage().contains("Email")) {
                                errorMessageLabel.setText("The Professor Email already exists.");
                            } else {
                                errorMessageLabel.setText("An error occurred: " + exception.getMessage());
                            }
                        } else {
                            errorMessageLabel.setText("An unexpected error occurred.");
                        }
                    });

                    new Thread(task).start();
                } else {
                    if (username.isEmpty()) {
                        errorMessageLabel.setText("Username cannot be empty ");
                        usernameField.requestFocus();
                    } else if (!containsLetterAndNumbers) {
                        errorMessageLabel.setText("Username must contain characters and numbers only");
                        usernameField.requestFocus();
                    } else if (password.isEmpty()) {
                        errorMessageLabel.setText("Password cannot be empty ");
                        passwordField.requestFocus();
                    } else if (email.isEmpty()) {
                        errorMessageLabel.setText("Email cannot be empty ");
                        emailField.requestFocus();
                    } else if (!emailPattern.matcher(email).matches()) {
                        errorMessageLabel.setText("Email format is incorrect ");
                        emailField.requestFocus();
                    } else if (selectedSubjectsList.isEmpty()) {
                        errorMessageLabel.setText("Areas of expertise cannot be empty ");
                        ExpertiseAreaComboBox.show();
                    }
                }
            } catch (Exception e) {
                loadingIndicator.setVisible(false); // Ensure spinner is hidden in case of unexpected exceptions
                errorMessageLabel.setText("An unexpected error occurred: " + e.getMessage());
                AddButton.setVisible(true);

            }
        });

        AdminPathTableController.Specific_Changes(Table, Theme, Panel,this);
    }

    public <T extends Professor> void Specific(TreeTableRow<T> row) {
        if (row.getTreeItem() != null && row.getTreeItem().getValue().getEmail()!=null) {
            Professor selectedProfessor = row.getTreeItem().getValue();
            usernameField.setText(selectedProfessor.getUsername());
            String oldUsername = usernameField.getText();
            passwordField.setText(selectedProfessor.getPassword());
            emailField.setText(selectedProfessor.getEmail());

            String[] addedItems = selectedProfessor.getSkills_List().split(",");

            int itemCount = addedItems.length;
            ExpertiseAreaComboBox.getCheckModel().clearChecks();

            for (int j = 0; j < itemCount; j++) {
                String subject = addedItems[j];

                    if (ExpertiseAreaComboBox.getItems().contains(subject.trim()))
                    {
                            int index = ExpertiseAreaComboBox.getItems().indexOf(subject);
                            ExpertiseAreaComboBox.getCheckModel().clearCheck(index);
                            ExpertiseAreaComboBox.getCheckModel().check(index);
                            ExpertiseAreaComboBox.getCheckModel().check(index); //BUG-RELATED FIX BY REPETITION
                            ExpertiseAreaComboBoxCheckList.add(subject);
                    }

            }
            AddButton.setDisable(true);
            AddButton.setVisible(false);
            UpdateButton.setDisable(false);
            UpdateButton.setVisible(true);
            DeleteButton.setDisable(false);
            DeleteButton.setVisible(true);

            UpdateButton.setOnAction(event -> {
                ArrayList<String> selectedSubjectsList = new ArrayList<>(ExpertiseAreaComboBoxCheckList);
                try {
                    Admin.UPDATE_PROFESSOR(oldUsername, emailField.getText(), usernameField.getText(), passwordField.getText(), selectedSubjectsList);
                } catch (SQLException e) {
                    if (e.getMessage().contains("Duplicate")&&e.getMessage().contains("PRIMARY")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The Professor username already exists.");
                    }
                    else if (e.getMessage().contains("Duplicate")&&e.getMessage().contains("Email")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The Professor Email already exists.");
                    }
                    else {
                        errorMessageLabel.setText("An error occurred: " + e.getMessage());
                    }              }
                initialize(Admin, Theme);
            });

            DeleteButton.setOnAction(event -> {
                try {
                    Admin.DELETE_PROFESSOR(usernameField.getText());
                } catch (SQLException e) {
                    if (e.getMessage().contains("Cannot delete")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The professor is assigned to a currently active group.");
                    } else {
                        errorMessageLabel.setText("An error occurred: " + e.getMessage());
                    }
                }
                initialize(Admin, Theme);
            });
        }
        else
        {
            row.updateSelected(false);
            AddButton.setDisable(false);
            AddButton.setVisible(true);
            UpdateButton.setDisable(true);
            UpdateButton.setVisible(false);
            DeleteButton.setDisable(true);
            DeleteButton.setVisible(false);
        }

    }

    public void Extra_Option() {

        AddButton.setDisable(false);
        AddButton.setVisible(true);
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
        DeleteButton.setDisable(true);
        DeleteButton.setVisible(false);

        ObservableList<Path> subjectList = Admin.SELECT_ALL_PATHS("Subject");
        ExpertiseAreaComboBox.getCheckModel().clearChecks();
        ExpertiseAreaComboBox.getItems().clear();

        for (Path subjectPath : subjectList) {
            for (Subject a : subjectPath.getSubjectsList()) {
                ExpertiseAreaComboBox.getItems().add(a.getSubjectName());
            }
        }
        passwordField.setText("");
        emailField.setText("");
        usernameField.setText("");
        ExpertiseAreaTextfield.setText("");
    }



}