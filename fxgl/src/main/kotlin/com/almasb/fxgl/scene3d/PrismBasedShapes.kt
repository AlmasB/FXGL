/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene3d

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */


class Cylinder
@JvmOverloads constructor(

        /**
         * Distance between center and base vertices.
         */
        bottomRadius: Double = DEFAULT_RADIUS,

        /**
         * Distance between center and top vertices.
         */
        topRadius: Double = DEFAULT_RADIUS,
        height: Double = DEFAULT_SIZE
) : Prism(bottomRadius, topRadius, height, DEFAULT_NUM_DIVISIONS)

class Pyramid
@JvmOverloads constructor(

        /**
         * Distance between center and base vertices.
         */
        bottomRadius: Double = DEFAULT_RADIUS,

        /**
         * Distance between center and top vertices.
         */
        topRadius: Double = 0.0,
        height: Double = DEFAULT_SIZE,
        numDivisions: Int = 3
) : Prism(bottomRadius, topRadius, height, numDivisions)

class Cone
@JvmOverloads constructor(

        /**
         * Distance between center and base vertices.
         */
        bottomRadius: Double = DEFAULT_RADIUS,

        /**
         * Distance between center and top vertices.
         */
        topRadius: Double = 0.0,
        height: Double = DEFAULT_SIZE
) : Prism(bottomRadius, topRadius, height, DEFAULT_NUM_DIVISIONS)