/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox.inventory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.inventory.Inventory;
import com.almasb.fxgl.inventory.view.InventoryView;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a simple inventory that shows
 * how you can pick up and drop items.
 *
 * @author Adam Bocco (adambocco) (adam.bocco@gmail.com)
 */
public class InventorySample extends GameApplication {

    private Inventory<CustomItem> inventory;

    private static class CustomItem {
        private String description;

        private CustomItem(String description) {
            this.description = description;
        }
    }

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        inventory = new Inventory<>(10);

        var wood = new CustomItem("Wood");
        var stone = new CustomItem("Stone");
        var crystal = new CustomItem("Crystal");

        var vbox = new VBox(5);

        List.of(wood, stone, crystal)
                .forEach(item -> {
                    var btnAdd = new Button("Pick up " + item.description);
                    var btnRemove = new Button("Drop " + item.description);

                    btnAdd.setOnAction(e -> {
                        var scene = getSceneService().getGameScene();

                        System.out.println(scene);

                        getSceneService().getMainMenuScene().ifPresent(scene2 -> {
                            System.out.println(scene2);
                        });

                        inventory.add(item);

                        System.out.println(inventory.getAllData());
                    });

                    btnRemove.setOnAction(e -> {
                        inventory.incrementQuantity(item, -1);

                        System.out.println(inventory.getAllData());
                    });

                    vbox.getChildren().addAll(btnAdd, btnRemove);
                });

        addUINode(vbox, 100, 100);
        addUINode(new InventoryView<>(inventory), 400, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }

//
//    private class InventorySubScene extends SubScene {
//
//        public Inventory<Entity> playerInventory = new Inventory(10);
//
//        public InventoryView view = new InventoryView<>(playerInventory);
//
//
//        public InventorySubScene() {
//            getContentRoot().getChildren().addAll(view);
//            getContentRoot().setTranslateX(300);
//            getContentRoot().setTranslateY(0);
//
//            Button dropOne = getUIFactoryService().newButton("Drop One");
//            dropOne.prefHeight(30.0);
//            dropOne.prefWidth(135.0);
//            dropOne.setTranslateX(35.0);
//            dropOne.setTranslateY(320.0);
//
//            dropOne.setOnAction(actionEvent -> {
//                var selectedItem = (Entity) view.getListView().getSelectionModel().getSelectedItem();
//
//                if (selectedItem != null) {
//                    var item = inventorySubScene.playerInventory.getData((Entity) selectedItem).get().getUserItem();
//                    playerInventory.incrementQuantity(item, -1);
//                }
//                view.getListView().refresh();
//            });
//
//            Button dropAll = getUIFactoryService().newButton("Drop All");
//            dropAll.prefHeight(30.0);
//            dropAll.prefWidth(135.0);
//            dropAll.setTranslateX(35.0);
//            dropAll.setTranslateY(370.0);
//
//            dropAll.setOnAction(actionEvent -> {
//
//                var selectedItem = (Entity) view.getListView().getSelectionModel().getSelectedItem();
//
//                if (selectedItem != null) {
//                    var itemData = inventorySubScene.playerInventory.getData((Entity) selectedItem).get().getUserItem();
//                    playerInventory.remove(selectedItem);
//                }
//                view.getListView().refresh();
//            });
//
//            this.getContentRoot().getChildren().addAll(dropOne, dropAll);
//        }
//    }
}