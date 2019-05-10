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
import javafx.scene.control.TabPane;
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

            //TabPane pane = new TabPane();

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






            //Text t = getUIFactory().newText("Money: 1000", Color.GOLD, 24.0);
            //t.setTranslateX(450);
            //t.setTranslateY(100);


//            var box = new VBox(10, pane, t);
//            box.setPadding(new Insets(15));
//
//            var bg = new Rectangle(400, 550, Color.color(0.1, 0, 0, 0.6));
//            bg.setArcWidth(25);
//            bg.setArcHeight(25);




//            FXGLTextFlow flow = getUIFactory().newTextFlow();
//            flow.append(new KeyView(KeyCode.E, Color.RED)).append(" Sell", Color.RED, 27.0);
//
//            flow.setTranslateY(460);

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

            this.getInput().addAction(new UserAction("Sell") {
                @Override
                protected void onActionBegin() {
                    TradeItem<?> item = playerView.getListView().getSelectionModel().getSelectedItem();

                    if (item == null)
                        return;

                    playerView.getListView().getItems().remove(item);

                    getNotificationService().pushNotification("Sold: " + item.getName());
                }
            }, KeyCode.ENTER);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
