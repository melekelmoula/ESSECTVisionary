package com.devmate.java;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController(); // Get the controller instance
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        controller.initialize(primaryStage);
        primaryStage.centerOnScreen();
        primaryStage.show();
        Image transparentImage = new Image(getClass().getResourceAsStream("/com/devmate/java/Admin/Essect_Icon.png"));
        primaryStage.getIcons().add(transparentImage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
