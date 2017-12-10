import javafx.fxml.FXML;

public class SettingsController {
    @FXML
    protected void initialize() {
        Settings settings = new Settings();
        settings.setPropertyOne(5);
        settings.setPropertyTwo("kek");
        Settings.save(settings);
    }
}
