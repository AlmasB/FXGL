/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.WheelMenu;
import javafx.scene.input.MouseButton;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WheelMenuSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("WheelMenuSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open/Close WheelMenu") {
            @Override
            protected void onActionBegin() {
                if (menu.isOpen())
                    menu.close();
                else
                    menu.open();

                menu.setTranslateX(getInput().getMouseXWorld() - 25);
                menu.setTranslateY(getInput().getMouseYWorld() - 75);
            }
        }, MouseButton.SECONDARY);
    }

    private WheelMenu menu;

    @Override
    protected void initUI() {
        menu = new WheelMenu("Hi", "Hello", "World", "FXGL");
        menu.setTranslateX(400);
        menu.setTranslateY(300);
        menu.setSelectionHandler(System.out::println);

        getGameScene().addUINode(menu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
