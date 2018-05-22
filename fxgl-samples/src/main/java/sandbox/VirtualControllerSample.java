/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualControllerOverlay;
import com.almasb.fxgl.input.virtual.VirtualControllerStyle;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.beans.binding.Bindings;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class VirtualControllerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(450);
        settings.setHeight(800);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("test") {
            @Override
            protected void onActionBegin() {
                System.out.println("start f");
            }

            @Override
            protected void onAction() {
                System.out.println("f");
            }

            @Override
            protected void onActionEnd() {
                System.out.println("end f");
            }
        }, KeyCode.F, VirtualButton.A);
    }

    @Override
    protected void initUI() {

        Texture t = texture("brick.png");
        t.setTranslateX(getWidth() - 150);
        t.setTranslateY(50);

        VirtualControllerOverlay vcOverlay = new VirtualControllerOverlay(VirtualControllerStyle.XBOX);
        vcOverlay.setTranslateY(600);

        getGameScene().addUINodes(t, vcOverlay);
    }
}
