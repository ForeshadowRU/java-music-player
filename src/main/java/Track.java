import java.io.File;
import javafx.scene.media.Media;

public class Track {

    private Media media;
    private File source;

    public Media getMedia() {
        return media;
    }

    public File getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;


        return media.equals(track.media) && source.equals(track.source);
    }

    @Override
    public int hashCode() {
        int result = media.hashCode();
        result = 31 * result + source.hashCode();
        return result;
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
