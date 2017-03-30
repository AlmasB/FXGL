## What?

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Javadoc](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://www.javadoc.io/doc/com.github.almasb/fxgl/)
![CI](https://travis-ci.org/AlmasB/FXGL.svg?branch=master)
[![Coverage](https://api.codacy.com/project/badge/Coverage/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Coverage)
[![Codacy](https://api.codacy.com/project/badge/Grade/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Grade)

## Why?

* No native libraries, 0 setup required
* Simple and clean API
* Brings real-world game development techniques to JavaFX

## Good for ...

* Desktop 2D / casual games
* Hobby / academic / commercial projects
* Learning / improving game development skills
* Fast prototyping of game ideas

## Not so good for ...

* 3D, mobile or web

## Features

* The list of features can be found in the [Wiki](https://github.com/AlmasB/FXGL/wiki/Core-Features)
* [Showcase](http://almasb.github.io/FXGLGames/) 

## Minimal Example

```java
public class BasicGameApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

## Getting Started

* Written tutorials - [Wiki](https://github.com/AlmasB/FXGL/wiki)
* Video tutorials - [YouTube](https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* The [Samples](fxgl-samples) module is constantly updated with feature demos
* Advanced examples - [FXGLGames](https://github.com/AlmasB/FXGLGames)

## Maven

```maven
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>0.3.0</version>
</dependency>
```

## Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.github.almasb:fxgl:0.3.0'
}
```

## Uber jar

Latest pre-compiled uber jar can be found in [Releases](https://github.com/AlmasB/FXGL/releases)

## Download Count

* GitHub Releases (uber jar): ![Github Releases](https://img.shields.io/github/downloads/AlmasB/FXGL/total.svg)
* JitPack (up to 0.2.9): ![Jitpack](https://jitpack.io/v/AlmasB/FXGL/month.svg)
* Maven Central: (0.3.0+): ![Maven](https://img.shields.io/badge/downloads-~53%2Fmonth-green.svg?style=flat)

## Contribution

[Contribute](CONTRIBUTING.md) to FXGL, or support FXGL on [Gratipay](https://gratipay.com/FXGL/).
Alternatively star the repo to show interest.

## Contact
[![Chat](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL)
[![Gmail](https://img.shields.io/badge/Email-almaslvl@gmail.com-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Google+](https://img.shields.io/badge/Google+-AlmasB-red.svg)](https://plus.google.com/+AlmasB0/about)
[![Survey](https://img.shields.io/badge/Feedback-SurveyMonkey-red.svg)](https://www.surveymonkey.com/r/BH6LLPM)
[![Survey2](https://img.shields.io/badge/Feedback-Google%20Forms-red.svg)](https://goo.gl/forms/6wrMnOBxTE1fEpOy2)
