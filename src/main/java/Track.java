import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Track {

    private Media media;
    private File source;
    private MediaPlayer player;

    private SimpleStringProperty artist;
    private SimpleStringProperty title;
    private SimpleStringProperty album;

    public String getArtist() {
        return artist.get();
    }

    SimpleStringProperty artistProperty() {
        return artist;
    }

    public String getTitle() {
        return title.get();
    }

    SimpleStringProperty titleProperty() {
        return title;
    }

    public String getAlbum() {
        return album.get();
    }

    SimpleStringProperty albumProperty() {
        return album;
    }

    Media getMedia() {
        return media;
    }

    public File getSource() {
        return source;
    }

    MediaPlayer getPlayer()
    {
        return player;
    }

    void play()
    {
        player.play();

    }

    void pause()
    {
        player.pause();
    }
    public void stop()
    {
        player.stop();
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

        artist = new SimpleStringProperty();
        album = new SimpleStringProperty();
        title = new SimpleStringProperty();
        this.source = source;
        media = new Media(source.toURI().toString());
        player = new MediaPlayer(media);
    }

    @Override
    public String toString() {
        return media.getMetadata().get("artist") + " - " + media.getMetadata().get("title");
    }
}
