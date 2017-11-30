import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
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

    private ArrayList<TrackView> views;

    private ArrayList<TrackView> selected;

    private List<TrackView> currentPlayList;

    private Track currentTrack;

    private void addView(Track track) {

        /** Use this var's for offset's control
         *
         */
        double VIEW_HEIGHT = 60;
        double VIEW_WIDTH = 500;
        double UNSELECTED_Y = 10;
        double UNSELECTED_X = 50;
        double SELECTED_X = 20;
//------------------------------------------------------------------


        TrackView view = new TrackView(track);
        view.getView().setPrefSize(VIEW_WIDTH, VIEW_HEIGHT);
        view.getView().setId("unselectedNode");
        view.getView().setLayoutX(UNSELECTED_X);
        if (views.size() == 0)
            view.getView().setLayoutY(10);
        else
            view.getView().setLayoutY(views.size() * VIEW_HEIGHT + views.size() * UNSELECTED_Y + UNSELECTED_Y);




        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Delete");
        deleteItem.setOnAction(event -> viewContainer.getChildren().remove(view.getView()));
        menu.getItems().add(deleteItem);

        /**TODO: Write selection logic here.
         * Single Click: select only clicked. +
         * Double Click: play only clicked; +
         * Single Click on Selected: do nothing;
         * Single Click with Shift Pressed: add clicked to selection;
         * Single Click with Shift Pressed on Selected: remove clicked from selection;
         * Double Click with Shift selected: play all selected;
         *
         * select : move pane to SELECTED_X offset, and change it's id to "selectedNode";
         *
         */

        view.getView().setOnMousePressed(event -> {
            if ((event.getClickCount() == 2) && (event.getButton() == MouseButton.PRIMARY)) {
                for (TrackView temp : views) {
                    if (temp.getView().getId() == "selectedNode") {
                        selected.remove(temp);
                        temp.getView().setId("unselectedNode");
                        temp.getView().setLayoutX(UNSELECTED_X);
                    }
                }
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
                    for (TrackView temp : views) {
                        if (temp.getView().getId() == "selectedNode") {
                            selected.remove(temp);
                            temp.getView().setId("unselectedNode");
                            temp.getView().setLayoutX(UNSELECTED_X);
                        }
                    }
                    if ((event.getButton() == MouseButton.PRIMARY) && !selected.contains(view)) {//view.getView().getId() != "selectedNode")) {
                        selected.add(view);
                        view.getView().setLayoutX(SELECTED_X);
                        view.getView().setId("selectedNode");
                    }
                }
            }
        });

        view.getView().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.isShiftDown()) {

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
        }
        selected.clear();
    }

    private InvalidationListener songBarSliderSync;

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


    @SuppressWarnings("ConstantConditions")
    public void browseFolderClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home").concat("/Music")));
        chooser.setTitle("Choose folder for search");
        File selected = chooser.showDialog(pane.getScene().getWindow());
        if (selected == null || selected.listFiles() == null) return;

        searchForMp3(selected);

    }

    public void parse(List<File> files) {
        for (File selectedFile : files) {
            Track selectedTrack = new Track(selectedFile);
            addView(selectedTrack);
        }
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

    private ChangeListener<Duration> sliderValueUpdater;
    private InvalidationListener songSliderInvalidationListener;
    private InvalidationListener volumeSliderInvalidationListener;

    @FXML
    protected void initialize() {
        currentPlayList = new ArrayList<>();
        views = new ArrayList<>();
        selected = new ArrayList<>();

        pauseButton.setVisible(false);
        pauseButton.setOnMouseClicked(event -> pauseClick());
        playButton.setOnMouseClicked(event -> playClick());

        pane.setOnKeyPressed(event ->
        {
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

        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(30);
        volumeBar.setProgress(0.3);
        volumeSlider.setBlockIncrement(1);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> volumeBar.setProgress(volumeSlider.getValue() / 100));

    }

    private void play (int index) {
        songSlider.setDisable(false);

        currentTrack = currentPlayList.get(index).getTrack();
        currentTrack.getPlayer().play();
        currentTrack.getPlayer().setVolume(volumeSlider.getValue() / 100);
        currentTrack.getPlayer().setOnEndOfMedia(() -> {
            if (index + 1 < currentPlayList.size()) {
                play (index + 1 );
            } else {
                currentTrack.getPlayer().stop();
            }
            disposeCurrent();

        });
        songSlider.setMax((int) currentTrack.getPlayer().getMedia().getDuration().toSeconds());
        songSlider.setMin(0);
        songSlider.setBlockIncrement(1);

        songBarSliderSync = observable ->
        {
            songBar.setProgress(songSlider.getValue() / songSlider.getMax());

        };
        songSlider.valueProperty().addListener(songBarSliderSync);

        sliderValueUpdater = (observable, oldValue, newValue) -> {
            songSlider.setValue((int) currentTrack.getPlayer().getCurrentTime().toSeconds());
        };
        currentTrack.getPlayer().currentTimeProperty().addListener(sliderValueUpdater);

        songSliderInvalidationListener = observable -> {
            if (songSlider.isValueChanging()) {
                currentTrack.getPlayer().seek(Duration.seconds((int) (songSlider.getValue())));
            }
        };
        songSlider.valueProperty().addListener(songSliderInvalidationListener);

        volumeSliderInvalidationListener = observable -> currentTrack.getPlayer().setVolume(volumeSlider.getValue() / 100);
        volumeSlider.valueProperty().addListener(volumeSliderInvalidationListener);

    }
    private void playClick() {

        if (selected.size() == 0) return;
        if (!selected.equals(currentPlayList)) {
            currentPlayList.clear();
            if (currentTrack != null) currentTrack.stop();
            currentPlayList = (List<TrackView>) selected.clone();
        }


        play(0);

        pauseButton.setVisible(true);
        pauseButton.requestFocus();
        playButton.setVisible(false);

    }

    public void disposeCurrent()
    {
        if (currentTrack != null && sliderValueUpdater != null)
            currentTrack.getPlayer().currentTimeProperty().removeListener(sliderValueUpdater);
        if (currentTrack != null && songSliderInvalidationListener != null)
            songSlider.valueProperty().removeListener(songSliderInvalidationListener);
        if (currentTrack != null && volumeSliderInvalidationListener != null)
            volumeSlider.valueProperty().removeListener(volumeSliderInvalidationListener);
        if (currentTrack != null) currentTrack.getPlayer().setOnEndOfMedia(null);
        if (currentTrack != null) songSlider.valueProperty().removeListener(songBarSliderSync);

    }


    private String timeFormat(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() - minutes * 60;
        if (seconds < 10) return Integer.toString(minutes).concat(":0").concat(Integer.toString(seconds));
        else return Integer.toString(minutes).concat(":").concat(Integer.toString(seconds));

    }

    public void pauseClick() {
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.requestFocus();
        currentTrack.pause();
    }

}
