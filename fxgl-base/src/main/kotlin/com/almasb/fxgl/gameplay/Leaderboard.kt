/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.concurrent.IOTask
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.impl.client.HttpClientBuilder

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Leaderboard {

    private val SUCCESS_CODE = 200

    private val httpClient = HttpClientBuilder.create().build()

    private val objectMapper = ObjectMapper().registerKotlinModule()

    private val listScoreClass: JavaType

    init {
        listScoreClass = objectMapper.typeFactory.constructCollectionType(List::class.java, ScoreData::class.java)
    }

    fun loadTopTask(numItems: Int): IOTask<List<ScoreData>> {
        return IOTask.of("Load Top", {

            val getRequest = HttpGet("${baseUrl()}top?gameName=${gameName()}")
            getRequest.addHeader("accept", "application/json")

            val response = httpClient.execute(getRequest)

            if (response.statusLine.statusCode != SUCCESS_CODE) {
                throw RuntimeException("HTTP error code: " + response.statusLine.statusCode)
            }

            // https://github.com/AlmasB/FXGL/issues/486
            objectMapper.readValue<List<ScoreData>>(response.entity.content, listScoreClass)
                    .sortedByDescending { it.score }
                    .take(numItems)
        })
    }

    fun postNewScoreTask(data: ScoreData): IOTask<*> {
        return IOTask.ofVoid("Put New Score", {

            val putRequest = HttpPut("${baseUrl()}newscore?gameName=${gameName()}&name=${data.name}&score=${data.score}")
            putRequest.addHeader("accept", "application/json")

            val response = httpClient.execute(putRequest)

            if (response.statusLine.statusCode != SUCCESS_CODE) {
                throw RuntimeException("HTTP error code: " + response.statusLine.statusCode)
            }

            //objectMapper.readValue<ScoreData>(response.entity.content, ScoreData::class.java)
        })
    }

    private fun baseUrl() = FXGL.getProperties().getString("url.leaderboard")

    private fun gameName() = FXGL.getSettings().title.replace(' ', '_')
}

data class ScoreData(@JsonProperty("name") val name: String,
                     @JsonProperty("score") val score: Int) {
}