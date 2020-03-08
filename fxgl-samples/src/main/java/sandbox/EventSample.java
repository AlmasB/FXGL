/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import dev.DeveloperWASDControl;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class EventSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {

        Entity e = entityBuilder()
                .view(new Rectangle(40, 40))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        //eventBuilder().when();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
