package client.game.collision;

import com.almasb.fxgl.physics.AddCollisionHandler;
import com.almasb.fxgl.physics.CollisionHandler;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class TestCollisionHandler2 extends CollisionHandler {

    public TestCollisionHandler2(Object a, Object b) {
        super(a, b);
    }
}
