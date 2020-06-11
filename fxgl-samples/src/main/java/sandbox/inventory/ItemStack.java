package sandbox.inventory;

import com.almasb.fxgl.texture.Texture;

public class ItemStack {
    Texture texture;
    int amount;

    public ItemStack(Texture texture, int amount) {
        this.texture = texture;
        this.amount = amount;
    }
}
