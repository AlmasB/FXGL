/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

/*
 * This file contains convenient access functions to the FXGL architecture.
 */

function notify(message) {
    FXGL.getNotificationService().pushNotification(message);
}

function sound(filename) {
    FXGL.getAudioPlayer().playSound(filename);
}

function int(varName) {
    return FXGL.getApp().getGameState().getInt(varName);
}

function intP(varName) {
    return FXGL.getApp().getGameState().intProperty(varName);
}

function double(varName) {
    return FXGL.getApp().getGameState().getDouble(varName);
}

function doubleP(varName) {
    return FXGL.getApp().getGameState().doubleProperty(varName);
}

function boolean(varName) {
    return FXGL.getApp().getGameState().getBoolean(varName);
}

function string(varName) {
    return FXGL.getApp().getGameState().getString(varName);
}

function object(varName) {
    return FXGL.getApp().getGameState().getObject(varName);
}

function println(obj) {
    java.lang.System.out.println(obj);
}

function addQuest(questName, varName, varNum) {
    var Quest = Java.type("com.almasb.fxgl.gameplay.rpg.quest.Quest")
    var QuestObjective = Java.type("com.almasb.fxgl.gameplay.rpg.quest.QuestObjective")

    FXGL.getApp().getGameplay().getQuestManager().addQuest(new Quest(questName, java.util.Arrays.asList(
            new QuestObjective(questName, intP(varName), varNum)
    )));
}

function hasQuests() {
    return FXGL.getApp().getGameplay().getQuestManager().questsProperty().size() > 0;
}

function playerLinesWrap() {
    return Java.to(playerLines())
}

function npcLinesWrap() {
    return Java.to(npcLines())
}

var count = 0

function callMe() {
    count++
    println(count)
}

/* DSL mappings */

var DSLKt = Java.type("com.almasb.fxgl.app.DSLKt")

function byID(name, id) {
    return DSLKt.byID(name, id)
}

function showConfirm(message, callback) {
    DSLKt.showConfirm(message, callback)
}