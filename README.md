![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## About

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)
[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)
[![sponsor](https://img.shields.io/badge/sponsor-%241-brightgreen)](https://github.com/sponsors/AlmasB)

### Why FXGL?

* No installation or setup required
* "Out of the box": Java 8-15, Win/Mac/Linux/Android 8+/iOS 11.0+/Web
* Simple and clean API, higher level than other engines
* Superset of JavaFX: no need to learn new UI API
* Real-world game development techniques: Entity-Component, interpolated animations, particles, and [many more](https://github.com/AlmasB/FXGL/wiki/Core-Features)
* Games are easily packaged into a single executable .jar or native images

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

* [Showcase Trailer](https://youtu.be/fuDQg7W0v4g)
* [Wiki & written tutorials](https://github.com/AlmasB/FXGL/wiki)
* [YouTube tutorials](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* [Sample code demos](fxgl-samples)
* [FXGL games](https://github.com/AlmasB/FXGLGames) (with source)
* [Published demos](https://fxgl.itch.io/) on itch.io

### Maven

* [Maven](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin (Java 11+)

Make sure to set `<release>11</release>` for `maven-compiler-plugin`.

```xml
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>11.16</version>
</dependency>
```

Note: use `0.5.4` for Java 8-10.

### Gradle

* [Gradle](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin (Java 11+)

Please refer to the template if there are any errors.

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:11.16'
}
```

Note: use `0.5.4` for Java 8-10.

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
* kidOYO, USA
* Walton High School, USA
* Conant High School, USA
* Zealand Institute of Business and Technology, Denmark
* Federal Institute of Education, Science and Technology of Rio Grande do Sul, Brazil
* FHNW School of Engineering / Computer Science, Switzerland

If your institution wants to use or is using FXGL, add a note in the [Chat](https://gitter.im/AlmasB/FXGL) to be added to the list.

Community tutorials:

- [Space Ranger](https://www.journaldev.com/40219/space-rangers-game-java-fxgl) at journaldev
- [Geometry Wars](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/) at webtechie
- [Mazela-Man](https://dykstrom.github.io/mazela-man-web/home/) by dykstrom

Community projects (identified using `fxgl` topic): 

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [Consume](https://ergoscrit.itch.io/consume)
- [FXGL Sliding Puzzle](https://github.com/beryx/fxgl-sliding-puzzle)

If you'd like your project featured here, just add a note in the [Chat](https://gitter.im/AlmasB/FXGL).

### Development Team

Description of roles is given in the [Contribution Guide](CONTRIBUTING.md).

Maintainers (Collaborators):

* [Almas Baimagambetov](https://github.com/AlmasB)

Coordinators:

* [Adam Bocco](https://github.com/adambocco)

Testers:

* [Carl Dea](https://github.com/carldea)
* [Frank Delporte](https://github.com/FDelporte)

### Contribution & Support

If you want to build FXGL from sources or want to contribute,
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

Companies:

* @karakun

### Contact

* Ask questions via [GitHub Discussions](https://github.com/AlmasB/FXGL/discussions) or on [StackOverflow](https://stackoverflow.com/search?q=fxgl) with tags `javafx` and `fxgl`
* Tweet with [#fxgl](https://twitter.com/search?src=typd&q=%23fxgl)
* [Chat](https://gitter.im/AlmasB/FXGL) with the friendly FXGL community
