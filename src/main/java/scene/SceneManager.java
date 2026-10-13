package scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    public static void switchPage(String fxmlFile){

        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));

        try {
            Parent parent = loader.load();

            Scene scene = new Scene(parent);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
