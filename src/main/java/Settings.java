import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

//TODO: ADD SOME TESTS
public class Settings {

    public Settings()
    {

    }

    public static Settings load() {
        File settingsFile = new File("settings.xml");
        Settings settings = null;
        try (FileInputStream fis = new FileInputStream(settingsFile)){
            XMLDecoder decoder = new XMLDecoder(fis);
            settings = (Settings) decoder.readObject();
            decoder.close();
            fis.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

        return settings;
    }
    public static void save(Settings settings)
    {
        File settingsFile = new File("jplayer_settings.xml");
        try {
            if (!settingsFile.exists() && settingsFile.createNewFile())
            {
                FileOutputStream fos = new FileOutputStream(settingsFile);
                XMLEncoder encoder = new XMLEncoder(fos);
                encoder.writeObject(settings);
                encoder.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int propertyOne;
    private String propertyTwo;

    public int getPropertyOne() {
        return propertyOne;
    }

    public void setPropertyOne(int propertyOne) {
        this.propertyOne = propertyOne;
    }

    public String getPropertyTwo() {
        return propertyTwo;
    }

    public void setPropertyTwo(String propertyTwo) {
        this.propertyTwo = propertyTwo;
    }
}
