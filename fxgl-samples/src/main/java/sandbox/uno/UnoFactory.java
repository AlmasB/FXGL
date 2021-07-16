/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UnoFactory implements EntityFactory {

    @Spawns("Background")
    public Entity newBackground(SpawnData data) {
        var bg = new Vignette(getAppWidth(), getAppHeight(), 350);
        bg.setIntensity(0.7);
        bg.setColor(Color.DARKGREEN.darker());

        return entityBuilder()
                .view(bg)
                .buildAndAttach();
    }

    @Spawns("Card")
    public Entity newCard(SpawnData data) {
        Card card = data.get("card");

        return entityBuilder(data)
                .view(makeCardView(card))
                .onClick(e -> {
                    FXGL.<UnoSample>getAppCast().playerClickedOnCard(e);
                })
                .build();
    }

    private Node makeCardView(Card card) {
        Rectangle bg = new Rectangle(100, 150, card.getSuit().color.brighter());
        bg.setArcWidth(15);
        bg.setArcHeight(15);
        bg.setStrokeWidth(4);
        bg.setStroke(Color.WHITE);

        Text text = getUIFactoryService().newText(card.toString(), Color.BLACK, 32);
        Text text2 = getUIFactoryService().newText(card.toString(), Color.BLACK, 32);

        text.setTranslateX(5);
        text.setTranslateY(32);
        text2.setTranslateX(90 - text2.getLayoutBounds().getWidth());
        text2.setTranslateY(140);

        var view = new Pane(bg, text, text2);
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);

        return view;
    }
}
