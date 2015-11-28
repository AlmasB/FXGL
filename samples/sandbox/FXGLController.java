package sandbox;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.ui.UIController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLController implements UIController {

    @FXML
    private Label labelCount;

    private BasicGameApplication app;

    public FXGLController(BasicGameApplication app) {
        this.app = app;
    }

    @Override
    public void init() {
        labelCount.textProperty().bind(app.countProperty.asString("Count: %d"));
    }
}
