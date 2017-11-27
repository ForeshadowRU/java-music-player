import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Track {

    private Media media;
    private File source;
    private MediaPlayer player;


    public Media getMedia() {
        return media;
    }

    public File getSource() {
        return source;
    }

    public MediaPlayer getPlayer()
    {
        return player;
    }

    public void play()
    {
        player.play();

    }
    public void pause()
    {
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)) player.pause();
    }
    public void stop()
    {
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)) player.stop();
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
        player = new MediaPlayer(media);
    }

    @Override
    public String toString() {
        return media.getMetadata().get("artist") + " - " + media.getMetadata().get("title");
    }
}
