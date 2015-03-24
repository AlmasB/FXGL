package com.almasb.fxgl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.almasb.fxgl.entity.Texture;

import javafx.scene.image.Image;

public class AssetManager {

    private static final String ASSETS_DIR = "/assets/";
    private static final String TEXTURES_DIR = ASSETS_DIR + "textures/";

    private static final Logger log = FXGLLogger.getLogger("AssetManager");

    public Texture loadTexture(String name) {
        Texture texture = new Texture();

        try (InputStream is = getClass().getResourceAsStream(TEXTURES_DIR + name)) {
            if (is != null) {
                Image image = new Image(is);
                texture.setImage(image);
            }
            else {
                log.warning("Failed to load texture: " + name + " Check it exists in assets/textures/");
            }
        }
        catch (IOException e) {
            log.warning("Failed to load texture: " + name);
            FXGLLogger.trace(e);
        }

        return texture;
    }
}
