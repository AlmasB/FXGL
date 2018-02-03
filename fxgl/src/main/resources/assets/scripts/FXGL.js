/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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

/* DSL mappings */

var DSLKt = Java.type("com.almasb.fxgl.app.DSLKt")

function byID(name, id) {
    return DSLKt.byID(name, id)
}

function showConfirm(message, callback) {
    // workaround for jdk nashorn bug: https://bugs.java.com/view_bug.do?bug_id=8162839
    // in short functions defined in different scopes (engine vs global) cannot be passed to Java
    // here callback is an engine function and the anon function below is a global function
    // since FXGL.js is in global scope, we just wrap the engine function with a global function
    DSLKt.showConfirm(message, function(yes) { callback(yes) })
}