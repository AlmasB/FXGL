/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.NineSliceTextureBuilder;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UINineSliceSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(800);
    }

    private NineSliceTextureBuilder builder;
    private Texture texture;

    @Override
    protected void initUI() {
        boolean fromSingleImage = true;

        if (fromSingleImage) {
            builder = new NineSliceTextureBuilder(texture("9SliceSprite.jpg"))
                    .topLeft(new Rectangle2D(0, 0, 53, 53))
                    .top(new Rectangle2D(53, 0, 420, 53))
                    .topRight(new Rectangle2D(473, 0, 52, 53))

                    .left(new Rectangle2D(0, 52, 53, 420))
                    .center(new Rectangle2D(53, 52, 420, 420))
                    .right(new Rectangle2D(473, 52, 52, 420))

                    .botLeft(new Rectangle2D(0, 473, 53, 52))
                    .botRight(new Rectangle2D(473, 473, 52, 52))
                    .bot(new Rectangle2D(53, 473, 473-53, 52));
        } else {
            builder = new NineSliceTextureBuilder(texture("ui_sheet.png"))
                    .topLeft(100, 100, 35, 35)
                    .topRight(165, 100, 35, 35)
                    .botLeft(100, 165, 35, 35)
                    .botRight(165, 165, 35, 35)
                    .center(135, 135, 30, 30)

                    .top(135, 100, 30, 35)
                    .bot(135, 165, 30, 35)
                    .left(100, 135, 35, 30)
                    .right(165, 135, 35, 30);
        }

        TextField fieldW = new TextField();
        fieldW.setText("300");
        fieldW.setPrefWidth(100);
        TextField fieldH = new TextField();
        fieldH.setText("300");
        fieldH.setPrefWidth(100);

        Button btn = new Button("Generate");
        btn.setOnAction(e -> {
            if (texture != null) {
                getGameScene().removeUINode(texture);
                texture.dispose();
            }

            try {
                double w = Double.parseDouble(fieldW.getText());
                double h = Double.parseDouble(fieldH.getText());

                texture = builder.build((int)w, (int)h);
                texture.setTranslateX(100);
                getGameScene().addUINode(texture);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        getGameScene().addUINodes(new VBox(5, fieldW, fieldH, btn));

        btn.fire();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
