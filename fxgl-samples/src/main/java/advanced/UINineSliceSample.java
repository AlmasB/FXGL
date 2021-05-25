/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.NineSliceTextureBuilder;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use 9-slice UI builder.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UINineSliceSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Nice Slice Sampler");
        settings.setVersion("1.0");
        settings.setWidth(800);
        settings.setHeight(800);
    }

    private final Logger log = Logger.get(UINineSliceSample.class);
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

        Button generateBtn = new Button("Generate");
        generateBtn.setOnAction(e -> {
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

        Button exportBtn = new Button("Export");
        exportBtn.setOnAction(e -> {
            if (texture != null) {
                var fileChooser = createImageSaverDialog();

                var file = fileChooser.showSaveDialog(getGameScene().getRoot().getScene().getWindow());
                if (file != null) {
                    saveImageToFile(texture.getImage(), file);
                }
            }
        });

        getGameScene().addUINodes(new VBox(5, fieldW, fieldH, generateBtn, exportBtn));

        generateBtn.fire();
    }

    private FileChooser createImageSaverDialog() {
        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName("SliceSample.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
        return fileChooser;
    }

    private void saveImageToFile(Image image, File file) {
        try {
            var x = ImagesKt.toBufferedImage(image);
            ImageIO.write(x,"png", file);
            log.info("Saved image to '" + file.getAbsolutePath() + "'.");
        } catch (IOException ex) {
            log.warning("Failed to save image to '" + file.getAbsolutePath() + "'.", ex);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
