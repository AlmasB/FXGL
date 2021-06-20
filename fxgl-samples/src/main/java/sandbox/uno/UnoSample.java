/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A simple clone of Uno.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UnoSample extends GameApplication {

    private static final int NUM_BASE_CARDS = 6;

    private Point2D centerCardPosition;

    private Deck deck = new InfiniteDeck();

    /**
     * The top-most card placed on the table.
     */
    private Entity currentCard;

    private Hand player;
    private Hand enemy;

    private boolean playerHandChanged = false;
    private boolean isPlayerTurn = true;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("UnoSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        centerCardPosition = new Point2D(getAppWidth() / 2 - 100 / 2, getAppHeight() / 2 - 150 / 2);

        getGameWorld().addEntityFactory(new UnoFactory());

        spawn("Background");

        player = new Hand("Player");
        enemy = new Hand("AI");

        for (int i = 0; i < NUM_BASE_CARDS; i++) {
            player.addCard(spawnCard(deck.drawCard(), i*130, getAppHeight() - 150));
            enemy.addCard(spawnCard(deck.drawCard(), 0, -150));
        }

        currentCard = spawnCard(deck.drawCard(), getAppWidth() / 2 - 100 / 2, getAppHeight() / 2 - 150 / 2);
    }

    @Override
    protected void initUI() {
        Text text = getUIFactoryService().newText("", Color.BLACK, 18);
        text.setTranslateY(50);
        text.textProperty().bind(Bindings.size(enemy.cardsProperty()).asString("Enemy Cards: %d"));

        getGameScene().addUINode(text);

        Button btn = new Button("Draw Card");
        btn.setTranslateY(getAppHeight() / 2);
        btn.setOnAction(e -> {
            player.addCard(spawnCard(deck.drawCard(), 0, 0));

            playerHandChanged = true;
        });

        getGameScene().addUINode(btn);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (playerHandChanged) {

            double sizePerCard = Math.min(1.0 * getAppWidth() / player.cardsProperty().size(), 120);

            int i = 0;
            for (Entity card : player.cardsProperty()) {
                card.setX(i++ * sizePerCard);
                card.setY(getAppHeight() - 150);
            }

            playerHandChanged = false;
        }
    }

    private Entity spawnCard(Card card, int x, int y) {
        return getGameWorld().spawn("Card", new SpawnData(x, y).put("card", card));
    }

    /**
     * @param cardEntity the card (in player's hand) that player clicked on
     */
    public void playerClickedOnCard(Entity cardEntity) {
        if (!isPlayerTurn)
            return;

        Card card = cardEntity.getObject("card");

        if (card.canUseOn(currentCard.getComponent(CardComponent.class).getValue())) {
            isPlayerTurn = false;

            playCard(player, cardEntity, () -> {
                playerHandChanged = true;
                aiMove();
            });
        } else {
            animationBuilder()
                    .duration(Duration.seconds(0.02))
                    .repeat(6)
                    .autoReverse(true)
                    .translate(cardEntity)
                    .from(cardEntity.getPosition())
                    .to(cardEntity.getPosition().add(4, 0))
                    .buildAndPlay();
        }
    }

    private void aiMove() {
        for (Entity card : enemy.cardsProperty()) {
            if (canPlay(card)) {
                playCard(enemy, card, () -> isPlayerTurn = true);
                return;
            }
        }

        // if we reached here, there are no playable cards in enemy's hands
        while (deck.hasCards()) {
            Entity card = spawnCard(deck.drawCard(), 0, -150);

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
        currentCard.setZIndex(-1);

        Card card = cardEntity.getObject("card");

        animationBuilder()
                .onFinished(() -> {
                    hand.removeCard(cardEntity);
                    currentCard.addComponent(new ExpireCleanComponent(Duration.seconds(0.5)));

                    currentCard = cardEntity;

                    // TODO: apply special effect
                    switch (card.getRank()) {
                        case SP_PLUS2:
                            break;

                        case SP_PLUS4:
                            break;

                        case SP_SKIP:
                            break;

                        default:
                    }

                    if (hand.cardsProperty().isEmpty()) {
                        getDialogService().showMessageBox("Hand wins: " + hand, getGameController()::exit);
                    } else {
                        onFinished.run();
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
    private boolean canPlay(Entity card) {
        return card.getComponent(CardComponent.class)
                .getValue()
                .canUseOn(currentCard.getComponent(CardComponent.class).getValue());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
