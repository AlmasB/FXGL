/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ScaleTest extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2300);
        settings.setHeight(900);

        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Click") {
            @Override
            protected void onActionBegin() {
                System.out.println(getInput().getMousePositionWorld() + " in world");
                System.out.println(getInput().getMousePositionUI() + " in UI");
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(0, 0)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach();

        Entities.builder()
                .at(getWidth()-40, 0)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach();

        Entities.builder()
                .at(getWidth()-40, getHeight()-40)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach();

        Entities.builder()
                .at(0, getHeight()-40)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
