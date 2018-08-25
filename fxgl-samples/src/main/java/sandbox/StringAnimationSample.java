package sandbox;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class StringAnimationSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initUI() {
        Text text = getUIFactory().newText("", Color.BLACK, 18);

        Animation<?> anim = new StringAnimation("Hello world") {
            @Override
            public void onProgress(String value) {
                text.setText(value);
            }
        };

        anim.startInPlayState();

        DSLKt.addUINode(text, 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
