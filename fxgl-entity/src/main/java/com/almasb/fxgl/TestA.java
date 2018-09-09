package com.almasb.fxgl;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.view.EntityView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestA extends Application {

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(600, 600);

        Entity entity = new Entity();
        entity.getViewComponent().setView(new EntityView(new Rectangle(40, 40)));
        entity.getTransformComponent().setZ(500);

        Entity entity2 = new Entity();
        entity2.setPosition(20, 20);
        entity2.getViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.RED)));

        root.getChildren().addAll(entity.getViewComponent().getParent(), entity2.getViewComponent().getParent());

        root.getChildren().sort((n1, n2) -> );

        return root;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }
}
