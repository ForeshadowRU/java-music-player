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



    private Track track;
    private Pane view;


    public TrackView(Track track) {
        this.track = track;
        view = new Pane();
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
