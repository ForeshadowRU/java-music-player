import java.util.ArrayList;

public class Playlist {
    public ArrayList<Track> list;

    public void playAll()
    {
        if (list.size() > 0)
        {
            for(int i = 0; i < list.size() - 1; i++)
            {
                final int finalI = i;
                list.get(i).getPlayer().setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        list.get(finalI + 1).play();
                    }
                });
            }
            list.get(0).play();

        }
    }
}
