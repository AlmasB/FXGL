package client.game.collision;

import com.almasb.fxgl.physics.AddCollisionHandler;
import com.almasb.fxgl.physics.CollisionHandler;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class TestCollisionHandler3 extends CollisionHandler {

    public TestCollisionHandler3(Object a, Object b) {
        super(a, b);
    }
}
