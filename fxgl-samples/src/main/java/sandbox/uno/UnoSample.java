/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox.uno;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UnoSample extends GameApplication {

    private static final int BASE_CARDS = 6;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("UnoSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    private Deck deck = new InfiniteDeck();

    private GameEntity currentCard;
    private Hand currentHand;
    private Hand nextHand;

    private Hand player;
    private Hand enemy;

    private boolean playerHandChanged = false;

    @Override
    protected void initGame() {
        player = new Hand();
        enemy = new Hand();

        for (int i = 0; i < BASE_CARDS; i++) {
            Card card = deck.drawCard();

            GameEntity cardEntity = spawn(card, i*130, 450);

            player.addCard(cardEntity);
            //enemy.addCard(deck.drawCard());
        }

        currentCard = spawn(deck.drawCard(), 400 - 50, 300 - 75);
        currentHand = player;
        nextHand = enemy;
    }

    @Override
    protected void initUI() {
//        getGameScene().addUINode(new HandView(enemy));
//
//        HandView playerView = new HandView(player);
//        playerView.setTranslateY(500);
//
//        getGameScene().addUINode(playerView);

        Button btn = new Button("Draw Card");
        btn.setTranslateY(getHeight() / 2);
        btn.setOnAction(e -> {
            player.addCard(spawn(deck.drawCard(), 0, 0));

            playerHandChanged = true;
        });

        getGameScene().addUINode(btn);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (playerHandChanged) {

            double sizePerCard = 1.0 * getWidth() / player.cardsProperty().size();

            int i = 0;
            for (GameEntity card : player.cardsProperty()) {
                card.setX(i++ * sizePerCard);
                card.setY(450);
            }

            playerHandChanged = false;
        }
    }

    private GameEntity spawn(Card card, int x, int y) {
        GameEntity cardEntity = (GameEntity) getGameWorld().spawn("Card", new SpawnData(x, y).put("card", card));

        cardEntity.getView().setOnMouseClicked(e -> {
            if (card.canUseOn(currentCard.getComponentUnsafe(CardComponent.class).getValue())) {
                Animation<?> animation = Entities.animationBuilder()
                        .duration(Duration.seconds(0.35))
                        .translate(cardEntity)
                        .from(cardEntity.getPosition())
                        .to(new Point2D(350, 225))
                        .build();

                animation.setOnFinished(() -> {

                    player.removeCard(cardEntity);
                    playerHandChanged = true;

                    // AI move

                    currentCard.addControl(new ExpireCleanControl(Duration.seconds(0.5)));

                    currentCard = cardEntity;
                    currentCard.setRenderLayer(RenderLayer.BACKGROUND);

                    // apply special effect
                    switch (card.getRank()) {
                        case SP_PLUS2:
                            break;

                        case SP_PLUS4:
                            break;

                        case SP_SKIP:
                            break;

                        default:
                    }
                });
                animation.startInPlayState();
            }
        });

        return cardEntity;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
