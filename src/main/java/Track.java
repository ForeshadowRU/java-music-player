import java.io.File;
import javafx.scene.media.Media;

public class Track {
    private int id;
    private Media media;
    private File source;

    public Media getMedia() {
        return media;
    }

    public File getSource() {
        return source;
    }

    public Track(File source) {
        this.source = source;
        media = new Media(source.toURI().toString());
    }

    @Override
    public String toString() {
        return media.getMetadata().get("artist") + " - " + media.getMetadata().get("title");
    }
}
