/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.inventory;

import com.almasb.fxgl.inventory.Inventory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author Charly Zhu (charlyzhu@hotmail.com)
 */
public class InventoryView extends Region {
    private Inventory inv;
    public static int SLOT_SIZE = 50;
    public static double SLOT_SPACING = 10;

    private int width;
    private int height;

    InventoryView(Inventory inv, int width, int height) {
        this.inv = inv;
        this.width = width;
        this.height = height;
        recreateView();
    }

    private void recreateView() {
        ObservableList<Node> observableList = this.getChildren();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        this.setPadding(new Insets(10, 10, 10, 10));
        hBox.getChildren().add(getInventoryDisplayableContent());
        hBox.getChildren().add(getButtonPanel());
        observableList.add(hBox);
    }

    private VBox getInventoryDisplayableContent() {
        String name = "name";
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(SLOT_SPACING);
        ObservableList<Node> vBoxList = vBox.getChildren();
        if (name != null && !name.equals("")) {
            Text nameText = new Text(name);
            nameText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, SLOT_SIZE / 5 * 3));
            vBoxList.add(nameText);
        }
        int index = 0;
        for (int h = 0; h < height; h++) {
            HBox hBox = new HBox();
            hBox.setSpacing(SLOT_SPACING);
            ObservableList<Node> hBoxList = hBox.getChildren();
            for (int w = 0; w < width; w++) {
                hBoxList.add(getSlotRectangle(index++));
            }
            vBoxList.add(hBox);
        }
        return vBox;
    }

    private VBox getButtonPanel() {
        VBox vbox = new VBox();
        Button btn = new Button();
        btn.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        vbox.getChildren().add(btn);
        return vbox;
    }

    private StackPane getSlotRectangle(int index) {
        Rectangle slotBg = new Rectangle(SLOT_SIZE, SLOT_SIZE);
        slotBg.setArcWidth(15);
        slotBg.setArcHeight(15);
        slotBg.setStroke(Color.RED);
        slotBg.setStrokeWidth(3);

        StackPane slot = new StackPane();
        ObservableList<Node> list = slot.getChildren();
        list.add(slotBg);
//        ItemStack item = inv.get(index);
//        if (item != null && item.texture != null && item.amount > 0) {
//            Texture texture = item.texture;
//            list.add(texture);
//
//            Text text = new Text(item.amount + "");
//            text.setTranslateX(SLOT_SIZE / 5);
//            text.setTranslateY(SLOT_SIZE / 5);
//            text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, SLOT_SIZE / 5 * 2));
//            text.setFill(Color.WHITE);
//            text.setStroke(Color.GREY);
//            list.add(text);
//        }
        return slot;
    }
}