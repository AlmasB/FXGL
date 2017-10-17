/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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

    private Entity currentCard;
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

            Entity cardEntity = spawn(card, i*130, 450);

            player.addCard(cardEntity);
            enemy.addCard(spawn(deck.drawCard(), 0, -150));
        }

        currentCard = spawn(deck.drawCard(), 400 - 50, 300 - 75);
        currentHand = player;
        nextHand = enemy;
    }

    @Override
    protected void initUI() {

        Text text = getUIFactory().newText("", Color.BLACK, 18);
        text.setTranslateY(50);
        text.textProperty().bind(Bindings.size(enemy.cardsProperty()).asString("Enemy Cards: %d"));

        getGameScene().addUINode(text);

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
            for (Entity card : player.cardsProperty()) {
                card.setX(i++ * sizePerCard);
                card.setY(450);
            }

            playerHandChanged = false;
        }
    }

    private Entity spawn(Card card, int x, int y) {
        Entity cardEntity = (Entity) getGameWorld().spawn("Card", new SpawnData(x, y).put("card", card));

        cardEntity.getView().setOnMouseClicked(e -> {
            if (card.canUseOn(currentCard.getComponent(CardComponent.class).getValue())) {
                Animation<?> animation = Entities.animationBuilder()
                        .duration(Duration.seconds(0.35))
                        .translate(cardEntity)
                        .from(cardEntity.getPosition())
                        .to(new Point2D(350, 225))
                        .build();

                animation.setOnFinished(() -> {

                    player.removeCard(cardEntity);
                    playerHandChanged = true;

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

                    aiMove();
                });
                animation.startInPlayState();
            }
        });

        return cardEntity;
    }

    private void aiMove() {

        Entity chosenCard = null;

        for (Entity card : enemy.cardsProperty()) {

            // can we avoid calls like this?
            if (card.getComponent(CardComponent.class).getValue().canUseOn(currentCard.getComponent(CardComponent.class).getValue())) {
                currentCard.addControl(new ExpireCleanControl(Duration.seconds(0.5)));

                enemy.removeCard(card);
                card.setPosition(new Point2D(350, 225));

                currentCard = card;
                currentCard.setRenderLayer(RenderLayer.BACKGROUND);

                // apply special effect

                chosenCard = card;
                break;
            }
        }

        if (chosenCard == null) {
            while (deck.hasCards()) {
                Entity draw = spawn(deck.drawCard(), 0, -150);

                if (draw.getComponent(CardComponent.class).getValue().canUseOn(currentCard.getComponent(CardComponent.class).getValue())) {
                    currentCard.addControl(new ExpireCleanControl(Duration.seconds(0.5)));

                    draw.setPosition(new Point2D(350, 225));

                    currentCard = draw;
                    currentCard.setRenderLayer(RenderLayer.BACKGROUND);

                    // apply special effect


                    break;
                } else {
                    enemy.addCard(draw);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
