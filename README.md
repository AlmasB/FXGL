## FXGL

JavaFX 8 Game Library written in Java + Kotlin<br/>
[![Join the chat at https://gitter.im/AlmasB/FXGL](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Release](https://img.shields.io/badge/maven-0.2.1-blue.svg)](https://jitpack.io/#AlmasB/FXGL)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/AlmasB/FXGL/blob/master/LICENSE)
[![Javadoc](https://img.shields.io/badge/docs-javadoc-green.svg)](http://almasb.github.io/FXGL/javadoc/index.html)
[![Website](https://img.shields.io/badge/www-FXGL-green.svg)](http://almasb.github.io/FXGL/)

## Maven
```maven
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.AlmasB</groupId>
    <artifactId>FXGL</artifactId>
    <version>0.2.1</version>
</dependency>
```

## Gradle
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.AlmasB:FXGL:0.2.1'
}
```

## Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
It is primarily aimed at people who wish to learn and practise game development.
It also takes care of the common boilerplate code, so it can be used for fast prototyping.
For larger projects the library may not be as suitable, whereas advanced programmers will probably want to work
with something like libGDX or JMonkey.
If you have a use case (feature) that FXGL doesn't cover, raise an issue, carefully describing the use case.

## Setup Tutorials
* [Eclipse](https://www.youtube.com/watch?v=2kLIXDhEGo0)
* [IntelliJ](https://www.youtube.com/watch?v=ZM2NuvMG4cg)

## Basic Usage / Examples
The samples/ folder will be constantly updated to include demonstrations of various features.
Video Tutorials Playlist - <a href="https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp">YouTube Link</a> <br/>
The videos will walk you through the basics. For advanced examples please see <a href="https://github.com/AlmasB/FXGLGames">FXGL Games Repository</a>.

## Extra Info
For more information check out the project <a href="https://github.com/AlmasB/FXGL/wiki">Wiki</a>

## Latest Release Features
* Full JavaFX Integration (FXGL is built on top of JavaFX 8)
* Top level Java interfaces with lower level [Kotlin](https://github.com/JetBrains/kotlin) implementation
* [JBox2D](https://github.com/jbox2d/jbox2d) Physics Engine Integration (fork based on v.2.3.0)
* [Ents](https://github.com/AlmasB/Ents) Entity Component/Control System Integration
* [FXEventBus](https://github.com/AlmasB/FXEventBus) Event System Integration
* [AStar](https://github.com/AlmasB/AStar) AI Pathfinding Integration ([A* Search](https://en.wikipedia.org/wiki/A*_search_algorithm))
* Game Loop
* Input Bindings (Keys + Mouse)
* Automated Asset Management (".png", ".jpg", ".wav", ".mp3", ".txt", ".ttf/.otf", custom binary formats)
* Text/Script Parsers
* Automated Collision Handling (physics collisions are hooked into FXGL)
* Automated Target Screen Resolution (+Fullscreen)
* Particle System with Canvas Rendering
* Multi-Layer Rendering
* Dynamic Texture Manipulation (Texture Processing + Sprite Sheet Animations)
* Time Management System
* Audio System
* Multithreading
* Networking (both TCP and UDP)
* Quick Time Events (QTE) (<b>API INCOMPLETE</b>)
* Customizable Intro Video / Animation
* Customizable Main Menu / Game Menu
* Customizable UI elements (Dialogs, Bars, Buttons, etc)
* Customizable Global CSS for menus / UI elements
* Saving / Loading System
* User Profiles (Save/Load/Restore Game Settings)
* Achievement System
* In-game Notification System
* Logging & Performance Monitor
* Other minor game dev features

## Contact
Email: almaslvl@gmail.com<br/>
<a href="https://plus.google.com/+AlmasB0/about">Google+</a>