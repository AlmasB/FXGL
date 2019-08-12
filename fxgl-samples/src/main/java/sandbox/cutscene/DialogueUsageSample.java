/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.cutscene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.io.FS;
import dev.dialogue.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DialogueUsageSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "test", () -> {
            var graph = new DialogueGraph();

            graph = new FS(true).<DialogueGraph>readDataTask("graph.dat")
                    .onFailure(e -> e.printStackTrace())
                    .run();

//            var start = new StartNode("Hello world!");
//            var end = new EndNode("Bye!");
//
//            var text1 = new TextNode("This is an example text.");
//            var text2 = new TextNode("This is another line of text.");
//
//            graph.addNode(start);
//            graph.addNode(end);
//            graph.addNode(text1);
//            graph.addNode(text2);
//
//            graph.addEdge(start, text1);
//            graph.addEdge(text1, text2);
//            graph.addEdge(text2, end);
            if (graph != null) {

                var scene = new DialogueScene(getGameController(), getAppWidth(), getAppHeight());
                scene.start(graph);
            }
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
