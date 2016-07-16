## FXGL

JavaFX 8 Game Library written in Java + Kotlin<br/>
[![Join the chat at https://gitter.im/AlmasB/FXGL](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Release](https://img.shields.io/badge/maven-0.2.4-blue.svg)](https://jitpack.io/#AlmasB/FXGL)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg)](https://github.com/AlmasB/FXGL/blob/master/LICENSE)
[![Javadoc](https://img.shields.io/badge/docs-javadoc-green.svg)](http://almasb.github.io/FXGL/javadoc/index.html)
[![Website](https://img.shields.io/badge/www-FXGL-green.svg)](http://almasb.github.io/FXGL/)

## Good for ...
* 2D or casual UI based games
* Hobby & academic projects
* Learning & practising game development
* Fast prototyping

## Not so good for ...
* 3D, mobile or web (until JavaFX can painlessly support these)
* Commercial projects

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
* Quick Time Events (QTE)
* Customizable Intro Video / Animation
* Customizable Main Menu / Game Menu
* Customizable UI elements (Dialogs, Bars, Buttons, etc)
* Customizable Global CSS for menus / UI elements
* Saving / Loading System
* User Profiles (Save/Load/Restore Game Settings)
* Achievement System
* In-game Notification System
* [Log4j2](http://logging.apache.org/log4j/2.x/) Logging Framework
* [EasyIO](https://github.com/AlmasB/EasyIO) IO Framework
* Performance Monitor
* Global Services Framework
* Other minor game dev features

If you have a use case (feature) that FXGL doesn't cover, raise an issue, carefully describing the use case.

## Extra Info
For more information check out the project [Wiki](https://github.com/AlmasB/FXGL/wiki).

## Basic Usage
#### Java Example
```java
public class BasicGameApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
        settings.setVersion("0.1");
        // other settings
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
```
#### Kotlin Example
```kotlin
class BasicGameApp : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 800
            height = 600
            title = "Basic Game App"
            version = "0.1"
            // other settings
        }
    }

    override fun initInput() { }

    override fun initAssets() { }

    override fun initGame() { }

    override fun initPhysics() { }

    override fun initUI() { }

    override fun onUpdate(tpf: Double) { }
}

fun main(args: Array<String>) {
    Application.launch(BasicGameApp::class.java, *args)
}
```

* The [Samples](https://github.com/AlmasB/FXGL/tree/master/samples) folder will be constantly updated to include demonstrations of various features.
* The [YouTube](https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp) videos will walk you through the basics.
* For advanced examples please see [FXGLGames](https://github.com/AlmasB/FXGLGames).

## Setup Tutorials
* [Eclipse](https://www.youtube.com/watch?v=2kLIXDhEGo0)
* [IntelliJ](https://www.youtube.com/watch?v=ZM2NuvMG4cg)

## Maven
```maven
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.AlmasB</groupId>
    <artifactId>FXGL</artifactId>
    <version>0.2.4</version>
</dependency>
```

## Gradle
```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.AlmasB:FXGL:0.2.4'
}
```

## Contact
[![Gmail](https://img.shields.io/badge/Email-almaslvl@gmail.com-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Google+](https://img.shields.io/badge/Google+-AlmasB-red.svg)](https://plus.google.com/+AlmasB0/about)