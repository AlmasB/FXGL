/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.inventory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

/**
 * @author Charly Zhu (charlyzhu@hotmail.com)
 */
public class InventorySample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(1000);
    }

    @Override
    protected void initUI() {
        int slotSize = 40;
//        Inventory inventory = new Inventory(9, 5, "Test Inventory");
//        inventory.add(0, new ItemStack(FXGL.texture("brick.png", slotSize, slotSize), 1));
//        inventory.add(1, new ItemStack(FXGL.texture("grass.png", slotSize, slotSize), 3));
//        inventory.add(2, new ItemStack(FXGL.texture("brick.png", slotSize, slotSize), 5));
//        inventory.add(19, new ItemStack(FXGL.texture("brick.png",slotSize, slotSize), 7));
//        inventory.add(12, new ItemStack(FXGL.texture("bubble.png", slotSize, slotSize), 22));
//        inventory.add(26, new ItemStack(FXGL.texture("brick.png",slotSize, slotSize), 9));
//        inventory.add(35, new ItemStack(FXGL.texture("coin.png", slotSize, slotSize), 99));
//        inventory.add(42, new ItemStack(FXGL.texture("brick.png",slotSize, slotSize), 88));
//        InventoryView view = inventory.getView();
//        addUINode(view, 50, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}