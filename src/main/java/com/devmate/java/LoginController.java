package com.devmate.java;

import com.devmate.java.Admin.Admin;
import com.devmate.java.Exception.EmailNotFoundException;
import com.devmate.java.Exception.IncorrectLoginException;
import com.devmate.java.Professor.Professor;
import com.devmate.java.Student.Student;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class LoginController {
    @FXML
    private Pane MainLogin;
    @FXML
    private Pane LogPane;
    @FXML
    private Pane RecoverPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private MFXButton LoginButton;
    @FXML
    private Button RecoverButton;
    @FXML
    private MFXButton BackButton;
    @FXML
    private Label StudentLabelCount;
    @FXML
    private Label ProfessorLabelCount;
    @FXML
    private Label PartnerLabelCount;
    @FXML
    private Label ResearchLabelCount;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private VBox loadingBox;
    private Stage primaryStage;

    private double x= 0,y=0;
    private int StudentCount = 2500;
    private int ProfessorCount = 30;
    private int PartnerCount = -50;
    private int ResearcCount = -50;
    private Timeline Timeline;

    public void initialize (Stage primaryStage) throws SQLException {
        roleComboBox.getItems().addAll("Admin", "Professor", "Student");
        roleComboBox.setValue("Admin");



        this.primaryStage = primaryStage;

        Timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            if (ResearcCount < 2) {
                ResearcCount++;
                ResearchLabelCount.setText(String.valueOf(ResearcCount));
            }
            if (PartnerCount < 20) {
                PartnerCount++;
                PartnerLabelCount.setText(String.valueOf(PartnerCount));
            }
            if (ProfessorCount < 190) {
                ProfessorCount++;
                ProfessorLabelCount.setText(String.valueOf(ProfessorCount));
            }
            if (StudentCount < 2700) {
                StudentCount++;
                StudentLabelCount.setText(String.valueOf(StudentCount));
            } else {
                Timeline.stop();
            }
        }));

        Timeline.setCycleCount(Timeline.INDEFINITE);
        Timeline.play();

        MainLogin.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();

        });

        MainLogin.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() - x);
            primaryStage.setY(mouseEvent.getScreenY() - y);

        });

        RecoverButton.setOnAction(event -> {
            LogPane.setVisible(false);
            LogPane.setDisable(true);
            RecoverPane.setVisible(true);
            RecoverPane.setDisable(false);
            emailField.setText("");
            errorMessageLabel.setText("");
        });

        BackButton.setOnAction(event -> {
            LogPane.setVisible(true);
            LogPane.setDisable(false);
            RecoverPane.setVisible(false);
            RecoverPane.setDisable(true);
            passwordField.setText("");
            usernameField.setText("");
            errorMessageLabel.setText("");
        });



        LoginButton.setOnAction(event -> {
            errorMessageLabel.setText("");

            PauseTransition delay = new PauseTransition(Duration.seconds(1));

            delay.setOnFinished(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String role = roleComboBox.getValue();

                if (role.equals("Admin")) {
                    Admin Admin_Y;
                    try {
                        Admin_Y = Admin.SELECT_ADMIN_BY_USERNAME_PASSWORD(username, password);
                        if (Admin_Y != null) {
                            openAdminPanel(Admin_Y);
                        }
                    } catch (IncorrectLoginException ex) {
                        errorMessageLabel.setText(ex.getMessage());
                    } catch (SQLException ex) {
                        errorMessageLabel.setText("Database error occurred");
                    }
                } else if (role.equals("Professor")) {
                    try {
                        Professor Professor_X = Admin.SELECT_PROFESSOR_BY_USERNAME_PASSWORD(username, password);
                        if (Professor_X != null) {
                            openTeacherPanel(Professor_X);
                        }
                    } catch (IncorrectLoginException ex) {
                        errorMessageLabel.setText(ex.getMessage());
                    } catch (SQLException ex) {
                        errorMessageLabel.setText("Database error occurred");
                    }
                } else if (role.equals("Student")) {
                    try {
                        Student Student_X = Admin.SELECT_STUDENT_BY_USERNAME_PASSWORD(username, password);
                        if (Student_X != null) {
                            openStudentPanel(Student_X);
                        }
                    } catch (IncorrectLoginException ex) {
                        errorMessageLabel.setText(ex.getMessage());
                    } catch (SQLException ex) {
                        errorMessageLabel.setText("Database error occurred");
                    }
                }

            });
            delay.play();
        });
    }



        public void openAdminPanel(Admin Admin_Y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/Dashboard.fxml"));
            Parent root = loader.load();
            DashBoardController controller = loader.getController();

            Button Professorbutton = (Button) root.lookup("#Professorbutton");
            Professorbutton.setDisable(false);
            Professorbutton.setVisible(true);

            Button Studentbutton = (Button) root.lookup("#Studentbutton");
            Studentbutton.setDisable(false);
            Studentbutton.setVisible(true);

            Button Subjectbutton = (Button) root.lookup("#Subjectbutton");
            Subjectbutton.setDisable(false);
            Subjectbutton.setVisible(true);

            Button Pathbutton = (Button) root.lookup("#Pathbutton");
            Pathbutton.setDisable(false);
            Pathbutton.setVisible(true);

            Button Groupbutton = (Button) root.lookup("#Groupbutton");
            Groupbutton.setDisable(false);
            Groupbutton.setVisible(true);

            Stage adminPanelStage = new Stage();
            adminPanelStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            adminPanelStage.setScene(scene);
            Image transparentImage = new Image(getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Icon.png"));
            adminPanelStage.getIcons().add(transparentImage);
            adminPanelStage.show();

            controller.initialize(Admin_Y,adminPanelStage);

            primaryStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openTeacherPanel(Professor Professor_X) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/Dashboard.fxml"));

            Parent root = loader.load();
            DashBoardController controller = loader.getController();

            Button Exam = (Button) root.lookup("#Exam");
            Exam.setDisable(false);
            Exam.setVisible(true);

            Button Result = (Button) root.lookup("#Result");
            Result.setDisable(false);
            Result.setVisible(true);

            Stage professorPanelStage = new Stage();
            professorPanelStage.initStyle(StageStyle.TRANSPARENT);
            professorPanelStage.setTitle("");
            Image transparentImage = new Image(getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Icon.png"));
            professorPanelStage.getIcons().add(transparentImage);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            professorPanelStage.setScene(scene);
            controller.initialize(Professor_X,professorPanelStage);

            professorPanelStage.show();
            primaryStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openStudentPanel(Student Student_X) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/Dashboard.fxml"));
            Parent root = loader.load();
            DashBoardController controller = loader.getController();

            Button Exams = (Button) root.lookup("#Exams");
            Exams.setDisable(false);
            Exams.setVisible(true);

            Button Results = (Button) root.lookup("#Results");
            Results.setDisable(false);
            Results.setVisible(true);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage adminPanelStage = new Stage();
            adminPanelStage.setTitle("");
            adminPanelStage.initStyle(StageStyle.TRANSPARENT);
            Image transparentImage = new Image(getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Icon.png"));
            adminPanelStage.getIcons().add(transparentImage);

            adminPanelStage.setScene(scene);
            controller.initialize(Student_X,adminPanelStage);
            adminPanelStage.show();

            this.primaryStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleForgetClicked() {
        errorMessageLabel.setText("");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        if (emailPattern.matcher(emailField.getText()).matches()) {
            // Show loading indicator
            loadingBox.setVisible(true);

                Task<Void> task = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        try {

                            Object user = Admin.SELECT_RECOVER_BY_EMAIL(emailField.getText());

                            if (user != null) {
                                String username = "";
                                String password = "";

                                if (user instanceof Student) {
                                    Student student = (Student) user;
                                    username = student.getUsername();
                                    password = student.getPassword();
                                } else if (user instanceof Professor) {
                                    Professor professor = (Professor) user;
                                    username = professor.getUsername();
                                    password = professor.getPassword();
                                }

                                EmailUtil.sendEmail(emailField.getText(), "Login Credentials", "Username: " + username + "\nPassword: " + password);
                            }
                        } catch (EmailNotFoundException e) {
                            throw e;
                        } catch (SQLException e) {
                            throw e;
                        }
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    // Hide loading indicator
                    loadingBox.setVisible(false);

                    LogPane.setDisable(false);
                    LogPane.setVisible(true);
                    RecoverPane.setDisable(true);
                    RecoverPane.setVisible(false);
                    errorMessageLabel.setText("Please check your email for a message");
                });

                task.setOnFailed(e -> {
                    loadingBox.setVisible(false);

                    Throwable exception = task.getException();
                    if (exception instanceof EmailNotFoundException) {
                        errorMessageLabel.setText(exception.getMessage());
                    } else if (exception instanceof SQLException) {
                        errorMessageLabel.setText("Database error: " + exception.getMessage());
                    }
                });

                new Thread(task).start();

        } else {
            errorMessageLabel.setText("Please enter a valid email address.");
        }
    }

    
    @FXML
    private void handleExitClicked() {
        this.primaryStage.close();
        //Alternative : Stage stage = (Stage) Exit.getScene().getWindow();
    }

    @FXML
    private void handleMinimizeClicked() {
        this.primaryStage.setIconified(true);
    }

    @FXML
    private void handleLinkedinClick(MouseEvent event) {
        openUrl("https://www.linkedin.com/in/melekelmoula");
    }

    @FXML
    private void handleGithubClick(MouseEvent event) {
        openUrl("https://github.com/melekelmoula");
    }

    private void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
