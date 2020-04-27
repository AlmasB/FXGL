/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.trade.Shop;
import com.almasb.fxgl.trade.ShopListener;
import com.almasb.fxgl.trade.TradeItem;
import com.almasb.fxgl.trade.view.ShopView;
import com.almasb.fxgl.trade.view.TradeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TradeSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(900);
        settings.setHeightFromRatio(16/9.0);
    }

    private TradeSubScene tradeSubScene;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("wood", 500);
        vars.put("stone", 300);
        vars.put("crystals", 100);
        vars.put("money", 1000);
    }

    @Override
    protected void initGame() {
        tradeSubScene = new TradeSubScene();

        onKeyDown(KeyCode.F, "f", () -> {
            getSceneService().pushSubScene(tradeSubScene);
        });

        var textWood = getUIFactoryService().newText(getip("wood").asString("Wood: %d"));
        textWood.setFill(Color.BLACK);
        var textStone = getUIFactoryService().newText(getip("stone").asString("Stone: %d"));
        textStone.setFill(Color.BLACK);
        var textCrystals = getUIFactoryService().newText(getip("crystals").asString("Crystals: %d"));
        textCrystals.setFill(Color.BLACK);
        var textMoney = getUIFactoryService().newText(getip("money").asString("Money: %d"));
        textMoney.setFill(Color.BLACK);

        var vbox = new VBox(5, textWood, textStone, textCrystals, textMoney);

        addUINode(vbox, 10, 10);
    }

    public static class TradeSubScene extends SubScene {
        public TradeSubScene() {

            Entity itemWood = new Entity();
            Entity itemStone = new Entity();
            Entity itemCrystals = new Entity();

            var playerShop = new Shop<Entity>(1000, List.of(
                    new TradeItem<>(itemStone, "Stone", "Item Description", 1, 1, 300),
                    new TradeItem<>(itemCrystals, "Crystals", "Item Description", 1, 1, 100),
                    new TradeItem<>(itemWood, "Wood", "Item Description", 1, 1, 500)
            ));
            playerShop.moneyProperty().bindBidirectional(getip("money"));

            playerShop.setListener(new ShopListener<Entity>() {
                @Override
                public void onSold(TradeItem<Entity> item) {
                    inc(item.getName().toLowerCase(), -item.getQuantity());

                    System.out.println("player sold " + item.getName());
                }

                @Override
                public void onBought(TradeItem<Entity> item) {
                    inc(item.getName().toLowerCase(), item.getQuantity());

                    System.out.println("player bought " + item.getName());
                }
            });


            var npcShop = new Shop<Entity>(10000, List.of(
                    new TradeItem<>(itemStone, "Stone", "Item Description", 1, 1, 300),
                    new TradeItem<>(itemCrystals, "Crystals", "Item Description", 1, 1, 300),
                    new TradeItem<>(itemWood, "Wood", "Item Description", 1, 1, 300)
            ));

            var playerView = new ShopView<>(playerShop, 400, 450);
            var npcView = new ShopView<>(npcShop, 400, 450);

            var view = new TradeView<>(playerView, npcView);

            getContentRoot().getChildren().addAll(view);

            getContentRoot().setTranslateX(300);
            getContentRoot().setTranslateY(0);

            this.getInput().addAction(new UserAction("Close") {
                @Override
                protected void onActionBegin() {
                    getSceneService().popSubScene();
                }
            }, KeyCode.F);

            this.getInput().addAction(new UserAction("Trade") {
                @Override
                protected void onActionBegin() {
                    var tab = view.getTabPane().getSelectionModel().getSelectedItem();

                    ShopView<Entity> shopView = (ShopView<Entity>) tab.getContent();

                    TradeItem<Entity> item = (TradeItem<Entity>) shopView.getListView().getSelectionModel().getSelectedItem();

                    System.out.println("selected item is : " + item);

                    if (item == null)
                        return;

                    if (shopView == playerView) {
                        npcView.getShop().buyFrom(playerShop, item, 1);
                    } else {
                        playerShop.buyFrom(npcShop, item, 1);
                    }

                    npcView.getListView().refresh();
                    playerView.getListView().refresh();
                }
            }, KeyCode.S);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
