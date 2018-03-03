package com.almasb.fxgl.ui;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * ScrollPane with custom style.
 *
 * @author Krisztián Nagy (Valdar) (okt.valdar@gmail.com)
 */
public class FXGLScrollPane extends ScrollPane{

    public FXGLScrollPane() {
        super();
        getStyleClass().setAll("fxgl-scroll-pane");
    }

    public FXGLScrollPane(Node content) {
        this();
        setContent(content);
    }
}
