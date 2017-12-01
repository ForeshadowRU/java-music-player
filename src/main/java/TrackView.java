import javafx.scene.layout.Pane;

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

}
