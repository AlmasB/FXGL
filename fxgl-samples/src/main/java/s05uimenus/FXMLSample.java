/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxgl.ui.UIController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Shows how to use FXML UI within FXGL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXMLSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("FXMLSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    private IntegerProperty count;

    @Override
    protected void initGame() {
        count = new SimpleIntegerProperty(0);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {
        // 1. create a controller class that implements UIController
        GameUIController controller = new GameUIController();

        // 2. place fxml file in "assets/ui" and load it
        UI fxmlUI = getAssetLoader().loadUI("test_ui.fxml", controller);

        // 3. controller instance now has its @FXML fields injected
        controller.getLabelCount().textProperty().bind(count.asString("Count: [%d]"));

        // 4. add UI to game scene
        getGameScene().addUI(fxmlUI);
    }

    @Override
    protected void onUpdate(double tpf) {
        count.set(count.get() + 1);
    }

    public static class GameUIController implements UIController {

        @FXML
        private Label labelCount;

        public Label getLabelCount() {
            return labelCount;
        }

        @Override
        public void init() {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
