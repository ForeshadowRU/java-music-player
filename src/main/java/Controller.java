import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    private MediaPlayer player;
    private int lastOrder = 0;
    private Track selected;
    private Track currentlyPlaying;
    @FXML
    public Label timeElapsed;
    @FXML
    public Label timeTotal;
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
        timeElapsed.setText( Integer.toString( (int) player.getCurrentTime().toSeconds() / 60) + ":" + Integer.toString((int)player.getCurrentTime().toSeconds() % 60) );
        timeTotal.setText( Integer.toString( (int) currentlyPlaying.getMedia().getDuration().toSeconds() / 60) + ":" + Integer.toString((int)currentlyPlaying.getMedia().getDuration().toSeconds() % 60)   );
    }

    private void onFirstTimePlay()
    {
        assert player != null;

        songSlider.setMin(0);
        songSlider.setBlockIncrement(1);
        songSlider.setValue(0);
        player.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                songSlider.setMax(currentlyPlaying.getMedia().getDuration().toSeconds());
                if ((int) player.getCurrentTime().toSeconds() <= songSlider.getMax())
                {

                    songSlider.setValue((int) player.getCurrentTime().toSeconds());

                }

            }

        });
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
            }
        });
        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.setValue(50);
        player.setVolume(volumeSlider.getValue() / 100);


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

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue observable, Duration oldValue, Duration newValue) {
                updateSongSliderValue();
            }

        });
        songSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                if (songSlider.isValueChanging())
                {
                    player.seek(Duration.seconds(Math.floor(songSlider.getValue())));
                }
                updateSongSliderValue();
            }
        });
    }
    @FXML
    protected void initialize()
    {
        loaded = new ArrayList<Track>();


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
            ObservableList<String> items = list.getItems();
            for(File selected : selections)
            {
                Track selectedTrack = new Track(selected);
                selectedTrack.setOrder(lastOrder++);
                items.add(selectedTrack.getSource().getName());
                loaded.add(selectedTrack);
            }
            list.setItems(items);
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
            selected = track;
            currentlyPlaying = selected;
            onFirstTimePlay();


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
