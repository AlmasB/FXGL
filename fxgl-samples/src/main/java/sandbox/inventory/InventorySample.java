/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox.inventory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.inventory.Inventory;
import com.almasb.fxgl.inventory.InventoryListView;
import com.almasb.fxgl.inventory.ItemConfig;
import javafx.scene.control.Button;
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
        var crystal = new CustomItem("Crystal that has a really long name going beyond the limit of 20");

        var vbox = new VBox(5);

        List.of(wood, stone, crystal)
                .forEach(item -> {
                    var btnAdd = new Button("Pick up " + item.description);
                    var btnRemove = new Button("Drop " + item.description);

                    btnAdd.setOnAction(e -> {
                        inventory.add(item, new ItemConfig(item.description), +1);
                    });

                    btnRemove.setOnAction(e -> {
                        inventory.incrementQuantity(item, -1);
                    });

                    vbox.getChildren().addAll(btnAdd, btnRemove);
                });

        addUINode(vbox, 100, 100);
        addUINode(new InventoryListView<>(inventory), 340, 70);
    }

    public static void main(String[] args) {
        launch(args);
    }
}