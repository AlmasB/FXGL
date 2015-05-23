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
}
