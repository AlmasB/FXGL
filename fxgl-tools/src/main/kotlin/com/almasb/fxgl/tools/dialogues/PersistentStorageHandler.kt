/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType
import com.almasb.fxgl.dsl.getSaveLoadService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.profile.SaveLoadHandler
import javafx.scene.paint.Color
import java.io.Serializable

/**
 * TODO: save properties (game vars) to bundle, also consider easy API to read/write PropertyMap
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PersistentStorageService : EngineService() {

    override fun onMainLoopStarting() {
        getSaveLoadService().addHandler(PersistentStorageHandler())
        getSaveLoadService().readAndLoadTask("fxgl-dialogue-editor.prefs").run()
    }

    override fun onExit() {
        getSaveLoadService().saveAndWriteTask("fxgl-dialogue-editor.prefs").run()
    }
}

class PersistentStorageHandler : SaveLoadHandler {

    override fun onSave(data: DataFile) {
        val bundle = Bundle("preferences")

        NodeView.colors.forEach { (key, color) ->
            bundle.put("color_${key}", color.value.toSerializable())
        }

        data.putBundle(bundle)
    }

    override fun onLoad(data: DataFile) {
        val bundle = data.getBundle("preferences")

        bundle.data.forEach { (key, color) ->
            if (key.startsWith("color_")) {
                val type = key.removePrefix("color_")

                val nodeType = DialogueNodeType.valueOf(type)
                NodeView.colors[nodeType]?.value = (color as SerializableColor).toColor()
            }
        }
    }
}

private class SerializableColor(
        val r: Double,
        val g: Double,
        val b: Double,
        val a: Double
) : Serializable {

    fun toColor(): Color = Color.color(r, g, b, a)
}

private fun Color.toSerializable(): SerializableColor = SerializableColor(this.red, this.green, this.blue, this.opacity)