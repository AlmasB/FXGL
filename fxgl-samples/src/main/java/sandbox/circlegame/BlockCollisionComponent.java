/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BlockCollisionComponent extends Component {

    private RandomMoveComponent randomMove;

    private List<Entity> blocks;
    private Point2D prevPos;

    @Override
    public void onAdded() {
        randomMove = entity.getComponent(RandomMoveComponent.class);
        blocks = getGameWorld().getEntitiesByType(CircleNNType.BLOCK);
        prevPos = entity.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        for (var block : blocks) {
            if (block.isColliding(entity)) {
                entity.translate(randomMove.getVelocity().negateLocal());

                changeDirection();

                //var vel = randomMove.getVelocity();

                //applyVelocityManually();
                return;
            }
        }





        //entity.setPosition(prevPos);


        //prevPos = entity.getPosition();
    }

    private void changeDirection() {
        var newDirectionVector = randomMove.getVelocity();

        var angle = FXGLMath.toDegrees(Math.atan(newDirectionVector.y / newDirectionVector.x)) + random(-45, 45);

        randomMove.setDirectionAngle(newDirectionVector.x > 0 ? angle : 180 + angle);
    }

    private void applyVelocityManually() {
        var vel = randomMove.getVelocity();

        var dx = Math.signum(vel.x);
        var dy = Math.signum(vel.y);

        for (int i = 0; i < (int) vel.x; i++) {
            entity.translateX(dx);

            boolean collision = false;

            for (Entity block : blocks) {
                if (block.isColliding(entity)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                entity.translateX(-dx);
                break;
            }
        }

        for (int i = 0; i < (int) vel.y; i++) {
            entity.translateY(dy);

            boolean collision = false;

            for (Entity block : blocks) {
                if (block.isColliding(entity)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                entity.translateY(-dy);
                break;
            }
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
