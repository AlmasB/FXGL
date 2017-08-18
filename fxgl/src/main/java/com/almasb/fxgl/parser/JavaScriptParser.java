/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.parser;

import com.almasb.fxgl.app.FXGL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Allows to parse valid javascript source files.
 * Once parsed, {@link #callFunction(String, Object...)}
 * can be used to invoke a JS function.
 */
public final class JavaScriptParser {
    private static final ScriptEngineManager manager = new ScriptEngineManager();

    static {
        manager.put("HOME_DIR", JavaScriptParser.class.getResource("/assets/scripts/"));
        manager.put("FXGL", FXGL.Companion);
        manager.put("APP", FXGL.getApp());
    }

    private ScriptEngine engine = manager.getEngineByName("nashorn");
    private Invocable invocableEngine;

    /**
     * Constructs new javascript parser for given .js file or source string.
     * The file will be loaded with AssetLoader.
     *
     * @param scriptFileName name of script file under "/assets/scripts/" or source string
     * @throws IllegalArgumentException if syntax error
     */
    public JavaScriptParser(String scriptFileName) {
        try {
            engine.eval(FXGL.getAssetLoader().loadScript("FXGL.js"));

            if (scriptFileName.endsWith(".js")) {
                engine.eval(FXGL.getAssetLoader().loadScript(scriptFileName));
            } else {
                engine.eval(scriptFileName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Script cannot parsed: " + e);
        }

        invocableEngine = (Invocable) engine;
    }

    /**
     * Invokes a JS function.
     *
     * @param name function name
     * @param args function arguments
     * @param <T> return type
     * @return object returned by function
     * @throws IllegalArgumentException if any error occurred during invocation
     */
    @SuppressWarnings("unchecked")
    public <T> T callFunction(String name, Object... args) {
        try {
            return (T) invocableEngine.invokeFunction(name, args);
        } catch (Exception e) {
            throw new IllegalArgumentException("Function call failed: " + e);
        }
    }
}
