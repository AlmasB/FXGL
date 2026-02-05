/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to listen for changes in variables and react.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class VarChangeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("hp", 0);
        vars.put("time", 0.0);
        vars.put("name", "Hello");
    }

    @Override
    protected void initGame() {
        // the DSL way, if you need something set up quickly
        onDoubleChange("time", value -> {
            System.out.println("The var <time> is now: " + value);
        });

        onStringChange("name", value -> {
            System.out.println("The var <name> is now: " + value);
        });

        onStringChangeTo("name", "HelloHH", () -> {
            System.out.println("The var <name> reached HelloHH");
        });

        onIntChangeTo("hp", 5, () -> {
            System.out.println("The var <hp> reached 5");
        });

        // the event builder way, if you need more control over execution
        eventBuilder()
                .when(() -> geti("hp") == 7)
                .limit(4)
                .thenRun(() -> System.out.println("The <hp> var reached 7. You will see this message 4 times"))
                .buildAndStart();

        // the listener way, if you want to control lifecycle / clean-up process
        getip("hp").subscribe((oldValue, newValue) -> {
            if (newValue.intValue() == 4) {
                System.out.println("The var <hp> reached 4");
            }
        });

        run(() -> {
            inc("hp", +1);
            inc("time", +1.0);

            set("name", gets("name") + "H");

        }, Duration.seconds(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
