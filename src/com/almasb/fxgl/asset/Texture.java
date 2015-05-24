package com.almasb.fxgl.asset;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Texture extends ImageView {

    /*package-private*/ Texture(Image image) {
        super(image);
    }

    public StaticAnimatedTexture toStaticAnimatedTexture(int frames, double duration) {
        StaticAnimatedTexture texture = new StaticAnimatedTexture(getImage(), frames, duration);
        return texture;
    }

    /**
     * Call this to create a new texture if you are
     * planning to use the same image as graphics
     * for multiple entities. This is required because
     * same Node can only have 1 parent
     *
     * @return new Texture with same image
     */
    public Texture copy() {
        return new Texture(getImage());
    }
}
