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
        settings.setHeight(800);
        settings.setTitle("ImageSample");
    }

    @Override
    protected void initGame() {
        // Image obtained from internet.
        Image background = new Image("https://www.empiraft.com/zh_cn/new/images/background.png");
        Image backgroundResized = ImagesKt.resize(background, 400, 200);
        FXGL.entityBuilder()
                .at(400, 0)
                .view(new ImageView(backgroundResized))
                .buildAndAttach();

        // Image obtained from internet.
        Image original = new Image("https://www.empiraft.com/zh_cn/new/images/icon-game.png");
        // Demo to resize image, note: original image from url ("https://www.empiraft.com/zh_cn/new/images/icon-game.png") is 64px * 64px
        for (int i = 16; i > 0; i--) {
            Image processed = ImagesKt.resize(original, 16 * i, 16 * i);
            FXGL.entityBuilder()
                    .at(5 * i  * i, 0.5 * i * i * i)
                    .view(new ImageView(processed))
                    .buildAndAttach();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
