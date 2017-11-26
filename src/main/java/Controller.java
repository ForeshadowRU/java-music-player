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

    private MediaPlayer player;

    private Media selected;


    @FXML
    public ListView<String> list;
    @FXML
    public Button browseBtn;
    @FXML
    private FileChooser browser;
    @FXML
    public Button playBtn;
    @FXML
    public Button pauseBtn;
    private ArrayList<Track> loaded = null;

    @FXML
    protected void initialize()
    {
        loaded = new ArrayList<Track>();

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
            Track selectedTrack = new Track(selected);
            ObservableList<String> items = list.getItems();
            items.add(selectedTrack.getSource().getName());
            list.setItems(items);
            loaded.add(selectedTrack);
        }else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing file");
            alert.setHeaderText(null);
            alert.setContentText("File isn't valid!");
            alert.showAndWait();
        }

    }

    public void play(Media media)
    {
        if (player == null)
        {
            player = new MediaPlayer(media);
            selected = media;
            player.play();

        }else
        {
            if (player.getStatus().equals(MediaPlayer.Status.PLAYING))
            {
                if (!media.equals(selected))
                {
                    player = new MediaPlayer(media);
                    selected = media;
                    player.play();
                }
            }
            if (player.getStatus().equals(MediaPlayer.Status.PAUSED))
            {
                if (!media.equals(selected))
                {
                    player = new MediaPlayer(media);
                    selected = media;
                    player.play();
                }else
                {
                    player.play();
                }
            }
        }


    }

    public void playClick(ActionEvent actionEvent) {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
        playBtn.setVisible(false);
        pauseBtn.setVisible(true);

            for(Track load : loaded)
            {
                if (load.getSource().getName().equals(selected)) {
                    play(load.getMedia());
                }

            }
        }

    }

    public void pauseClick(ActionEvent actionEvent) {
        playBtn.setVisible(true);
        pauseBtn.setVisible(false);
        player.pause();

    }
}
