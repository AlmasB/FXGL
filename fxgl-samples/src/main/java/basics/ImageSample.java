package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ImageSample");
    }

    @Override
    protected void initGame() {
        Image original = new Image("https://www.empiraft.com/zh_cn/new/images/background.png");
        Image processed = ImagesKt.resize(original, 300, 200);
        FXGL.debug(processed.getWidth() + "-" + processed.getHeight());
        FXGL.entityBuilder()
                .at(0, 0)
                .view(new ImageView(processed))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
