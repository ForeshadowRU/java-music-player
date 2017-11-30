import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {
    private double VIEW_HEIGHT = 60;
    private double VIEW_WIDTH = 500;
    private double UNSELECTED_X = 50;
    private double UNSELECTED_Y_OFFSET = 10;


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

    private ArrayList<Track> loaded;

    private ArrayList<TrackView> selected;

    private Track currentTrack;

    private void addView(Track track) {

        TrackView view = new TrackView(track);
        view.getView().setPrefSize(VIEW_WIDTH,VIEW_HEIGHT);
        view.getView().setId("libNode");
        view.getView().setLayoutX(UNSELECTED_X);
        if (views.size() == 0)
            view.getView().setLayoutY(10);
        else
            view.getView().setLayoutY(views.size() * VIEW_HEIGHT + views.size() * 10 + 10);

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


        view.getView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    menu.show(view.getView(), Side.BOTTOM, 0, 0);
                } else if (!event.isShiftDown() && event.getButton() == MouseButton.PRIMARY) {
                    for (TrackView view : views) {
                        view.getView().setId("libNode");
                    }
                    selected.clear();
                }
                if (event.getButton() == MouseButton.PRIMARY)
                {
                    if (event.getClickCount() == 2)
                    {
                        selected.clear();
                        selected.add(view);
                        if (currentTrack != null) currentTrack.stop();
                        currentTrack = view.getTrack();
                        playClick();
                    }else
                    if (selected.contains(view))
                    {
                        selected.remove(view);
                        view.getView().setId("libNode");
                    }
                    else {
                        selected.add(view);
                        view.getView().setId("selectedNode");
                    }




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
            sel.getView().setId("libNode");
        }
    }
    @FXML
    protected void initialize() {
        views = new ArrayList<>();
        loaded = new ArrayList<>();
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
            loaded.add(selectedTrack);
        }

    }

    private ChangeListener<Duration> sliderValueUpdater;
    private InvalidationListener songSliderInvalidationListener;
    private InvalidationListener volumeSliderInvalidationListener;


    private void playClick() {
        if (currentTrack != null) currentTrack.getPlayer().stop();
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
        currentTrack.getPlayer().play();


    }

    public void disposeCurrent()
    {
        ///if (currentTrack != null && sliderValueUpdater != null) currentTrack.getPlayer().currentTimeProperty().removeListener(sliderValueUpdater);
        /// if(currentTrack != null && songSliderInvalidationListener != null) songSlider.valueProperty().removeListener(songSliderInvalidationListener);
        ///if (currentTrack != null && volumeSliderInvalidationListener != null) volumeSlider.valueProperty().removeListener(volumeSliderInvalidationListener);
    }


    public void pauseClick() {
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.requestFocus();
        currentTrack.pause();
    }

}
