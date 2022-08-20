/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * An example that shows a scrolling background and a resizable window.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScrollingBackgroundSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, () -> player.translateX(-10));
        onKey(KeyCode.D, () -> player.translateX(10));

        onKeyDown(KeyCode.F, () -> {
            getGameScene().getViewport().shakeTranslational(5);
        });
    }

    @Override
    protected void initGame() {
        initPadding();

        player = entityBuilder()
                .buildAndAttach();

        getGameScene().getViewport().bindToEntity(player, 0, 0);

        // try enabling one of these to see how they affect the viewport
        //getGameScene().getViewport().setLazy(true);
        //getGameScene().getViewport().setFloating(true);

        var scrollView = new ScrollingBackgroundView(
                image("bg_wrap.png", 1066, 800),
                1066, 800,
                Orientation.HORIZONTAL
        );

        entityBuilder()
                .view(scrollView)
                .buildAndAttach();
    }

    private void initPadding() {
        getGameScene().getPaddingTop().setFill(Color.YELLOW);
        getGameScene().getPaddingBot().setFill(Color.RED);
        getGameScene().getPaddingRight().setFill(Color.GREEN);
        getGameScene().getPaddingLeft().setFill(Color.BLUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
