package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.application.Application;
import javafx.stage.Stage;

public class FXApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        GameApplication.customLaunch(new MyGame(), stage);
    }

    public static class Launcher {
        public static void main(String[] args) {
            Application.launch(FXApp.class, args);
        }
    }

    public class MyGame extends GameApplication {

        @Override
        protected void initSettings(GameSettings settings) {
            settings.setTitle("Hello From Custom");
        }
    }
}
