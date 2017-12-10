import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    public static void main (String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("JPlayer");
        stage.getIcons().add(new Image("/img/controlPanel/window.ico"));
        stage.getIcons().add(new Image("/img/controlPanel/window.png"));
        stage.show();
    }
}
