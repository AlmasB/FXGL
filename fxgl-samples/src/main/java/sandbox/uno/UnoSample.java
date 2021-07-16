/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A simple clone of Uno.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UnoSample extends GameApplication {

    private static final int NUM_BASE_CARDS = 6;

    private Deck deck = Deck.newInfiniteDeck();

    /**
     * The next hand to be played.
     */
    private Hand nextHand;

    private Hand player;
    private Hand enemy;

    private boolean isPlayerTurn = true;

    private Point2D centerCardPosition;
    private Map<Hand, Rectangle2D> cardsLayout = new HashMap<>();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Uno");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // the top-most card placed on the table, init with dummy entity before we can construct cards
        vars.put("currentCard", new Entity());
    }

    @Override
    protected void initGame() {
        centerCardPosition = new Point2D(getAppWidth() / 2 - 100 / 2, getAppHeight() / 2 - 150 / 2);

        getGameWorld().addEntityFactory(new UnoFactory());

        spawn("Background");

        player = new Hand("Player");
        enemy = new Hand("AI");

        var currentCard = spawnCard(deck.drawCard());
        currentCard.setPosition(centerCardPosition);
        set("currentCard", currentCard);

        nextHand = player;

        cardsLayout.put(player, new Rectangle2D(0, getAppHeight() - 150, getAppWidth(), 150));
        cardsLayout.put(enemy, new Rectangle2D(0, 0 - 150, getAppWidth(), 150));

        cardsLayout.forEach((hand, bounds) -> {
            hand.setChangeCallback(() -> {
                hand.cardsProperty().sort(Comparator.comparingInt(e -> {
                    Card card = e.getObject("card");

                    return card.getSuit().ordinal() * 1000 + card.getRank().ordinal();
                }));

                evaluateAndLayout(hand);
            });

            if (hand == player) {
                getWorldProperties().<Entity>addListener("currentCard", (prev, now) -> {
                    evaluateAndLayout(hand);
                });
            }
        });

        for (int i = 0; i < NUM_BASE_CARDS; i++) {
            player.addCard(spawnCard(deck.drawCard()));
            enemy.addCard(spawnCard(deck.drawCard()));
        }
    }

    @Override
    protected void initUI() {
        Text text = getUIFactoryService().newText("", Color.BLACK, 18);
        text.setTranslateY(50);
        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE);
        text.textProperty().addListener((observable, oldValue, newText) -> {
            animationBuilder()
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .duration(Duration.seconds(0.2))
                    .repeat(2)
                    .autoReverse(true)
                    .scale(text)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });
        text.textProperty().bind(Bindings.size(enemy.cardsProperty()).asString("Enemy Cards: %d"));

        getGameScene().addUINode(text);

        Button btn = new Button("Draw Card");
        btn.setTranslateY(getAppHeight() / 2);
        btn.setOnAction(e -> {
            player.addCard(spawnCard(deck.drawCard()));
        });

        getGameScene().addUINode(btn);
    }

    private void evaluateAndLayout(Hand hand) {
        Rectangle2D bounds = cardsLayout.get(hand);

        double sizePerCard = Math.min(bounds.getWidth() / hand.cardsProperty().size(), 120);

        int i = 0;
        for (Entity card : hand.cardsProperty()) {
            card.setX(i++ * sizePerCard + bounds.getMinX());
            card.setY(bounds.getMinY());

            if (canPlay(card) && hand == player) {
                card.translateY(-25);
            }

            card.setZIndex(i + 10);
        }
    }

    private Entity spawnCard(Card card) {
        return getGameWorld().spawn("Card", new SpawnData().put("card", card));
    }

    /**
     * @param cardEntity the card (in player's hand) that player clicked on
     */
    public void playerClickedOnCard(Entity cardEntity) {
        if (!isPlayerTurn)
            return;

        if (canPlay(cardEntity)) {
            isPlayerTurn = false;
            nextHand = enemy;

            playCard(player, cardEntity, () -> {
                aiMove();
            });
        } else {
            playInvalidSelectionAnimation(cardEntity);
        }
    }

    private void playInvalidSelectionAnimation(Entity cardEntity) {
        animationBuilder()
                .duration(Duration.seconds(0.02))
                .repeat(6)
                .autoReverse(true)
                .translate(cardEntity)
                .from(cardEntity.getPosition())
                .to(cardEntity.getPosition().add(4, 0))
                .buildAndPlay();
    }

    private void aiMove() {
        nextHand = player;

        for (Entity card : enemy.cardsProperty()) {
            if (canPlay(card)) {
                playCard(enemy, card, () -> isPlayerTurn = true);
                return;
            }
        }

        // if we reached here, there are no playable cards in enemy's hands
        while (deck.hasCards()) {
            Entity card = spawnCard(deck.drawCard());

            if (canPlay(card)) {
                playCard(enemy, card, () -> isPlayerTurn = true);

                break;
            } else {
                enemy.addCard(card);
            }
        }
    }

    /**
     * Move a valid card from a hand to the center.
     *
     * @param hand the hand from which the card is being played
     * @param cardEntity the card being played
     * @param onFinished action to perform once the card has been played
     */
    private void playCard(Hand hand, Entity cardEntity, Runnable onFinished) {
        Entity currentCard = geto("currentCard");
        currentCard.setZIndex(-1);

        Card card = cardEntity.getObject("card");

        animationBuilder()
                .onFinished(() -> {
                    hand.removeCard(cardEntity);
                    currentCard.addComponent(new ExpireCleanComponent(Duration.seconds(0.5)));

                    set("currentCard", cardEntity);

                    boolean wasSpecialUsed = false;

                    switch (card.getRank()) {
                        case SP_PLUS2: {
                            for (int i = 0; i < 2; i++) {
                                nextHand.addCard(spawnCard(deck.drawCard()));
                            }

                            wasSpecialUsed = true;

                            break;
                        }

                        case SP_PLUS4: {
                            for (int i = 0; i < 4; i++) {
                                nextHand.addCard(spawnCard(deck.drawCard()));
                            }

                            wasSpecialUsed = true;

                            break;
                        }

                        case SP_SKIP: {
                            wasSpecialUsed = true;
                            break;
                        }

                        default:
                            break;
                    }

                    if (hand.cardsProperty().isEmpty()) {
                        getDialogService().showMessageBox("Hand wins: " + hand, getGameController()::exit);
                    } else {

                        if (!wasSpecialUsed) {
                            onFinished.run();
                        } else {
                            if (nextHand == player) {
                                aiMove();
                            } else {
                                isPlayerTurn = true;
                            }
                        }
                    }
                })
                .duration(Duration.seconds(0.35))
                .translate(cardEntity)
                .from(cardEntity.getPosition())
                .to(centerCardPosition)
                .buildAndPlay();
    }

    /**
     * @return if the given card can be played on top the currentCard
     */
    private boolean canPlay(Entity cardEntity) {
        Card card = cardEntity.getObject("card");
        Entity currentCard = geto("currentCard");
        return card.canUseOn(currentCard.getObject("card"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
