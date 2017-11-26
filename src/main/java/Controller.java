import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Controller {

    @FXML
    public ListView<String> list;
    @FXML
    public Button browseBtn;
    @FXML
    public FileChooser browser;
    @FXML
    public Button playBtn;

    private ArrayList<File> loaded = null;

    @FXML
    protected void initialize()
    {
        loaded = new ArrayList<File>();

    }


    public void browseClick(javafx.event.ActionEvent actionEvent) {
        browser = new FileChooser();
        browser.setTitle("Select file...");
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("Wav (*.wav)", "*.wav");
        browser.getExtensionFilters().add(wavFilter);
        browser.getExtensionFilters().add(mp3Filter);
        File selected = browser.showOpenDialog(browseBtn.getScene().getWindow());
        if (selected != null)
        {
            ObservableList<String> items = list.getItems();
            items.add(selected.getName());
            list.setItems(items);
            loaded.add(selected);
        }else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing file");
            alert.setHeaderText(null);
            alert.setContentText("File isn't valid!");
            alert.showAndWait();
        }

    }

    public void play(File media)
    {
        Media track = new Media(media.toURI().toString());
        MediaPlayer player = new MediaPlayer(track);
        player.play();


    }

    public void playClick(ActionEvent actionEvent) {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            for(File load : loaded)
            {
                if (load.getName().equals(selected))
                {
                    play(load);
                }
            }
        }

    }
}
