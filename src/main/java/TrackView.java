import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class TrackView {

    private static double VIEW_HEIGHT = 60;
    private static double VIEW_WIDTH = 500;
    private static double UNSELECTED_X = 50;
    private static double UNSELECTED_Y_OFFSET = 10;

    private Track track;
    private Pane view;


    public TrackView(Track track, ArrayList<TrackView> views, Pane container) {
        view = new Pane();
        this.track = track;
        view.setPrefSize(VIEW_WIDTH,VIEW_HEIGHT);
        view.setId("libNode");
        view.setLayoutX(UNSELECTED_X);
        if (views.size() == 0)
            view.setLayoutY(10 + views.size()*VIEW_HEIGHT);
        else
            view.setLayoutY(views.get(views.size() - 1).view.getLayoutY() + 10 + VIEW_HEIGHT*views.size());

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
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                container.getChildren().remove(view);
            }
        });
        menu.getItems().add(deleteItem);


        view.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    menu.show(view, Side.BOTTOM, 0, 0);
                } else if (!event.isShiftDown() && event.getButton() == MouseButton.PRIMARY) {
                    for (TrackView view : views) {
                        view.view.setId("libNode");
                    }
                }
                if (event.getButton() == MouseButton.PRIMARY) view.setId("selectedNode");
            }
        });
        Label metadata = new Label();
        metadata.setLayoutX(20);
        metadata.setLayoutY(5);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        view.getChildren().add(metadata);
        metadata.textProperty().bind(track.artistProperty());


        metadata = new Label();
        metadata.setLayoutX((VIEW_WIDTH / 100 * 65)); metadata.setLayoutY(10);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.titleProperty());
        view.getChildren().add(metadata);


        metadata = new Label();
        metadata.setLayoutX(100); metadata.setLayoutY(30);
        metadata.setId("label");
        metadata.setMaxWidth(200);
        metadata.textProperty().bind(track.albumProperty());
        view.getChildren().add(metadata);
        views.add(this);
        view.applyCss();


    }


    public Track getTrack() {
        return track;
    }

    public Pane getView() {
        return view;
    }

    public void setView(Pane view) {
        this.view = view;
    }


}
