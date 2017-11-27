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

    private Track selected;
    private Track currentTrack;
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


    @FXML
    protected void initialize() {
        loaded = new ArrayList<Track>();

        songSlider.setMin(0);
        songSlider.setBlockIncrement(1);
        songSlider.setValue(0);

        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.setValue(50);

        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = list.getSelectionModel().getSelectedItem();

                if (event.getClickCount() == 2) {
                    for (Track load : loaded) {
                        if (load.getSource().getName().equals(selectedItem)) {
                            if (currentTrack != null) currentTrack.stop();
                            selected = load;
                            currentTrack = load;
                            playClick(null);
                        }
                    }


                } else if (event.getClickCount() == 1) {
                    for (Track load : loaded) {
                        if (load.getSource().getName().equals(selectedItem)) {
                            if (currentTrack != load) {
                                playBtn.setVisible(true);
                                pauseBtn.setVisible(false);
                            } else {
                                playBtn.setVisible(false);
                                pauseBtn.setVisible(true);
                            }
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
        if (selections.size() > 0) {
            ObservableList<String> items = list.getItems();
            for (File selected : selections) {
                Track selectedTrack = new Track(selected);
                items.add(selectedTrack.getSource().getName());
                loaded.add(selectedTrack);
            }
            list.setItems(items);
        }

    }

    ChangeListener<Duration> sliderValueUpdater;
    InvalidationListener songSliderInvalidationListener;
    InvalidationListener volumeSliderInvalidationListener;


    public void playClick(ActionEvent actionEvent) {
        if (currentTrack != null) currentTrack.stop();

        playBtn.setVisible(false);
        pauseBtn.setVisible(true);
        pauseBtn.requestFocus();

        String selectedItem = list.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            for (Track track : loaded) {
                if (track.getSource().getName().equals(selectedItem)) {
                    selected = track;
                    break;
                }
            }
            if (selected != null) {
                disposeCurrent();
                currentTrack = selected;
                songSlider.setMax((int) currentTrack.getMedia().getDuration().toSeconds());


                sliderValueUpdater = new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        songSlider.setValue((int) currentTrack.getPlayer().getCurrentTime().toSeconds());
                        updateTimeLabelValue();
                    }
                };

                currentTrack.getPlayer().currentTimeProperty().addListener(sliderValueUpdater);


                currentTrack.getPlayer().setVolume(volumeSlider.getValue() / 100);

                volumeSliderInvalidationListener  = new InvalidationListener() {
                    @Override
                    public void invalidated(Observable ov) {
                        if (volumeSlider.isValueChanging()) {
                            currentTrack.getPlayer().setVolume(volumeSlider.getValue() / 100);
                        }
                        updateVolumeSliderValue();
                    }
                };

                volumeSlider.valueProperty().addListener(volumeSliderInvalidationListener);

                songSliderInvalidationListener = new InvalidationListener() {
                    @Override
                    public void invalidated(Observable ov) {
                        if (songSlider.isValueChanging()) {
                            currentTrack.getPlayer().seek(Duration.seconds(Math.floor(songSlider.getValue())));
                        }
                    }
                };
                songSlider.valueProperty().addListener(songSliderInvalidationListener);
                currentTrack.play();
            }
        }
    }

    public void disposeCurrent()
    {
        if (currentTrack != null && sliderValueUpdater != null) currentTrack.getPlayer().currentTimeProperty().removeListener(sliderValueUpdater);
        if (currentTrack != null && songSliderInvalidationListener != null) songSlider.valueProperty().removeListener(songSliderInvalidationListener);
        if (currentTrack != null && volumeSliderInvalidationListener != null) volumeSlider.valueProperty().removeListener(volumeSliderInvalidationListener);
    }
    public void pauseClick(ActionEvent actionEvent) {
        playBtn.setVisible(true);
        pauseBtn.setVisible(false);
        playBtn.requestFocus();
        currentTrack.pause();


    }

    private void updateVolumeSliderValue() {
        volumeSlider.setValue(currentTrack.getPlayer().getVolume() * 100);
    }

    private void updateTimeLabelValue() {
        timeElapsed.setText(Integer.toString((int) currentTrack.getPlayer().getCurrentTime().toSeconds() / 60) + ":" + Integer.toString((int) currentTrack.getPlayer().getCurrentTime().toSeconds() % 60));
        timeTotal.setText(Integer.toString((int) currentTrack.getMedia().getDuration().toSeconds() / 60) + ":" + Integer.toString((int) currentTrack.getMedia().getDuration().toSeconds() % 60));
    }

}
