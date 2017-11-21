![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## What?

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Javadoc](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://www.javadoc.io/doc/com.github.almasb/fxgl/)
![CI](https://travis-ci.org/AlmasB/FXGL.svg?branch=master)
[![Coverage](https://api.codacy.com/project/badge/Coverage/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Coverage)
[![Codacy](https://api.codacy.com/project/badge/Grade/9603c2522deb42fbb9146bedfcb860b2)](https://www.codacy.com/app/AlmasB/FXGL?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlmasB/FXGL&amp;utm_campaign=Badge_Grade)

## Why?

* No native libraries, no setup required
* Simple and clean API
* Brings real-world game development techniques to JavaFX
* Works with Java 8 and 9 "out of the box"

## Good for ...

* Any 2D genre (side-scroller / platformer / arcade / RPG)
* Hobby / academic / commercial projects
* Teaching / learning / improving game development skills
* Fast prototyping of game ideas

## Features

* [List of features](https://github.com/AlmasB/FXGL/wiki/Core-Features)
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

* [Wiki & Written tutorials](https://github.com/AlmasB/FXGL/wiki)
* [YouTube tutorials](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* [Sample code demos](fxgl-samples)
* [Game demos](https://github.com/AlmasB/FXGLGames) (src)
* [Game demos](https://github.com/AlmasB/FXGLGames/tree/master/binaries) (.jar)

## Maven

* [Maven](https://github.com/AlmasB/FXGL-Maven) project if you use Java
* [Maven](https://github.com/AlmasB/FXGL-MavenKt) project if you use Kotlin

Already have `pom.xml`? Then add:

```maven
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Gradle

* [Gradle](https://github.com/AlmasB/FXGL-Gradle) project if you use Java
* [Gradle](https://github.com/AlmasB/FXGL-GradleKt) project if you use Kotlin

Already have `build.gradle`? Then add:

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:0.4.0'
}
```

## Uber jar

Download latest uber jar from [Releases](https://github.com/AlmasB/FXGL/releases)

## Contribution

Please see the [Contribution Guide](CONTRIBUTING.md) (including non-code), or simply star the repo to show interest.

## Contact

[![Chat](https://badges.gitter.im/AlmasB/FXGL.svg)](https://gitter.im/AlmasB/FXGL)
[![Gmail](https://img.shields.io/badge/Email-almaslvl@gmail.com-red.svg)](https://plus.google.com/+AlmasB0/about)
