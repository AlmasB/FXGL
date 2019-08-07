/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualControllerOverlay;
import com.almasb.fxgl.input.virtual.VirtualControllerStyle;
import com.almasb.fxgl.input.virtual.VirtualPauseButtonOverlay;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class VirtualControllerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(450);
        settings.setHeight(800);
        settings.setMenuEnabled(false);
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
//        VirtualPauseButtonOverlay btn = new VirtualPauseButtonOverlay();
//        btn.setTranslateX(getAppWidth() - 100);
//        btn.setTranslateY(50);

        VirtualControllerOverlay vcOverlay = new VirtualControllerOverlay(getInput(), VirtualControllerStyle.XBOX);

        Node dpad = vcOverlay.getDpad();
        Node buttons = vcOverlay.getButtons();

        //Node dpad = vcOverlay.getButtons();
        //vcOverlay.setTranslateY(600);

        addUINode(dpad, 0, 300);
        addUINode(buttons, 250, 300);

        addUINode(new VirtualPauseButtonOverlay(getInput(), getSettings().getMenuKey(), getSettings().isMenuEnabled()), 100, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
