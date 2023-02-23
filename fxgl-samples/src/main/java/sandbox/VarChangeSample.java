/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class VarChangeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Var change sample");
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "update", () -> {
            inc("time", +1.0);

            var name = gets("name");
            set("name", name + "H");
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("hp", 0);
        vars.put("time", 0.0);
        vars.put("name", "Hello");
    }

    @Override
    protected void initGame() {
        // the event builder way
        eventBuilder()
                .when(() -> geti("hp") == 7)
                .limit(4)
                .thenRun(() -> System.out.println("hello"))
                .buildAndStart();

        // the DSL way
        onDoubleChange("time", value -> {
            System.out.println(value);
        });

        onStringChange("name", value -> {
            System.out.println(value);
        });

        onStringChangeTo("name", "HelloHH", () -> {
            System.out.println("bye");
        });

        onIntChangeTo("hp", 5, () -> System.out.println("Hello"));

        run(() -> inc("hp", +1), Duration.seconds(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
