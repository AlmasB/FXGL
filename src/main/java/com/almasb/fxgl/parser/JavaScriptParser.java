/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.parser;

import com.almasb.fxgl.app.FXGL;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Allows to parse valid javascript source files.
 * Once parsed, {@link #callFunction(String, Object...)}
 * can be used to invoke a JS function.
 */
public final class JavaScriptParser {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("nashorn");
    private Invocable invocableEngine;

    /**
     * Constructs new javascript parser for given .js file or source string.
     * The file will be loaded with {@link com.almasb.fxgl.asset.AssetLoader#loadScript(String)}
     *
     * @param scriptFileName name of script file under "/assets/scripts/" or source string
     * @throws IllegalArgumentException if syntax error
     */
    public JavaScriptParser(String scriptFileName) {
        engine.getContext().getBindings(ScriptContext.GLOBAL_SCOPE).put("HOME_DIR", getClass().getResource("/assets/scripts/"));
        engine.getContext().getBindings(ScriptContext.GLOBAL_SCOPE).put("FXGL", FXGL.Companion);
        engine.getContext().getBindings(ScriptContext.GLOBAL_SCOPE).put("APP", FXGL.getApp());

        try {
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
