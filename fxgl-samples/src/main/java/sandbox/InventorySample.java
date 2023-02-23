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
import com.almasb.fxgl.inventory.view.InventoryView;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a simple inventory that shows
 * how you can pickup and drop items.
 *
 * @author Adam Bocco (adambocco) (adam.bocco@gmail.com)
 */
public class InventorySample extends GameApplication {

    private InventorySubScene inventorySubScene;

    public Entity woodEntity = new Entity();
    public Entity stoneEntity = new Entity();
    public Entity crystalEntity = new Entity();


    @Override
    protected void initSettings(GameSettings settings) {

        settings.setWidth(900);
        settings.setHeightFromRatio(16/9.0);
        settings.setTitle("Inventory Sample");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {

        vars.put("wood", 5);
        vars.put("stone", 3);
        vars.put("crystal", 1);
    }

    @Override
    protected void initUI() {

        var pickupWood = getUIFactoryService().newButton(getip("wood").asString("Wood: %d"));
        pickupWood.setStyle("-fx-background-color: saddlebrown");
        var pickupStone = getUIFactoryService().newButton(getip("stone").asString("Stone: %d"));
        pickupStone.setStyle("-fx-background-color: grey");
        var pickupCrystal = getUIFactoryService().newButton(getip("crystal").asString("Crystal: %d"));
        pickupCrystal.setStyle("-fx-background-color: aqua");

        pickupWood.setOnAction(actionEvent -> pickupItem(woodEntity, "Wood", "Wood description", 1));

        pickupStone.setOnAction(actionEvent -> pickupItem(stoneEntity, "Stone", "Stone description", 1));

        pickupCrystal.setOnAction(actionEvent -> pickupItem(crystalEntity, "Crystal", "Crystal description", 1));

        var vbox = new VBox(5, pickupWood, pickupStone, pickupCrystal);

        addUINode(vbox, 10, 10);
    }

    @Override
    protected void initInput() {

        inventorySubScene = new InventorySubScene();

        inventorySubScene.getInput().addAction(new UserAction("Close Inventory") {
            @Override
            protected void onActionBegin() {
                getSceneService().popSubScene();
            }
        }, KeyCode.F);

        onKeyDown(KeyCode.F, "Open Inventory", () -> getSceneService().pushSubScene(inventorySubScene));
    }

    @Override
    protected void initGame() {
        // Add initial items in player inventory
        inventorySubScene.playerInventory.add(woodEntity, "Wood", "Wood description", inventorySubScene.view, 15);
        inventorySubScene.playerInventory.add(stoneEntity, "Stone", "Stone description", inventorySubScene.view, 10);
        inventorySubScene.playerInventory.add(crystalEntity, "Crystal", "Crystal description", inventorySubScene.view, 5);
    }

    public void pickupItem(Entity item, String name, String description, int quantity) {
        if (getip(name.toLowerCase()).get() > 0) {
            inventorySubScene.playerInventory.add(item, name, description, inventorySubScene.view, quantity);
            inc(name.toLowerCase(), -1);
            inventorySubScene.view.getListView().refresh();
        }
    }

    public static void main(String[] args) { launch(args); }


    private class InventorySubScene extends SubScene {

        public Inventory<Entity> playerInventory = new Inventory(10);

        public InventoryView view = new InventoryView<>(playerInventory);


        public InventorySubScene() {
            getContentRoot().getChildren().addAll(view);
            getContentRoot().setTranslateX(300);
            getContentRoot().setTranslateY(0);

            Button dropOne = getUIFactoryService().newButton("Drop One");
            dropOne.prefHeight(30.0);
            dropOne.prefWidth(135.0);
            dropOne.setTranslateX(35.0);
            dropOne.setTranslateY(320.0);

            dropOne.setOnAction(actionEvent -> {
                var selectedItem = (Entity) view.getListView().getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    var item = inventorySubScene.playerInventory.getData((Entity) selectedItem).get().getUserItem();
                    playerInventory.incrementQuantity(item, -1);
                }
                view.getListView().refresh();
            });

            Button dropAll = getUIFactoryService().newButton("Drop All");
            dropAll.prefHeight(30.0);
            dropAll.prefWidth(135.0);
            dropAll.setTranslateX(35.0);
            dropAll.setTranslateY(370.0);

            dropAll.setOnAction(actionEvent -> {

                var selectedItem = (Entity) view.getListView().getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    var itemData = inventorySubScene.playerInventory.getData((Entity) selectedItem).get().getUserItem();
                    playerInventory.remove(selectedItem);
                }
                view.getListView().refresh();
            });

            this.getContentRoot().getChildren().addAll(dropOne, dropAll);
        }
    }
}