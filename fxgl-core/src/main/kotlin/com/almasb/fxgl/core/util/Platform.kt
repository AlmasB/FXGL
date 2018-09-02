package com.almasb.fxgl.core.util

import com.gluonhq.charm.down.Platform

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object Platform {

    // cheap hack for now
    fun isBrowser() = System.getProperty("fxgl.isBrowser", "false") == "true"

    // javafxports doesn't have "web" option, so will incorrectly default to desktop, hence the extra check
    fun isDesktop() = !isBrowser() && Platform.isDesktop()

    fun isMobile() = isAndroid() || isIOS()
    fun isAndroid() = Platform.isAndroid()
    fun isIOS() = Platform.isIOS()
}