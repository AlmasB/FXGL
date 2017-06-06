/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.app.FXGL;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CardView extends Pane {

    private Card card;

    public CardView(Card card) {
        this.card = card;

        Rectangle bg = new Rectangle(100, 150, card.getSuit().color.brighter());
        bg.setArcWidth(15);
        bg.setArcHeight(15);
        bg.setStrokeWidth(4);
        bg.setStroke(card.getSuit().color.darker());

        Text text = FXGL.getUIFactory().newText(card.toString(), Color.BLACK, 32);
        Text text2 = FXGL.getUIFactory().newText(card.toString(), Color.BLACK, 32);

        text.setTranslateY(32);
        text2.setTranslateX(100 - text2.getLayoutBounds().getWidth());
        text2.setTranslateY(150);

        getChildren().addAll(bg, text, text2);
    }
}
