package manual;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.ApplicationMode;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

public class SceneScaleTest extends GameApplication {

    private enum Type implements EntityType {
        TEST
    }

    private Entity player, enemy;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2560);
        settings.setHeight(1440);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(true);
        settings.setIntroEnabled(true);
        settings.setMenuEnabled(true);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        InputManager input = getInputManager();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                player.translate(-40, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                player.translate(40, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("XY Test") {
            @Override
            protected void onActionBegin() {
                UIFactory.getDialogBox().showMessageBox(input.getMouse().x + "," + input.getMouse().y);
//                log.info(input.getMouse().x + " " + input.getMouse().y);
//                log.info(input.getMouse().screenX + " " + input.getMouse().screenY);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() throws Exception {}

    @Override
    protected void initGame() {
        createEntity(0, 0);
        createEntity(0, getHeight() - 40);
        createEntity(getWidth() - 40, 0);
        createEntity(getWidth() - 40, getHeight() - 40);
        player = createEntity(getWidth() / 2, getHeight() / 2);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate() {}

    private Entity createEntity(double x, double y) {
        Entity e = new Entity(Type.TEST);
        e.setPosition(x, y);
        e.setSceneView(new Rectangle(40, 40));

        getGameWorld().addEntity(e);
        return e;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
