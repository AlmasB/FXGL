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
import com.almasb.fxgl.inventory.Inventory;
import com.almasb.fxgl.inventory.ItemData;
import com.almasb.fxgl.inventory.view.InventoryView;
import com.almasb.fxgl.scene.SubScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a simple inventory that shows
 * how you can pickup and drop items.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InventorySample extends GameApplication {

    private InventorySubScene inventorySubScene;

    public Button pickupWood;
    public Button pickupStone;
    public Button pickupCrystal;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.getCSSList().add("test_fxgl_light.css");
        settings.setWidth(900);
        settings.setHeightFromRatio(16/9.0);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("wood", 5);
        vars.put("stone", 3);
        vars.put("crystal", 1);
    }

    @Override
    protected void initGame() {
        inventorySubScene = new InventorySubScene();

        onKeyDown(KeyCode.F, "f", () -> {
            getSceneService().pushSubScene(inventorySubScene);
        });

        pickupWood = getUIFactoryService().newButton(getip("wood").asString("Wood: %d"));
        pickupStone = getUIFactoryService().newButton(getip("stone").asString("Stone: %d"));
        pickupCrystal = getUIFactoryService().newButton(getip("crystal").asString("Crystal: %d"));
        pickupWood.setStyle("-fx-text-fill: black; -fx-background-color: saddlebrown;");
        pickupStone.setStyle("-fx-text-fill: black; -fx-background-color: grey");
        pickupCrystal.setStyle("-fx-text-fill: black; -fx-background-color: aqua");


        pickupWood.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var inventoryItem = inventorySubScene.itemWood;
                pickupItem(inventoryItem, "wood");
            }
        });

        pickupStone.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var inventoryItem = inventorySubScene.itemStone;
                pickupItem(inventoryItem, "stone");
            }
        });

        pickupCrystal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var inventoryItem = inventorySubScene.itemCrystal;
                pickupItem(inventoryItem, "crystal");
            }
        });

        var vbox = new VBox(5, pickupWood, pickupStone, pickupCrystal);

        addUINode(vbox, 10, 10);

    }

    public <T> void pickupItem(ItemData<T> inventoryItem, String varKey) {
        if (getip(varKey).get() > 0) {
            inventorySubScene.playerInventory.add(inventoryItem);
            inventoryItem.setQuantity(inventoryItem.getQuantity() + 1);
            inc(inventoryItem.getName().toLowerCase(), -1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    public class InventorySubScene extends SubScene {
        public InventoryView view;

        public ItemData<Entity> itemWood;
        public ItemData<Entity> itemStone;
        public ItemData<Entity> itemCrystal;
        public Inventory playerInventory;

        public InventorySubScene() {

            Entity entityWood = new Entity();
            Entity entityStone = new Entity();
            Entity entityCrystal = new Entity();

            playerInventory = new Inventory<ItemData<Entity>>(10);
            itemWood = new ItemData(entityWood);
            itemWood.setName("Wood");
            itemWood.setDescription("Item Description");
            itemWood.setQuantity(5);
            playerInventory.add(itemWood);

            itemStone = new ItemData(entityStone);
            itemStone.setName("Stone");
            itemStone.setDescription("Item Description");
            itemStone.setQuantity(10);
            playerInventory.add(itemStone);

            itemCrystal = new ItemData(entityCrystal);
            itemCrystal.setName("Crystal");
            itemCrystal.setDescription("Item Description");
            itemCrystal.setQuantity(20);
            playerInventory.add(itemCrystal);

            view = new InventoryView<>(playerInventory, 300, 400);
            getContentRoot().getChildren().addAll(view);

            getContentRoot().setTranslateX(300);
            getContentRoot().setTranslateY(0);


            this.getInput().addAction(new UserAction("Close") {
                @Override
                protected void onActionBegin() {
                    getSceneService().popSubScene();
                    view.getListView().refresh();
                }
            }, KeyCode.F);
            Button dropOne = getUIFactoryService().newButton("Drop One");
            dropOne.prefHeight(30.0);
            dropOne.prefWidth(135.0);
            dropOne.setTranslateX(50.0);
            dropOne.setTranslateY(300.0);
            dropOne.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    var selectedItem = view.getListView().getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        if (selectedItem.getQuantity() > 1) {
                            selectedItem.setQuantity(selectedItem.getQuantity()-1);
                        }
                        else {
                            playerInventory.remove(selectedItem);
                        }
                    }
                    view.getListView().refresh();
                }
            });
            Button dropAll = getUIFactoryService().newButton("Drop All");
            dropAll.prefHeight(30.0);
            dropAll.prefWidth(135.0);
            dropAll.setTranslateX(50.0);
            dropAll.setTranslateY(350.0);
            dropAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    var selectedItem = view.getListView().getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        selectedItem.setQuantity(0);
                        playerInventory.remove(selectedItem);
                    }
                    view.getListView().refresh();
                }
            });
            view.getChildren().addAll(dropOne, dropAll);
        }
    }
}