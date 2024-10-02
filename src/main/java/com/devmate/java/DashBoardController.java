package com.devmate.java;

import com.devmate.java.Admin.*;
import com.devmate.java.Professor.Professor;
import com.devmate.java.Professor.ProfessorExamsTableController;
import com.devmate.java.Professor.ProfessorResultsTableController;
import com.devmate.java.Student.Student;
import com.devmate.java.Student.StudentExamsTableController;
import com.devmate.java.Student.StudentResultTableController;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class DashBoardController {
    @FXML
    private Button MinimizeButton;
    @FXML
    private Pane MainPage;
    @FXML
    private Button Result;
    @FXML
    private Button ExitButton;
    @FXML
    private Pane contentArea;
    @FXML
    public Button Exams;
    @FXML
    public Button Results;
    @FXML
    public MFXToggleButton toggleButton;
    @FXML
    public ImageView Logout;
    @FXML
    public ImageView Background;
    @FXML
    private ImageView Exit;
    @FXML
    private ImageView Minimize;

    private String Username;
    private String Role;
    private int Pageselected =1;
    private Boolean Theme =false;
    private Professor Professor_X;
    private Student Student_X;
    private Admin Admin_X;

    private double x= 0,y=0;
    Stage stage;

    public <T> void initialize(T userObject, Stage Stage) throws IOException {

        this.stage=Stage;

        MainPage.setOnMousePressed(mouseEvent -> {
            x=mouseEvent.getSceneX();
            y=mouseEvent.getSceneY();
        });

        MainPage.setOnMouseDragged(mouseEvent -> {
            Stage.setX(mouseEvent.getScreenX()-x);
            Stage.setY(mouseEvent.getScreenY()-y);
        });

        if (userObject instanceof Professor){
            Professor_X = (Professor) userObject;
            Username = Professor_X.getUsername();
            Role ="Professors";
            ThemeSelection();
            Quiz();

        }
        if (userObject instanceof Student){
            Student_X = (Student) userObject;
            Username = Student_X.getUsername();
            Role ="Students";
            ThemeSelection();
            Exams();
        }
        if (userObject instanceof Admin){
            Admin_X = (Admin) userObject;
            Username =Admin_X.getUsername();
            Role ="Admins";
            ThemeSelection();
            PathTable();
        }

    }

        public void ThemeSelection() {
        Boolean UserTheme = Admin.SELECT_THEME(Username, Role);
        if (UserTheme) {
            toggleButton.setSelected(true);
            Theme = true;
        } else {
            toggleButton.setSelected(false);
            Theme = false;
            javafx.scene.effect.Effect existingEffect = toggleButton.getEffect();

            if (existingEffect instanceof ColorAdjust) {
                ColorAdjust colorAdjust = (ColorAdjust) existingEffect;
                colorAdjust.setBrightness(1);
            }
                }
         }

        public void ThemeChange() throws IOException {
        if (toggleButton.isSelected()){
            Theme =true;
            javafx.scene.effect.Effect existingEffect = toggleButton.getEffect();

            if (existingEffect instanceof ColorAdjust) {
                ColorAdjust colorAdjust = (ColorAdjust) existingEffect;
                colorAdjust.setBrightness(-0.85);
            }
        }
        else {
            Theme =false;
            javafx.scene.effect.Effect existingEffect = toggleButton.getEffect();

            if (existingEffect instanceof ColorAdjust) {
                ColorAdjust colorAdjust = (ColorAdjust) existingEffect;
                colorAdjust.setBrightness(1.0);
            }
        }

        Admin.UPDATE_THEME(Theme, this.Username, this.Role);

        switch (Pageselected) {
            case 1:
                ProfessorTable();
                break;
            case 2:
                StudentTable();
                break;
            case 3:
                PathTable();
                break;
            case 4:
                SubjectsTable();
                break;
            case 5:
                GroupsTable();
                break;
            case 6:
                Quiz();
                break;
            case 7:
                Result();
                break;
            case 8:
                Exams();
                break;
            case 9:
                Results();
                break;
        }
    }

    @FXML
    public void ProfessorTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/ProfessorTable.fxml"));
        Parent root = loader.load();
        AdminProfessorTableController controller = loader.getController();
        controller.initialize(this.Admin_X,this.Theme); // Pass the admin object to the controller
        contentArea.getChildren().clear();
        contentArea.getChildren().add(root);
        Pageselected =1;
    }

    @FXML
    public void StudentTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/StudentTable.fxml"));
        Parent root = loader.load();
        AdminStudentTableController controller = loader.getController();
        controller.initialize(Admin_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =2;
    }

    @FXML
    public void PathTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/PathTable.fxml"));
        Parent root = loader.load();
        AdminPathTableController controller = loader.getController();
        controller.initialize(Admin_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().clear();
        contentArea.getChildren().add(root);
        Pageselected =3;
    }


    @FXML
    public void SubjectsTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/SubjectTable.fxml"));
        Parent root = loader.load();
        AdminSubjectsTableController controller = loader.getController();
        controller.initialize(Admin_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =4;
    }

    @FXML
    public void GroupsTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Admin/GroupTable.fxml"));
        Parent root = loader.load();
        AdminGroupsTableController controller = loader.getController();
        controller.initialize(Admin_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =5;
    }


    public void Quiz() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Professor/ExamsTable.fxml"));
        Parent root = loader.load();
        ProfessorExamsTableController controller = loader.getController();
        controller.initialize(Professor_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =6;
    }

    public void Result() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Professor/ResultsTable.fxml"));
        Parent root = loader.load();
        ProfessorResultsTableController controller = loader.getController();
        controller.initialize(Professor_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =7;
    }

    public void Exams() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Student/ExamTable.fxml"));
        Parent root = loader.load();
        StudentExamsTableController controller = loader.getController();
        controller.setExams2(this.Exams,this.Results,this.toggleButton,this.Logout,this.ExitButton);
        controller.initialize(Student_X, Theme,stage); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =8;
    }

    public void Results() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Student/ResultTable.fxml"));
        Parent root = loader.load();
        StudentResultTableController controller = loader.getController();
        controller.initialize(Student_X, Theme); // Pass the admin object to the controller
        contentArea.getChildren().setAll(root);
        Pageselected =9;
    }

    @FXML
    private void handleExitClicked() {
        // Get the stage from any component in the scene
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeClicked() {
        Stage stage = (Stage) Minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    public void logoutClicked(MouseEvent mouseEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/devmate/java/Login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController(); // Get the controller instance
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Login");
        controller.initialize(stage);
        stage.centerOnScreen();
        Image transparentImage = new Image(getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Icon.png"));
        stage.getIcons().add(transparentImage);
        stage.show();
        Stage currentStage = (Stage) contentArea.getScene().getWindow();
        currentStage.close();
    }

}
