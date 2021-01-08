![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## About

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

JavaFX Game Development Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)
[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)

### Why FXGL?

* No installation or setup required
* "Out of the box": Java 8-15, Win/Mac/Linux/Android 8+/iOS 11.0+/Web
* Simple and clean API, higher level than other engines
* Superset of JavaFX: no need to learn new UI API
* Real-world game development techniques: Entity-Component, interpolated animations, particles, and [many more](https://github.com/AlmasB/FXGL/wiki/Core-Features)
* Games are easily packaged into a single executable .jar or native images

### Good for:

* Any 2D game (side-scroller / platformer / arcade / RPG)
* Any business applications with complex UI controls / animations
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

For a quick dive into code, see standalone [basic examples](https://github.com/AlmasB/FXGL/tree/master/fxgl-samples/src/main/java/basics).
Otherwise, see:

* [Showcase Trailer](https://youtu.be/fuDQg7W0v4g)
* [Wiki & written tutorials](https://github.com/AlmasB/FXGL/wiki)
* [YouTube tutorials](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* [Java 11 modules](https://github.com/AlmasB/FXGL/wiki/FXGL-11-%28Java-11-modules%29)
* [Sample code demos](fxgl-samples)
* Full [FXGL games](https://github.com/AlmasB/FXGLGames) (with source)
* Pre-built [FXGL demos](https://github.com/AlmasB/FXGLGames/tree/master/binaries) (.jar)
* [Published demos](https://fxgl.itch.io/) on itch.io

### Maven

* [Maven](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin (Java 11+)

Already have `pom.xml`? Then add: (use `0.5.4` for Java 8-10). **Note:** make sure to set `<release>11</release>` for `maven-compiler-plugin`.

```
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>11.12</version>
</dependency>
```

### Gradle

* [Gradle](https://github.com/AlmasB/FXGL-MavenGradle) template project if you use Java and/or Kotlin (Java 11+)

Already have `build.gradle`? Then add: (use `0.5.4` for Java 8-10). **Note:** in case you have errors, please see templates for Gradle above.

```
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:11.12'
}
```

### Uber jar

Download the latest uber jar from [Releases](https://github.com/AlmasB/FXGL/releases)

## Community

* University of Brighton, UK
* University of Nottingham, UK
* kidOYO, USA
* Walton High School, USA
* Zealand Institute of Business and Technology, Denmark
* Federal Institute of Education, Science and Technology of Rio Grande do Sul, Brazil

If your institution wants to use / is using FXGL, drop me an email to be added to the list.

Community tutorials:

- [Space Ranger](https://www.journaldev.com/40219/space-rangers-game-java-fxgl) at journaldev
- [Geometry Wars](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/) at webtechie

Community projects (identified using `fxgl` topic): 

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [Consume](https://ergoscrit.itch.io/consume)
- [FXGL Sliding Puzzle](https://github.com/beryx/fxgl-sliding-puzzle)

If you'd like your project featured here, just add a note in the [Chat](https://gitter.im/AlmasB/FXGL).

### Contribution & Support

If you want to build FXGL from sources or want to contribute,
please see the [Contribution Guide](CONTRIBUTING.md) (including non-code).
FXGL is fully modular, so new contributors do not need to understand the entire codebase, only the module to which the contribution is made.
Contributions will be reviewed in accordance with the [Code of Conduct](CODE_OF_CONDUCT.md).

You can support the FXGL development / show interest by simply starring the repo or becoming a [sponsor](https://github.com/sponsors/AlmasB).

### Sponsors

* @Marsl10
* @SergeMerzliakov
* @mbains
* @sabit86

### Contact

* Ask questions on [StackOverflow](https://stackoverflow.com/search?q=fxgl) with tags `javafx` and `fxgl`
* Tweet with [#fxgl](https://twitter.com/search?src=typd&q=%23fxgl)
* [Chat](https://gitter.im/AlmasB/FXGL) with the friendly FXGL community
