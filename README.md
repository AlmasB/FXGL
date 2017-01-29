## FXGL

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Javadoc](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://jitpack.io/com/github/AlmasB/FXGL/0.2.9/javadoc/index.html)
![Code](https://img.shields.io/badge/lines%20of%20code-15k-blue.svg)
![CI](https://travis-ci.org/AlmasB/FXGL.svg?branch=master)
[![Coverage](https://api.codacy.com/project/badge/Coverage/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Coverage)
[![Codacy](https://api.codacy.com/project/badge/Grade/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Grade)

[![Chat](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL)
[![Showcase](https://img.shields.io/badge/www-Showcase-green.svg)](http://almasb.github.io/FXGLGames/)
[![Wiki](https://img.shields.io/badge/www-Wiki-green.svg)](https://github.com/AlmasB/FXGL/wiki)
[![MIT](http://img.shields.io/badge/license-MIT-yellow.svg)](https://github.com/AlmasB/FXGL/blob/master/LICENSE)

## Good for ...
* 2D / casual games
* Hobby / academic projects
* Learning / improving game development skills
* Fast prototyping of game ideas

## Not so good for ...
* 3D, mobile or web (until JavaFX can readily support these)

## Latest Release Features

Graphics & UI | Application Framework
:---:    | :---:
JavaFX 8  | [FXEventBus](https://github.com/AlmasB/FXEventBus)
Multi-Layer Rendering | Time Management System (in-game time + real time)
Canvas Particle System | Multithreading
Dynamic Texture Manipulation | [Log4j2](http://logging.apache.org/log4j/2.x/)
Sprite Sheet Animations | Performance Monitor + Profiling
Target Screen Resolution (+Fullscreen) | Global Services Framework
Customizable Intro Video / Animation | Developer Panel
Customizable Main Menu / Game Menu (3 built-in menu styles) | GC-free Object Pooling
Customizable UI elements (Dialogs, Bars, Buttons, etc)<br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/progress_bar.jpg" width="512" /><br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/profile.jpg" width="512" /> |
Customizable Global CSS for menus / UI elements |
Post-processing Effects (**alpha**) |


User Input | I/O
:---:      | :---:
Key & Mouse Bindings | [EasyIO](https://github.com/AlmasB/EasyIO) & Networking (TCP and UDP)
Full Input Mocking   | Asset Management (".png", ".jpg", ".wav", ".mp3", ".txt", ".ttf/.otf", custom)


Physics |   Utilities
:---: | :---:
[JBox2D](https://github.com/jbox2d/jbox2d) | [GameUtils](https://github.com/AlmasB/GameUtils)
FXGL Physics (BBox + SAT) |
Unified Collision Handling (JBox2D + FXGL physics) |


Gameplay | AI
:---:    | :---:
[Ents (ECS)](https://github.com/AlmasB/Ents) | [gdxAI](https://github.com/libgdx/gdx-ai)
Full Game Loop                              | [AStar](https://github.com/AlmasB/AStar)
Quick Time Events (QTE)<br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/QTE.jpg" width="256" /> | JavaScript Behavior Injections (for entities) + JavaScript FXGL Environment Variables<br/><br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/JS.jpg" width="768" />
Achievement System| 
Notification System<br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/achievement.jpg" width="512" /> |
Saving / Loading System |
User Profiles (Save/Load/Restore Game Settings) |
Level Parsers (.txt, .json (**alpha**) using [jackson](https://github.com/FasterXML/jackson-databind))|
Quest Tracker<br/><img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/quests.jpg" width="512" />|

If you have a use case (feature) that FXGL doesn't cover, raise an issue, carefully describing the use case.

## Showcase

You can browse sample games (with screenshots) on the [FXGLGames](http://almasb.github.io/FXGLGames/) website.
The source code is included.

## Java Example
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

## Kotlin Example
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

* (**up to date**) For all "Getting Started" tutorials check out the [Wiki](https://github.com/AlmasB/FXGL/wiki).
* (**up to date**) The [Samples](https://github.com/AlmasB/FXGL/tree/master/samples) folder will be constantly updated to include demonstrations of various features.
* (**outdated**) The [YouTube](https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp) videos will walk you through the basics.
* For advanced examples please see [FXGLGames](https://github.com/AlmasB/FXGLGames).

## Setup Video Tutorials
* [Eclipse](https://www.youtube.com/watch?v=2kLIXDhEGo0)
* [IntelliJ](https://www.youtube.com/watch?v=ZM2NuvMG4cg)

## Maven
```maven
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>0.2.9</version>
</dependency>
```

## Gradle
```gradle
repositories {
    // Needed to resolve unreleased dependencies of com.github.almasb:fxgl:0.2.9
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile 'com.github.almasb:fxgl:0.2.9'
}
```

## Uber jar
Latest pre-compiled uber jar can be found in [Releases](https://github.com/AlmasB/FXGL/releases)

## Contact
[![Gmail](https://img.shields.io/badge/Email-almaslvl@gmail.com-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Google+](https://img.shields.io/badge/Google+-AlmasB-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Survey](https://img.shields.io/badge/Feedback-SurveyMonkey-red.svg)](https://www.surveymonkey.com/r/BH6LLPM)
[![Survey2](https://img.shields.io/badge/Feedback-Google%20Forms-red.svg)](https://goo.gl/forms/6wrMnOBxTE1fEpOy2)
