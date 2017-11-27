import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    private MediaPlayer player;

    private Track selected;
    private Track currentlyPlaying;
    @FXML
    Label time;
    @FXML
    public ListView<String> list;
    @FXML
    public Button browseBtn;
    @FXML
    private FileChooser browser;
    @FXML
    public Button playBtn;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Button pauseBtn;
    private ArrayList<Track> loaded = null;

    @FXML
    public Slider songSlider;

    private void updateVolumeSliderValue()
    {
        volumeSlider.setValue(player.getVolume() * 100);
    }
    private void updateSongSliderValue()
    {
        songSlider.setMax(currentlyPlaying.getMedia().getDuration().toSeconds());
        songSlider.setMin(0);
        songSlider.setValue(player.getCurrentTime().toSeconds());
        time.setText(Double.toString(player.getCurrentTime().toSeconds()));
    }

    private void onFirstTimePlay() {

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue observable, Duration oldValue, Duration newValue) {
                updateSongSliderValue();


            }

        });
    }
    @FXML
    protected void initialize()
    {




        loaded = new ArrayList<Track>();
        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging())
                {
                    player.setVolume(volumeSlider.getValue() / 100);
                }
                updateVolumeSliderValue();
            }
        });

        songSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                if (songSlider.isValueChanging())
                {
                    player.seek(currentlyPlaying.getMedia().getDuration().multiply(songSlider.getValue() / 100.0));
                }
                updateSongSliderValue();
                }
            });



        list.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = list.getSelectionModel().getSelectedItem();
                if(event.getClickCount() == 2)
                {
                    if (player == null)
                    {
                        for(Track load : loaded)
                        {
                            if (load.getSource().getName().equals(selectedItem)) {
                                play(load);
                            }
                        }
                    }

                    assert player != null;
                    if (player.getStatus().equals(MediaPlayer.Status.PLAYING))
                    {
                        player.stop();
                    }
                    for(Track load : loaded)
                    {
                        if (load.getSource().getName().equals(selectedItem)) {
                            play(load);
                        }

                    }
                }else if (event.getClickCount() == 1)
                {
                    for(Track load : loaded)
                    {
                        if (load.getSource().getName().equals(selectedItem)) {
                            selected = load;
                        }

                    }

                }

            }
        });
    }


    public void browseClick(javafx.event.ActionEvent actionEvent) {

        browser = new FileChooser();
        browser.setTitle("Select file...");
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("Wav (*.wav)", "*.wav");
        browser.getExtensionFilters().add(wavFilter);
        browser.getExtensionFilters().add(mp3Filter);

        List<File> selections = browser.showOpenMultipleDialog(browseBtn.getScene().getWindow());
        if (selections.size() > 0)
        {
            for(File selected : selections)
            {
                Track selectedTrack = new Track(selected);
                ObservableList<String> items = list.getItems();
                items.add(selectedTrack.getSource().getName());
                list.setItems(items);
                loaded.add(selectedTrack);
            }
        }

    }

    public void play(Track track)
    {
        playBtn.setVisible(false);
        pauseBtn.setVisible(true);
        pauseBtn.requestFocus();
        if (player == null)
        {
            player = new MediaPlayer(track.getMedia());
            onFirstTimePlay();
            selected = track;
            currentlyPlaying = selected;
            player.play();

        }else
        {
            if (player.getStatus().equals(MediaPlayer.Status.PLAYING))
            {
                if (!track.equals(currentlyPlaying))
                {
                    player = new MediaPlayer(track.getMedia());
                    selected = track;
                    currentlyPlaying = selected;
                    player.play();
                }else
                {
                    player.seek(Duration.ZERO);
                }
            }
            if (player.getStatus().equals(MediaPlayer.Status.PAUSED))
            {
                if (!track.equals(currentlyPlaying))
                {
                    player = new MediaPlayer(track.getMedia());
                    selected = track;
                    currentlyPlaying = selected;
                    player.play();
                }else
                {
                    currentlyPlaying = selected;
                    player.play();
                }
            }
        }


    }

    public void playClick(ActionEvent actionEvent) {

        if (selected != null)
        {
            play(selected);
        }

    }

    public void pauseClick(ActionEvent actionEvent) {
        playBtn.setVisible(true);
        pauseBtn.setVisible(false);
        playBtn.requestFocus();
        player.pause();

    }
}
