import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Controller {
    @FXML
    public Label timeElapsed;
    @FXML
    public Label timeTotal;
    @FXML
    public ProgressBar volumeBar;
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
    @FXML
    public MenuItem settingsItem;

    private ArrayList<TrackView> views;

    private ArrayList<TrackView> selected;

    private List<TrackView> currentPlayList;

    private TrackView currentTrack;

    //------------------------------------------------------------------
    /** Use this var's for offset's control
     *
     */
    private double VIEW_HEIGHT = 60;
    private double VIEW_WIDTH = 500;
    private double UNSELECTED_Y = 10;
    private double UNSELECTED_X = 50;
    private double SELECTED_X = 20;

    private ChangeListener<Duration> sliderValueUpdater;
    private InvalidationListener songSliderInvalidationListener;
    private InvalidationListener volumeSliderInvalidationListener;
    private InvalidationListener songBarSliderSync;

    @FXML
    protected void initialize() {
        currentPlayList = new ArrayList<>();
        views = new ArrayList<>();
        selected = new ArrayList<>();

        pauseButton.setVisible(false);
        pauseButton.setOnMouseClicked(event -> pauseClick());
        playButton.setOnMouseClicked(event -> playClick());
        forwardButton.setOnMouseClicked(event -> forwardClick());
        backwardButton.setOnMouseClicked((event -> backwardClick()));

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                clearSelected();
            }
        });

        currentPlayList = new ArrayList<>();

        songSlider.setMin(0);
        songSlider.setBlockIncrement(1);
        songSlider.setMax(100);
        songSlider.setValue(0);
        songBar.setProgress(0);
        songSlider.setDisable(true);

        settingsItem.graphicProperty().set(new ImageView(new Image("img/menu/settings.png")));
        settingsItem.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("fxml/settings.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Settings");
                stage.setScene(new Scene(root, 450, 450));
                stage.getIcons().add(new Image("img/menu/settings.png"));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(30);
        volumeBar.setProgress(0.3);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> volumeBar.setProgress(volumeSlider.getValue() / 100));

    }

    private void addView(Track track) {
        TrackView view = new TrackView(track);
        view.getView().setPrefSize(VIEW_WIDTH, VIEW_HEIGHT);
        view.getView().setId("unselectedNode");
        view.getView().setLayoutX(UNSELECTED_X);
        if (views.size() == 0)
            view.getView().setLayoutY(10);
        else
            view.getView().setLayoutY(views.size() * VIEW_HEIGHT + views.size() * UNSELECTED_Y + UNSELECTED_Y);

        // TODO:
        // add more MenuItem and write for them function

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");

        delete.setOnAction(e -> {
            viewContainer.getChildren().remove(view.getView());
            if (selected.contains(view)) {
                if (currentTrack != null && currentTrack.equals(view)) {
                    currentTrack.getTrack().stop();
                    pauseButton.setVisible(false);
                    playButton.setVisible(true);
                    pauseButton.requestFocus();
                    disposeCurrent();
                }
                for (int k = views.indexOf(view) + 1; k < views.size(); k++) {
                    views.get(k).getView().setLayoutY((k - 1) * VIEW_HEIGHT + (k) * UNSELECTED_Y);
                }
                views.get(views.size() - 1).getView().setLayoutY((views.size() - 2) * VIEW_HEIGHT + (views.size() - 1) * UNSELECTED_Y);

                currentPlayList.remove(view);
                selected.remove(view);
            } else {
                for (int k = views.indexOf(view) + 1; k < views.size(); k++) {
                    views.get(k).getView().setLayoutY((k - 1) * VIEW_HEIGHT + (k) * UNSELECTED_Y);
                }
                views.get(views.size() - 1).getView().setLayoutY((views.size() - 2) * VIEW_HEIGHT + (views.size() - 1) * UNSELECTED_Y);
            }
            views.remove(view);
        });
        contextMenu.getItems().addAll(delete);
        view.getView().setOnContextMenuRequested(event -> contextMenu.show(view.getView(), event.getScreenX(), event.getScreenY()));


        view.getView().setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY && event.getButton() != MouseButton.MIDDLE) {
                if ((event.getClickCount() >= 2) && (event.getButton() == MouseButton.PRIMARY)) {
                    if (selected != null) clearSelected();
                    view.getView().setId("selectedNode");
                    selected.add(view);
                    view.getView().setLayoutX(SELECTED_X);
                    playClick();
                } else {
                    if (event.isControlDown() && (event.getButton() == MouseButton.PRIMARY)) {
                        selected.add(view);
                        view.getView().setLayoutX(SELECTED_X);
                        view.getView().setId("selectedNode");
                    } else {
                        if ( selected != null) clearSelected();
                        if ((event.getButton() == MouseButton.PRIMARY) && !selected.contains(view)) {//view.getView().getId() != "selectedNode")) {
                            selected.add(view);
                            view.getView().setLayoutX(SELECTED_X);
                            view.getView().setId("selectedNode");
                        }
                    }
                }
            }
        });

        ImageView image = new ImageView();
        image.setFitHeight(60);
        image.setFitWidth(60);
        image.setLayoutX(0);
        image.setLayoutY(0);
        view.getView().getChildren().add(image);

        Label metadata = new Label();
        metadata.setLayoutX(100);
        metadata.setLayoutY(5);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        view.getView().getChildren().add(metadata);
        metadata.textProperty().bind(track.artistProperty());

        Label time = new Label();
        time.setLayoutX((VIEW_WIDTH / 100 * 80));
        time.setLayoutY(VIEW_HEIGHT - 25);
        time.setId("label");
        time.setMaxWidth(80);

        view.getView().getChildren().add(time);

        metadata = new Label();
        metadata.setLayoutX((VIEW_WIDTH / 100 * 65));
        metadata.setLayoutY(5);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.titleProperty());
        view.getView().getChildren().add(metadata);


        metadata = new Label();
        metadata.setLayoutX(100);
        metadata.setLayoutY(30);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.albumProperty());
        view.getView().getChildren().add(metadata);
        view.getView().applyCss();


        track.getPlayer().setOnReady(() -> {
            if (track.getMedia().getMetadata().get("title") == null)
                track.titleProperty().set("Unknown Title");
            else
                track.titleProperty().set(track.getMedia().getMetadata().get("title").toString());

            if (track.getMedia().getMetadata().get("artist") == null)
                track.artistProperty().set("Unknown Artist");
            else
                track.artistProperty().set(track.getMedia().getMetadata().get("artist").toString());


            Image img = (Image) track.getPlayer().getMedia().getMetadata().get("image");
            if (img != null) image.setImage(img);
            else image.setImage(track.getImage());
            if (track.getMedia().getMetadata().get("album") == null) {
                track.albumProperty().set("Unknown Album");
            } else {
                track.albumProperty().set(track.getMedia().getMetadata().get("album").toString());
            }
            time.setText(timeFormat(track.getMedia().getDuration()));
        });
        viewContainer.getChildren().add(view.getView());
        views.add(view);
    }

    private void clearSelected() {
        for (TrackView sel : selected) {
            sel.getView().setId("unselectedNode");
            sel.getView().setLayoutX(UNSELECTED_X);
        }
        currentPlayList.clear();
        selected.clear();
    }

    private void forwardClick() {
        if (currentTrack != null) {
            int index = currentPlayList.indexOf(currentTrack) + 1;
            currentTrack.getTrack().stop();
            disposeCurrent();
            if (index < currentPlayList.size()) {
                play(index);
            } else {
                pauseButton.setVisible(false);
                playButton.setVisible(true);
            }
        }
        // TODO implement repeat mechanism
    }

    private void backwardClick() {
        if (currentTrack != null) {
            int index = currentPlayList.indexOf(currentTrack) - 1;
            currentTrack.getTrack().stop();
            disposeCurrent();
            if (index >= 0) {
                play(index);
            } else {
                pauseButton.setVisible(false);
                playButton.setVisible(true);
            }
        }
        // TODO implement repeat mechanism
    }

    private void searchForMp3(File directory) {
        if (!directory.isDirectory() || directory.listFiles() == null) return;
        List<File> output = new ArrayList<>();

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) searchForMp3(file);
            else {
                if (file.getName().endsWith(".mp3")) {
                    output.add(file);
                }
            }
        }
        parse(output);
    }

    public void deleteClick() {
        if (selected == null) return;
        if (currentTrack != null) {
            currentTrack.getTrack().stop();
            pauseButton.setVisible(false);
            playButton.setVisible(true);
            pauseButton.requestFocus();
        }

        ArrayList<TrackView> sortedSelect = new ArrayList<>();
        for (TrackView view : views) {
            for (TrackView select : selected) {
                if (view.equals(select)) {
                    sortedSelect.add(select);
                }
            }
        }
        for (int i = 0; i < views.size(); i++) {
            for (int j = 0; j < sortedSelect.size(); j++) {
                if (views.get(i).equals(sortedSelect.get(j))) {
                    viewContainer.getChildren().remove(sortedSelect.get(j).getView());

                    for (int k = i + 1; k < views.size(); k++) {
                        views.get(k).getView().setLayoutY((k - 1) * VIEW_HEIGHT + (k) * UNSELECTED_Y);
                    }
                    views.get(views.size() - 1).getView().setLayoutY((views.size() - 2) * VIEW_HEIGHT + (views.size() - 1) * UNSELECTED_Y);
                    views.remove(i);
                }
            }
        }
        sortedSelect.clear();
        currentPlayList.clear();
        selected.clear();
        disposeCurrent();
    }

    @SuppressWarnings("ConstantConditions")
    public void browseFolderClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home").concat("/Music")));
        chooser.setTitle("Choose folder for search");
        File selected = chooser.showDialog(pane.getScene().getWindow());
        if (selected == null || selected.listFiles() == null) return;

        searchForMp3(selected);
    }

    private void parse(List<File> files) {
        for (File selectedFile : files) {
            Track selectedTrack = new Track(selectedFile);
            addView(selectedTrack);
        }
    }

    public void aboutClick() {
        Alert kek = new Alert(Alert.AlertType.INFORMATION);
        kek.setTitle("NANI?");
        kek.setHeaderText("Authors:");
        kek.setContentText("Макс Keeper Максутов \nИлья Jesper Красов");
        kek.show();
    }

    public void browseClick() {
        FileChooser browser = new FileChooser();
        browser.setTitle("Select file...");
        browser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("Wav (*.wav)", "*.wav");
        browser.getExtensionFilters().add(mp3Filter);
        browser.getExtensionFilters().add(wavFilter);
        List<File> selected = browser.showOpenMultipleDialog(pane.getScene().getWindow());
        if (selected == null) return;
        parse(selected);
    }

    private void play (int index) {
        songSlider.setDisable(false);
        currentTrack = currentPlayList.get(index);
        currentTrack.getTrack().getPlayer().play();
        currentTrack.getTrack().getPlayer().setVolume(volumeSlider.getValue() / 100);
        currentTrack.getTrack().getPlayer().setOnEndOfMedia(() -> {
            if (index + 1 < currentPlayList.size()) {
                play(index + 1);
            } else {
                currentTrack.getTrack().getPlayer().stop();
                pauseButton.setVisible(false);
                playButton.setVisible(true);
            }
            disposeCurrent();
        });
        songSlider.setMin(0);
        songSlider.setMax((int) currentTrack.getTrack().getPlayer().getMedia().getDuration().toSeconds());

        songSlider.setBlockIncrement(1);

        songBarSliderSync = observable ->
        {
            songBar.setProgress(songSlider.getValue() / songSlider.getMax());
        };
        songSlider.valueProperty().addListener(songBarSliderSync);

        sliderValueUpdater = (observable, oldValue, newValue) -> {
            songSlider.setValue((int) currentTrack.getTrack().getPlayer().getCurrentTime().toSeconds());
        };
        currentTrack.getTrack().getPlayer().currentTimeProperty().addListener(sliderValueUpdater);

        songSliderInvalidationListener = observable -> {
            if (songSlider.isValueChanging()) {
                currentTrack.getTrack().getPlayer().seek(Duration.seconds((int) (songSlider.getValue())));
            }
        };
        songSlider.valueProperty().addListener(songSliderInvalidationListener);

        volumeSliderInvalidationListener = observable -> currentTrack.getTrack().getPlayer().setVolume(volumeSlider.getValue() / 100);
        volumeSlider.valueProperty().addListener(volumeSliderInvalidationListener);
    }

    private void playClick() {
        if (selected.size() == 0) return;
        if (!selected.equals(currentPlayList)) {
            if (currentTrack != null) currentTrack.getTrack().stop();
            currentPlayList.clear();
            currentPlayList = (List<TrackView>) selected.clone();
        }

        play(0);

        pauseButton.setVisible(true);
        pauseButton.requestFocus();
        playButton.setVisible(false);
    }

    public void pauseClick() {
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.requestFocus();
        currentTrack.getTrack().pause();
    }

    public void disposeCurrent() {
        if (currentTrack != null && sliderValueUpdater != null)
            currentTrack.getTrack().getPlayer().currentTimeProperty().removeListener(sliderValueUpdater);
        if (currentTrack != null && songSliderInvalidationListener != null)
            songSlider.valueProperty().removeListener(songSliderInvalidationListener);
        if (currentTrack != null && volumeSliderInvalidationListener != null)
            volumeSlider.valueProperty().removeListener(volumeSliderInvalidationListener);
        if (currentTrack != null) currentTrack.getTrack().getPlayer().setOnEndOfMedia(null);
        if (currentTrack != null) songSlider.valueProperty().removeListener(songBarSliderSync);
    }

    private String timeFormat(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() - minutes * 60;
        if (seconds < 10) return Integer.toString(minutes).concat(":0").concat(Integer.toString(seconds));
        else return Integer.toString(minutes).concat(":").concat(Integer.toString(seconds));
    }
}
