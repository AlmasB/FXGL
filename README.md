## FXGL
JavaFX 8 Game Library<br/>
[![Release](https://img.shields.io/badge/maven-0.2.0-blue.svg)](https://jitpack.io/#AlmasB/FXGL)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/AlmasB/FXGL/blob/master/LICENSE)
[![Javadoc](https://img.shields.io/badge/docs-javadoc-green.svg)](http://almasb.github.io/FXGL/javadoc/index.html)

## Maven
```maven
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.AlmasB</groupId>
    <artifactId>FXGL</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Gradle
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.AlmasB:FXGL:0.2.0'
}
```

## Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
It is primarily aimed at people who wish to learn and practise game development.
It also takes care of the common boilerplate code, so it can be used for fast prototyping.
For larger projects the library may not be as suitable, whereas advanced programmers will probably want to work
with something like libGDX or JMonkey.
If you have a use case (feature) that FXGL doesn't cover, raise an issue, carefully describing the use case.

## Basic Usage / Examples
The samples/ folder will be constantly updated to include demonstrations of various features.
Video Tutorials Playlist - <a href="https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp">YouTube Link</a> <br/>
The videos will walk you through the basics. For advanced examples please see <a href="https://github.com/AlmasB/FXGLGames">FXGL Games Repository</a>.

## Extra Info
For more information check out the project <a href="https://github.com/AlmasB/FXGL/wiki">Wiki</a>

## Latest Release Features
FXGL 0.2.0 supports:
* Full JavaFX Integration (FXGL is built on top of JavaFX 8)
* [JBox2D](https://github.com/jbox2d/jbox2d) Physics Engine Integration (fork based on v.2.3.0)
* [Ents](https://github.com/AlmasB/Ents) Entity Component/Control System Integration
* [FXEventBus](https://github.com/AlmasB/FXEventBus) Event System Integration
* Game Loop
* Input Bindings (Keys + Mouse)
* Automated Asset Management (".png", ".jpg", ".wav", ".mp3", ".txt", ".ttf/.otf" custom binary formats)
* Automated Collision Handling (physics collisions are hooked into FXGL)
* Automated Target Screen Resolution (+Fullscreen)
* Particle System with Canvas Rendering
* Multi-Layer Rendering
* Dynamic Texture Manipulation (Texture Processing + Sprite Sheet Animations)
* Time Management System
* Audio System
* Multithreading
* Networking (both TCP and UDP)
* AI Pathfinding ([A* Search](https://en.wikipedia.org/wiki/A*_search_algorithm))
* Quick Time Events (QTE) (<b>API INCOMPLETE</b>)
* Intro Video / Animation (supports custom intro)
* Few Built-in Styles of Main Menu + Game Menu (supports custom Main Menu and Game Menu)
* Custom Modifiable UI elements (Dialogs, Bars, Buttons, etc)
* Custom Global CSS to use with existing menus / UI elements
* Saving / Loading System
* User Profiles (Save/Load/Restore Game Settings)
* Achievement System
* In-game Notification System
* Logging & Performance Monitor
* Other minor game dev features

## Contact
Email: almaslvl@gmail.com<br/>
<a href="https://plus.google.com/+AlmasB0/about">Google+</a>