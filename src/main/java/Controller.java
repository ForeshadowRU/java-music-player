import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    @FXML
    public Label timeElapsed;
    @FXML
    public Label timeTotal;
    @FXML
    public ListView<String> list;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Slider songSlider;
    @FXML
    public ProgressBar songBar;
    @FXML
    public ImageView pauseButton;
    @FXML
    public ImageView playButton;
    @FXML
    public ImageView forwardButton;
    @FXML
    public ImageView backwardButton;
    @FXML
    public AnchorPane pane;
    @FXML
    public Pane viewContainer;

    private ArrayList<TrackView> views;

    private ArrayList<Track> loaded;

    private ArrayList<Track> selected;

    private Track currentTrack;


    private void addView(final Track track)
    {
        if (views == null) views = new ArrayList<>();
        TrackView view = new TrackView(track, views, viewContainer);
        viewContainer.getChildren().add(view.getView());
    }


    @FXML
    protected void initialize() {

        pauseButton.setVisible(false);
        songSlider.setMin(0);
        songSlider.setBlockIncrement(1);
        songSlider.setValue(0);
        songSlider.setMax(100);
        songBar.setProgress(0);
        songSlider.setValue(50);

        songSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (currentTrack!= null) songBar.setProgress(songSlider.getValue()/currentTrack.getPlayer().getCurrentTime().toSeconds());
                else
                {
                    songBar.setProgress(songSlider.getValue() / 100);
                }
            }
        });


        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playClick(null);
            }
        });


        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

            }
        });

        loaded = new ArrayList<Track>();



        volumeSlider.setMax(100);
        volumeSlider.setMin(0);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.setValue(50);

        list.setVisible(false);
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = list.getSelectionModel().getSelectedItem();

                if (event.getClickCount() == 2) {
                    for (Track load : loaded) {
                        if (load.getSource().getName().equals(selectedItem)) {
                            if (currentTrack != null) currentTrack.stop();
                            //selected = load;
                            //currentTrack = load;
                            playClick(null);
                        }
                    }


                } else if (event.getClickCount() == 1) {
                    for (Track load : loaded) {
                        if (load.getSource().getName().equals(selectedItem)) {
                            if (currentTrack != load) {
                                playButton.setVisible(true);
                                pauseButton.setVisible(false);
                            } else {
                                playButton.setVisible(false);
                                pauseButton.setVisible(true);
                            }
                            // selected = load;
                        }

                    }

                }
            }


        });
    }

    public void browseClick() {

        FileChooser browser = new FileChooser();
        browser.setTitle("Select file...");
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("Wav (*.wav)", "*.wav");
        browser.getExtensionFilters().add(mp3Filter);
        browser.getExtensionFilters().add(wavFilter);


        List<File> selections = browser.showOpenMultipleDialog(playButton.getScene().getWindow());

        if (selections.size() > 0) {
                for (File selected : selections) {
                    final Track selectedTrack = new Track(selected);
                    addView(selectedTrack);
                    loaded.add(selectedTrack);
                }

        }


    }

    private ChangeListener<Duration> sliderValueUpdater;
    private InvalidationListener songSliderInvalidationListener;
    private InvalidationListener volumeSliderInvalidationListener;


    public void playClick(ActionEvent actionEvent) {
        if (selected == null && currentTrack == null) return;

        if (currentTrack != null) currentTrack.stop();

        playButton.setVisible(false);
        pauseButton.setVisible(true);
        pauseButton.requestFocus();

        String selectedItem = list.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            for (Track track : loaded) {
                if (track.getSource().getName().equals(selectedItem)) {
                    //selected = track;
                    break;
                }
            }
            if (selected != null) {
                disposeCurrent();
                // currentTrack = selected;
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
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.requestFocus();
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
