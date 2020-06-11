package sandbox.inventory;

import java.util.ArrayList;

public class Inventory extends ArrayList<ItemStack> {
    private InventoryView view;

    private int width, height;
    private String name;

    public Inventory(int horizontalSlots, int verticalSlots, String name) {
        width = horizontalSlots;
        height = verticalSlots;
        this.name = name;
        for (int i = 0; i < width * height; i++)
            this.add(null);
    }

    public InventoryView getView() {
        view = new InventoryView(this);
        return view;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }
}
