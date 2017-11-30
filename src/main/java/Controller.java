import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
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

    private List<Track> currentPlayList;

    private Track currentTrack;

    private void addView(Track track) {

        /** Use this var's for offset's control
         *
         */
        double VIEW_HEIGHT = 60;
        double VIEW_WIDTH = 500;
        double UNSELECTED_X = 50;
        double UNSELECTED_Y_OFFSET = 10;
        double SELECTED_X = 20;
//------------------------------------------------------------------


        TrackView view = new TrackView(track);
        view.getView().setPrefSize(VIEW_WIDTH,VIEW_HEIGHT);
        view.getView().setId("unselectedNode");
        view.getView().setLayoutX(UNSELECTED_X);
        if (views.size() == 0)
            view.getView().setLayoutY(10);
        else
//<<<<<<< HEAD
            view.getView().setLayoutY(views.size() * VIEW_HEIGHT + views.size() * UNSELECTED_Y_OFFSET + UNSELECTED_Y_OFFSET);
//=======
            //view.getView().setLayoutY(views.get(views.size() - 1).getView().getLayoutY() + UNSELECTED_Y_OFFSET + VIEW_HEIGHT * views.size());
//>>>>>>> 517175e261e46df7ae5e95c9f777639d7174f7ff

        track.getPlayer().setOnReady(() -> {
            track.titleProperty().set(track.getMedia().getMetadata().get("title").toString());
            track.artistProperty().set(track.getMedia().getMetadata().get("artist").toString());

            if (track.getMedia().getMetadata().get("album") == null) {
                track.albumProperty().set("Unknown Album");
            }else {
                track.albumProperty().set(track.getMedia().getMetadata().get("album").toString());
            }

        });


        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Delete");
        deleteItem.setOnAction(event -> viewContainer.getChildren().remove(view.getView()));
        menu.getItems().add(deleteItem);

        /**TODO: Write selection logic here.
         * Single Click: select only clicked.
         * Double Click: play only clicked;
         * Single Click on Selected: do nothing;
         * Single Click with Shift Pressed: add clicked to selection;
         * Single Click with Shift Pressed on Selected: remove clicked from selection;
         * Double Click with Shift selected: play all selected;
         *
         * select : move pane to SELECTED_X offset, and change it's id to "selectedNode";
         *
         */
        view.getView().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.isShiftDown()) {

                }

            }
        });
        Label metadata = new Label();
        metadata.setLayoutX(20);
        metadata.setLayoutY(5);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        view.getView().getChildren().add(metadata);
        metadata.textProperty().bind(track.artistProperty());


        metadata = new Label();
        metadata.setLayoutX((VIEW_WIDTH / 100 * 65)); metadata.setLayoutY(10);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.titleProperty());
        view.getView().getChildren().add(metadata);


        metadata = new Label();
        metadata.setLayoutX(100); metadata.setLayoutY(30);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.albumProperty());
        view.getView().getChildren().add(metadata);
        view.getView().applyCss();
        viewContainer.getChildren().add(view.getView());
        views.add(view);

    }

    private void clearSelected()
    {
        for (TrackView sel : selected)
        {
            sel.getTrack().getPlayer().setOnEndOfMedia(() -> sel.getTrack().getPlayer().stop());
            sel.getView().setId("unselectedNode");
        }
        selected.clear();
    }
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
            if (event.getCode() == KeyCode.ESCAPE)
            {
                clearSelected();
            }
        });

        /**
         *  Song slider init
         */
        songSlider.setDisable(true);
        songSlider.setMin(0);
        songSlider.setMax(100);
        songSlider.setValue(0);
        songSlider.valueProperty().addListener(ov -> songBar.setProgress(songSlider.getValue()));




    }

    public void browseClick() {

        FileChooser browser = new FileChooser();
        browser.setTitle("Select file...");
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("Wav (*.wav)", "*.wav");
        browser.getExtensionFilters().add(mp3Filter);
        browser.getExtensionFilters().add(wavFilter);
        List<File> selected = browser.showOpenMultipleDialog(pane.getScene().getWindow());
        if (selected == null) return;

        for(File selectedFile : selected)
        {
            Track selectedTrack = new Track(selectedFile);
            addView(selectedTrack);
        }

    }

    private ChangeListener<Duration> sliderValueUpdater;
    private InvalidationListener songSliderInvalidationListener;
    private InvalidationListener volumeSliderInvalidationListener;

    //TODO: We should separate play function for both playlists and single track;
    private void playClick() {

        if (currentTrack != null) {
            currentTrack.getPlayer().stop();
            disposeCurrent();
        }
        pauseButton.setVisible(true);
        pauseButton.requestFocus();
        playButton.setVisible(false);

        if (selected.size() == 0) return;
        int[] i = {0};
        currentTrack = selected.get(i[0]).getTrack();
        currentTrack.getPlayer().setOnEndOfMedia(() -> {
            currentTrack = selected.get(++i[0]).getTrack();
            currentTrack.play();
        });
        for (int j = 1; j < selected.size() - 1; j++)
        {
            selected.get(j).getTrack().getPlayer().setOnEndOfMedia(() ->
            {
                currentTrack = selected.get(++i[0]).getTrack();
                currentTrack.play();
            });
        }
        /**
         *  TODO: songSlider controller should be defined here as it's different to each track.
         *  TODO: setSlider max value at tracks duration in seconds;
         *  TODO: add Volume control and listener for volumeSlider;
         */
        sliderValueUpdater = (observable, oldValue, newValue) -> {
            //what to do when track's time changing? Adjust slider value;
        };
        songSliderInvalidationListener = observable -> {
            //What to do when user is moving songSlider?
            //currentTrack.getPlayer().seek();
        };
        songSlider.valueProperty().addListener(songSliderInvalidationListener);

        volumeSliderInvalidationListener = observable -> {
            //What to do when volume Slider value were changed?
        };
        volumeSlider.valueProperty().addListener(volumeSliderInvalidationListener);

        currentTrack.getPlayer().currentTimeProperty().addListener(sliderValueUpdater);

        currentTrack.getPlayer().play();


    }

    public void disposeCurrent()
    {
        if (currentTrack != null && sliderValueUpdater != null)
            currentTrack.getPlayer().currentTimeProperty().removeListener(sliderValueUpdater);
        if (currentTrack != null && songSliderInvalidationListener != null)
            songSlider.valueProperty().removeListener(songSliderInvalidationListener);
        if (currentTrack != null && volumeSliderInvalidationListener != null)
            volumeSlider.valueProperty().removeListener(volumeSliderInvalidationListener);
    }


    public void pauseClick() {
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.requestFocus();
        currentTrack.pause();
    }

}
