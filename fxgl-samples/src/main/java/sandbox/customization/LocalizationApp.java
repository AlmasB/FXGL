/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.customization;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.localization.Language;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LocalizationApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(650);
        settings.setHeight(357);
    }

    int i = 0;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(new LinearGradient(
                0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.BLACK),
                new Stop(1.0, Color.AQUA)
        ));

        var languages = new ArrayList<Language>(getLocalizationService().getLanguages());
        languages.add(new Language("KOREAN"));
        languages.add(new Language("CHINESE"));

        getLocalizationService().addLanguageData(Language.ENGLISH, Map.of("some.key", "Hello World"));
        getLocalizationService().addLanguageData(Language.FRENCH, Map.of("some.key", "Bonjour le monde"));
        getLocalizationService().addLanguageData(Language.GERMAN, Map.of("some.key", "Hallo Welt"));
        getLocalizationService().addLanguageData(Language.HUNGARIAN, Map.of("some.key", "Helló Világ"));
        getLocalizationService().addLanguageData(Language.RUSSIAN, Map.of("some.key", "Привет, мир"));

        getLocalizationService().addLanguageData(new Language("KOREAN"), Map.of("some.key", "안녕 세상"));
        getLocalizationService().addLanguageData(new Language("CHINESE"), Map.of("some.key", "你好，世界"));

        run(() -> {

            getNotificationService().pushNotification(localize("some.key"));

            getSettings().getLanguage().setValue(languages.get(i));

            i++;
            if (i == languages.size())
                i = 0;

        }, Duration.seconds(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
