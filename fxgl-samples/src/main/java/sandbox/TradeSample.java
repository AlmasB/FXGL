/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.trade.TradeItem;
import com.almasb.fxgl.trade.TradeListView;
import com.almasb.fxgl.trade.TradeView;
import com.almasb.fxgl.ui.FXGLTextFlow;
import com.almasb.fxgl.ui.MDIWindow;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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

            TabPane pane = new TabPane();
            //pane.setTranslateX(50);
            //pane.setTranslateY(50);

            Tab tab1 = new Tab("Buy");
            tab1.setContent(new TradeListView(
                    FXCollections.observableArrayList(new TradeItem<>("", "Item name", "Item description", 30, 15, 1))
            ));
            tab1.setClosable(false);

            Tab tab2 = new Tab("Sell");
            tab2.setContent(new TradeListView(
                    FXCollections.observableArrayList(new TradeItem<>("", "Item name2", "Item description", 25, 20, 3))
            ));
            tab2.setClosable(false);

            pane.getTabs().addAll(
                    tab1,
                    tab2
            );


            Text t = getUIFactory().newText("Money: 1000", Color.GOLD, 24.0);
            //t.setTranslateX(450);
            //t.setTranslateY(100);

//            MDIWindow window = new MDIWindow();
//            window.setCanResize(false);
//            window.setCanMinimize(false);
//            window.setCanClose(false);
//            //window.setBackground(null);
//            window.setPrefSize(400, 550);

            var box = new VBox(10, pane, t);
            box.setPadding(new Insets(15));

            var bg = new Rectangle(400, 550, Color.color(0.1, 0, 0, 0.6));
            bg.setArcWidth(25);
            bg.setArcHeight(25);

            var view = new TradeView();

            //window.setContentPane();

            FXGLTextFlow flow = getUIFactory().newTextFlow();
            flow.append(new KeyView(KeyCode.E, Color.RED)).append(" Sell", Color.RED, 27.0);

            flow.setTranslateY(460);

            getContentRoot().getChildren().addAll(view, flow);
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
                    TradeItem<?> item = view.getListView().getSelectionModel().getSelectedItem();

                    view.getListView().getItems().remove(item);

                    getNotificationService().pushNotification("Sold: " + item.getName());
                }
            }, KeyCode.S);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
