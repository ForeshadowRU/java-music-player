import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application{

    public static Parent root;
    public static void main (String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("JPlayer");

        stage.show();
    }

}
