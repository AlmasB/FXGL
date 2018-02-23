package com.almasb.fxgl.ui;

import javafx.scene.control.ScrollBar;

/**
 * ScrollBar with custom style.
 *
 * @author Krisztián Nagy (Valdar) (okt.valdar@gmail.com)
 */
public class FXGLScrollBar extends ScrollBar{

    public FXGLScrollBar() {
        getStyleClass().setAll("fxgl-scroll-bar");
    }
}
