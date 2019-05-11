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
import com.almasb.fxgl.trade.TradeItem;
import com.almasb.fxgl.trade.ShopView;
import com.almasb.fxgl.trade.TradeView;
import javafx.scene.input.KeyCode;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TradeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1066);
        settings.setHeight(600);
    }

    private TradeSubScene tradeSubScene;

    @Override
    protected void initGame() {
        tradeSubScene = new TradeSubScene();

        onKeyDown(KeyCode.F, "f", () -> {
            getGameController().pushSubScene(tradeSubScene);
        });
    }

    public static class TradeSubScene extends SubScene {
        public TradeSubScene() {

            Entity item1 = new Entity();
            Entity item2 = new Entity();
            Entity item3 = new Entity();

            var playerShop = new Shop<Entity>(1000, List.of(
                    new TradeItem<>(item1, "10mm Pistol", "Item Description", 30, 65, 3),
                    new TradeItem<>(item2, "Longsword", "Item Description", 30, 35, 1)
            ));

            var npcShop = new Shop<Entity>(2000, List.of(
                    new TradeItem<>(item3, "10mm Pistol", "Item Description", 30, 165, 3)
            ));

            var playerView = new ShopView<>(playerShop, 400, 450);
            var npcView = new ShopView<>(npcShop, 400, 450);

            var view = new TradeView<>(playerView, npcView);

            getContentRoot().getChildren().addAll(view);
            getContentRoot().setTranslateX(100);
            getContentRoot().setTranslateY(50);

            this.getInput().addAction(new UserAction("Close") {
                @Override
                protected void onActionBegin() {
                    getGameController().popSubScene();
                }
            }, KeyCode.F);

            this.getInput().addAction(new UserAction("Trade") {
                @Override
                protected void onActionBegin() {
                    var tab = view.getTabPane().getSelectionModel().getSelectedItem();

                    ShopView<Entity> shopView = (ShopView<Entity>) tab.getContent();

                    TradeItem<Entity> item = (TradeItem<Entity>) shopView.getListView().getSelectionModel().getSelectedItem();

                    System.out.println("Item is : " + item);

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
