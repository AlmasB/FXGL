/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GraphVisSample extends GameApplication {

    private static final double NODE_RADIUS = 5;

    private Map<Integer, Entity> nodes;
    private Map<Integer, Boolean> edges;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setRandomSeed(1234);
    }

    @Override
    protected void initGame() {
        nodes = new HashMap<>();
        edges = new HashMap<>();

        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        Entity background = new Entity();

        getGameWorld().addEntity(background);

        List<String> lines = getAssetLoader().loadText("twitter_edges.txt");

        lines.forEach(line -> {
            var tokens = line.split(" +");

            int n1 = Integer.parseInt(tokens[0]);
            int n2 = Integer.parseInt(tokens[1]);

            Entity e1 = nodes.get(n1);

            if (e1 == null) {
                e1 = makeNode(n1);
            }

            Entity e2 = nodes.get(n2);

            if (e2 == null) {
                e2 = makeNode(n2);
            }

            var edge = new Line();
            edge.startXProperty().bind(e1.xProperty().add(NODE_RADIUS));
            edge.startYProperty().bind(e1.yProperty().add(NODE_RADIUS));
            edge.endXProperty().bind(e2.xProperty().add(NODE_RADIUS));
            edge.endYProperty().bind(e2.yProperty().add(NODE_RADIUS));

            background.getViewComponent().addChild(edge);

            edges.put(hash(e1, e2), true);
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        double K = 50;

        var entities = getGameWorld().getEntities();

        // start from 1 to skip the background entity
        for (int i = 1; i < entities.size(); i++) {
            var e1 = entities.get(i);

            for (int j = i + 1; j < entities.size(); j++) {
                var e2 = entities.get(j);

                // e1 -> e2
                var vector = e2.getPosition().subtract(e1.getPosition());

                double d = vector.magnitude();

                // if there's an edge between e1 and e2
                if (edges.containsKey(hash(e1, e2))) {
                    double force = d * d / K * tpf;

                    e1.translate(vector.normalize().multiply(force));
                    e2.translate(vector.normalize().multiply(-force));
                }

                if (d > 0.0) {
                    double force = K * K / d * tpf;

                    e1.translate(vector.normalize().multiply(-force));
                    e2.translate(vector.normalize().multiply(force));

                    forceBounds(e1);
                    forceBounds(e2);
                }
            }
        }
    }

    private Entity makeNode(int id) {
        var e = entityBuilder()
                .at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())))
                .view(new Circle(NODE_RADIUS, NODE_RADIUS, NODE_RADIUS))
                .with("id", id)
                .with(new DraggableComponent())
                .onClick(entity -> {
                    // here you can find out more info about the node in question
                })
                .buildAndAttach();

        nodes.put(id, e);

        return e;
    }

    private void forceBounds(Entity e) {
        if (e.getX() < 0) {
            e.setX(0);
        }

        if (e.getY() < 0) {
            e.setY(0);
        }

        if (e.getX() + 10 > getAppWidth()) {
            e.setX(getAppWidth() - 10);
        }

        if (e.getY() + 10 > getAppHeight()) {
            e.setY(getAppHeight() - 10);
        }
    }

    private int hash(Entity e1, Entity e2) {
        var hash1 = e1.hashCode();
        var hash2 = e2.hashCode();

        return hash1 > hash2
                ? 31 * (31 + hash1) + hash2
                : 31 * (31 + hash2) + hash1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
