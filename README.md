![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## Languages

&emsp;&emsp;English

&emsp;&emsp;[简体中文](https://github.com/AlmasB/FXGL/blob/dev/README_CN.md)

&emsp;&emsp;[Русский](https://github.com/AlmasB/FXGL/blob/dev/README_RU.md)

## About

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)
[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)
[![sponsor](https://img.shields.io/badge/sponsor-%241-brightgreen)](https://github.com/sponsors/AlmasB)
[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/fxgl)

### Why FXGL?

* No installation or setup is required
* "Out of the box": Java 8-21, Win/Mac/Linux/Android 8+/iOS 11.0+/Web
* Simple and clean API, higher level than other engines
* Superset of JavaFX: no need to learn new UI API
* Real-world game development techniques: Entity-Component, interpolated animations, particles, and [many more](https://github.com/AlmasB/FXGL/wiki/Core-Features)
* Games are easily packaged into a single executable .jar, or native images

### Good for:

* Any 2D game (side-scroller / platformer / arcade / RPG)
* Any business application with complex UI controls / animations
* Experimental 3D
* Hobby / academic / commercial projects
* Teaching / learning / improving game development skills
* Fast prototyping of app ideas

### Minimal Example

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

For a quick dive into code, see standalone [basic examples](https://github.com/AlmasB/FXGL/tree/dev/fxgl-samples/src/main/java/basics).

Otherwise, see:

* [FXGL book](https://link.springer.com/book/10.1007/978-1-4842-8625-8?sap-outbound-id=3352BB472E8E602B4B29844F1A86CCC4374DDF6E)
* [Showcase Trailer](https://youtu.be/fuDQg7W0v4g)
* [Use FXGL with IntelliJ](https://youtu.be/LhmlFC6KE2Q)
* [Wiki & written tutorials](https://github.com/AlmasB/FXGL/wiki)
* [YouTube tutorials](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* [Sample code demos](fxgl-samples)
* [FXGL games](https://github.com/AlmasB/FXGLGames) (with source)
* [Published demos](https://fxgl.itch.io/) on itch.io

### Maven

* [Maven](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin

```xml
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>21.1</version>
</dependency>
```

### Gradle

* [Gradle](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin

Please refer to the template if there are any errors.

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:21.1'
}
```

### Modularity

If you wish to develop a modular application, here's a complete example of your `module-info.java`:

```java
open module app.name {
    requires com.almasb.fxgl.all;
}
```

### Uber jar

Download the latest uber jar from [Releases](https://github.com/AlmasB/FXGL/releases)

## Community

* University of Brighton, UK
* University of Nottingham, UK
* Georgia Institute of Technology, USA
* kidOYO, USA
* Walton High School, USA
* Conant High School, USA
* Zealand Institute of Business and Technology, Denmark
* Federal Institute of Education, Science and Technology of Rio Grande do Sul, Brazil
* FHNW School of Engineering / Computer Science, Switzerland
* Johann-Andreas-Schmeller-Gymnasium Nabburg, Germany

If your institution wants to use or is using FXGL, add a note via [GitHub Discussions](https://github.com/AlmasB/FXGL/discussions) to be added to the list.

Community tutorials:

- [Space Ranger](https://www.journaldev.com/40219/space-rangers-game-java-fxgl) at journaldev
- [Geometry Wars](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/) at webtechie
- [Mazela-Man](https://dykstrom.github.io/mazela-man-web/home/) by dykstrom

Community projects (identified using `fxgl` topic): 

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [Consume](https://ergoscrit.itch.io/consume)
- [FXGL Sliding Puzzle](https://github.com/beryx/fxgl-sliding-puzzle)

If you'd like your project featured here, just add a note via [GitHub Discussions](https://github.com/AlmasB/FXGL/discussions).

### Development Team

A description of roles is given in the [Contribution Guide](CONTRIBUTING.md).

Maintainers (Collaborators):

* [Almas Baimagambetov](https://github.com/AlmasB)

Coordinators:

* [Chengen Zhao](https://github.com/chengenzhao)

Testers:

* [Carl Dea](https://github.com/carldea)
* [Frank Delporte](https://github.com/FDelporte)

### Contribution & Support

If you want to build FXGL from the source code or want to contribute,
please see the [Contribution Guide](CONTRIBUTING.md) (including non-code).
FXGL is fully modular, so new contributors do not need to understand the entire codebase, only the module to which the contribution is made.
Contributions will be reviewed in accordance with the [Code of Conduct](CODE_OF_CONDUCT.md).

You can support the FXGL development or show interest by simply starring the repo or becoming a [sponsor](https://github.com/sponsors/AlmasB).

### Sponsors

Users:

* @Marsl10
* @SergeMerzliakov
* @mbains
* @sabit86
* @hendrikebbers
* @ImperaEtConquer
* @thejeed
* @chikega

Companies:

* @karakun

### Contact

* Ask questions via [GitHub Discussions](https://github.com/AlmasB/FXGL/discussions) 
* Ask via [StackOverflow](https://stackoverflow.com/search?q=fxgl) with tags `javafx` and `fxgl`
* Tweet with [#fxgl](https://twitter.com/search?src=typd&q=%23fxgl)
