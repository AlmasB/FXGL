## FXGL

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Javadoc](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://jitpack.io/com/github/AlmasB/FXGL/0.2.9/javadoc/index.html)
![CI](https://travis-ci.org/AlmasB/FXGL.svg?branch=master)
[![Coverage](https://api.codacy.com/project/badge/Coverage/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Coverage)
[![Codacy](https://api.codacy.com/project/badge/Grade/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Grade)
[![Showcase](https://img.shields.io/badge/www-Showcase-green.svg)](http://almasb.github.io/FXGLGames/)

## Good for ...

* 2D / casual games
* Hobby / academic / commercial projects
* Learning / improving game development skills
* Fast prototyping of game ideas

## Not so good for ...

* 3D, mobile or web (until JavaFX can readily support these)

## Latest Release Features

The ever-growing list of features can be found in the [Wiki](https://github.com/AlmasB/FXGL/wiki/Core-Features)

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
}

fun main(args: Array<String>) {
    Application.launch(BasicGameApp::class.java, *args)
}
```

## Getting Started

* For written tutorials check out the [Wiki](https://github.com/AlmasB/FXGL/wiki)
* For video tutorials check out the [YouTube](https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp) channel
* The [Samples](https://github.com/AlmasB/FXGL/tree/master/samples) folder is constantly updated to include demos of various features
* For advanced examples please see [FXGLGames](https://github.com/AlmasB/FXGLGames)

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
dependencies {
    compile 'com.github.almasb:fxgl:0.2.9'
}
```

## Uber jar

Latest pre-compiled uber jar can be found in [Releases](https://github.com/AlmasB/FXGL/releases)

## Contribution

The contribution [guide](CONTRIBUTING.md)

## Contact
[![Chat](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL)
[![Gmail](https://img.shields.io/badge/Email-almaslvl@gmail.com-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Google+](https://img.shields.io/badge/Google+-AlmasB-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Survey](https://img.shields.io/badge/Feedback-SurveyMonkey-red.svg)](https://www.surveymonkey.com/r/BH6LLPM)
[![Survey2](https://img.shields.io/badge/Feedback-Google%20Forms-red.svg)](https://goo.gl/forms/6wrMnOBxTE1fEpOy2)