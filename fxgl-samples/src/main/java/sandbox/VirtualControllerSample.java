/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualControllerOverlay;
import com.almasb.fxgl.input.virtual.VirtualControllerStyle;
import com.almasb.fxgl.input.virtual.VirtualPauseButtonOverlay;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

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
        VirtualPauseButtonOverlay btn = new VirtualPauseButtonOverlay();
        btn.setTranslateX(getWidth() - 100);
        btn.setTranslateY(50);

        VirtualControllerOverlay vcOverlay = new VirtualControllerOverlay(VirtualControllerStyle.XBOX);

        //Node dpad = vcOverlay.getButtons();
        vcOverlay.setTranslateY(600);

        getGameScene().addUINodes(btn, vcOverlay);
    }
}
