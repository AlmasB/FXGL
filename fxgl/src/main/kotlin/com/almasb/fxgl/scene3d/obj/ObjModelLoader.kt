/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d.obj

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.scene3d.Model3D
import com.almasb.fxgl.scene3d.Model3DLoader
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.CullFace
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import java.net.URL

/**
 * TODO: revisit implementation
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ObjModelLoader : Model3DLoader {

    companion object {
        private val objParsers = linkedMapOf<(String) -> Boolean, (List<String>, ObjData) -> Unit>()
        private val mtlParsers = linkedMapOf<(String) -> Boolean, (List<String>, MtlData) -> Unit>()

        init {
            objParsers[ { it.startsWith("g") }  ] = Companion::parseGroup
            objParsers[ { it.startsWith("s") }  ] = Companion::parseSmoothing
            objParsers[ { it.startsWith("vt") }  ] = Companion::parseVertexTextures
            objParsers[ { it.startsWith("vn") }  ] = Companion::parseVertexNormals
            objParsers[ { it.startsWith("v ") }  ] = Companion::parseVertices
            objParsers[ { it.startsWith("f") }  ] = Companion::parseFaces
            objParsers[ { it.startsWith("mtllib") }  ] = Companion::parseMaterialLib
            objParsers[ { it.startsWith("usemtl") }  ] = Companion::parseUseMaterial

            mtlParsers[ { it.startsWith("newmtl") }  ] = Companion::parseNewMaterial
            mtlParsers[ { it.startsWith("Ka") }  ] = Companion::parseColorAmbient
            mtlParsers[ { it.startsWith("Kd") }  ] = Companion::parseColorDiffuse
            mtlParsers[ { it.startsWith("Ks") }  ] = Companion::parseColorSpecular
            mtlParsers[ { it.startsWith("Ns") }  ] = Companion::parseSpecularPower
            mtlParsers[ { it.startsWith("map_Kd") }  ] = Companion::parseDiffuseMap
        }

        private fun parseGroup(tokens: List<String>, data: ObjData) {
            val groupName = if (tokens.isEmpty()) "default" else tokens[0]

            data.groups += ObjGroup(groupName)
        }

        private fun parseSmoothing(tokens: List<String>, data: ObjData) {
            data.currentGroup.currentSubGroup.smoothingGroup = tokens.toSmoothingGroup()
        }

        private fun parseVertexTextures(tokens: List<String>, data: ObjData) {
            data.vertexTextures += tokens.toFloats2()
        }

        private fun parseVertexNormals(tokens: List<String>, data: ObjData) {
            data.vertexNormals += tokens.toFloats3()
        }

        private fun parseVertices(tokens: List<String>, data: ObjData) {
            // for -Y
            // .mapIndexed { index, fl -> if (index == 1) -fl else fl }
            data.vertices += tokens.toFloats3()
        }

        private fun parseFaces(tokens: List<String>, data: ObjData) {
            if (tokens.size > 3) {
                for (i in 2 until tokens.size) {
                    parseFaceVertex(tokens[0], data)
                    parseFaceVertex(tokens[i-1], data)
                    parseFaceVertex(tokens[i], data)
                }
            } else {
                tokens.forEach { token ->
                    parseFaceVertex(token, data)
                }
            }
        }

        /**
         * Each token is of form v1/(vt1)/(vn1).
         * Case v1
         * Case v1/vt1
         * Case v1//n1
         * Case v1/vt1/vn1
         */
        private fun parseFaceVertex(token: String, data: ObjData) {
            val faceVertex = token.split("/")

            // JavaFX format is vertices, normals and tex
            when (faceVertex.size) {
                // f v1
                1 -> {
                    data.currentGroup.currentSubGroup.faces += faceVertex[0].toInt() - 1
                    data.currentGroup.currentSubGroup.faces += 0
                    data.currentGroup.currentSubGroup.faces += 0
                }

                // f v1/vt1
                2 -> {
                    data.currentGroup.currentSubGroup.faces += faceVertex[0].toInt() - 1
                    data.currentGroup.currentSubGroup.faces += 0
                    data.currentGroup.currentSubGroup.faces += faceVertex[1].toInt() - 1
                }

                // f v1//vn1
                // f v1/vt1/vn1
                3 -> {
                    data.currentGroup.currentSubGroup.faces += faceVertex[0].toInt() - 1
                    data.currentGroup.currentSubGroup.faces += faceVertex[2].toInt() - 1
                    data.currentGroup.currentSubGroup.faces += (faceVertex[1].toIntOrNull() ?: 1) - 1
                }
            }
        }

        private fun parseMaterialLib(tokens: List<String>, data: ObjData) {
            val fileName = tokens[0]
            val mtlURL = URL(data.url.toExternalForm().substringBeforeLast('/') + '/' + fileName)

            val mtlData = loadMtlData(mtlURL)

            data.materials += mtlData.materials
            data.ambientColors += mtlData.ambientColors
        }

        private fun parseUseMaterial(tokens: List<String>, data: ObjData) {
            data.currentGroup.subGroups += SubGroup()

            data.currentGroup.currentSubGroup.material = data.materials[tokens[0]]
                    ?: throw RuntimeException("Material with name ${tokens[0]} not found")

            data.currentGroup.currentSubGroup.ambientColor = data.ambientColors[data.currentGroup.currentSubGroup.material]
        }

        private fun List<String>.toFloats2(): List<Float> {
            return this.take(2).map { it.toFloat() }
        }

        private fun List<String>.toFloats3(): List<Float> {
            return this.take(3).map { it.toFloat() }
        }

        private fun List<String>.toColor(): Color {
            val rgb = this.toFloats3().map { if (it > 1.0) 1.0 else it.toDouble() }
            return Color.color(rgb[0], rgb[1], rgb[2])
        }

        private fun List<String>.toSmoothingGroup(): Int {
            return if (this[0] == "off") 0 else this[0].toInt()
        }

        private fun parseNewMaterial(tokens: List<String>, data: MtlData) {
            data.currentMaterial = PhongMaterial()
            data.materials[tokens[0]] = data.currentMaterial
        }

        private fun parseColorAmbient(tokens: List<String>, data: MtlData) {
            data.ambientColors[data.currentMaterial] = tokens.toColor()
        }

        private fun parseColorDiffuse(tokens: List<String>, data: MtlData) {
            data.currentMaterial.diffuseColor = tokens.toColor()
        }

        private fun parseColorSpecular(tokens: List<String>, data: MtlData) {
            data.currentMaterial.specularColor = tokens.toColor()
        }

        private fun parseSpecularPower(tokens: List<String>, data: MtlData) {
            data.currentMaterial.specularPower = tokens[0].toDouble()
        }

        private fun parseDiffuseMap(tokens: List<String>, data: MtlData) {
            val ext = data.url.toExternalForm().substringBeforeLast("/") + "/"

            data.currentMaterial.diffuseMap = FXGL.getAssetLoader().loadImage(URL(ext + tokens[0]))
        }

        private fun loadObjData(url: URL): ObjData {
            val data = ObjData(url)

            load(url, objParsers, data)

            return data
        }

        private fun loadMtlData(url: URL): MtlData {
            val data = MtlData(url)

            load(url, mtlParsers, data)

            return data
        }

        private fun <T> load(url: URL,
                             parsers: Map<(String) -> Boolean, (List<String>, T) -> Unit>,
                             data: T) {

            url.openStream().bufferedReader().useLines {
                it.forEach { line ->

                    val lineTrimmed = line.trim()

                    for ((condition, action) in parsers) {
                        if (condition.invoke(lineTrimmed) ) {
                            // drop identifier
                            val tokens = lineTrimmed.split(" +".toRegex()).drop(1)

                            action.invoke(tokens, data)
                            break
                        }
                    }
                }
            }
        }
    }

    // TODO: smoothing groups
    override fun load(url: URL): Model3D {
        try {
            val data = loadObjData(url)
            val modelRoot = Model3D()

            data.groups.forEach {
                val groupRoot = Model3D()
                groupRoot.properties["name"] = it.name

                it.subGroups.forEach {

                    // TODO: ?
                    if (!it.faces.isEmpty()) {

                        val mesh = TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD)

                        mesh.points.addAll(*data.vertices.map { it * 0.05f }.toFloatArray())

                        // if there are no vertex textures, just add 2 values
                        if (data.vertexTextures.isEmpty()) {
                            mesh.texCoords.addAll(*FloatArray(2) { _ -> 0.0f })
                        } else {
                            mesh.texCoords.addAll(*data.vertexTextures.toFloatArray())
                        }

                        // if there are no vertex normals, just add 3 values
                        if (data.vertexNormals.isEmpty()) {
                            mesh.normals.addAll(*FloatArray(3) { _ -> 0.0f })
                        } else {
                            mesh.normals.addAll(*data.vertexNormals.toFloatArray())
                        }

                        mesh.faces.addAll(*it.faces.toIntArray())

                        if (it.smoothingGroups.isNotEmpty()) {
                            mesh.faceSmoothingGroups.addAll(*it.smoothingGroups.toIntArray())
                        }

                        val view = MeshView(mesh)
                        view.material = it.material
                        view.cullFace = CullFace.NONE

                        groupRoot.addMeshView(view)
                    }
                }

                modelRoot.addModel(groupRoot)
            }

            return modelRoot
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Load failed for URL: $url Error: $e")
        }
    }
}