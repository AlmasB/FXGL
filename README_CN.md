![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## 语言

&emsp;&emsp;[English](https://github.com/AlmasB/FXGL/blob/dev/README.md)

&emsp;&emsp;简体中文

## 关于

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

JavaFX 游戏开发框架

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)[![sponsor](https://img.shields.io/badge/sponsor-%241-brightgreen)](https://github.com/sponsors/AlmasB)

## 为什么选择FXGL ?

- 无需安装或设置
- “开箱即用”：Java 8-17、Win/Mac/Linux/Android 8+/iOS 11.0+/Web
- 简单干净的API，比其他引擎更高级别
- JavaFX 的超集：无需学习新的 UI API
- 真实世界的游戏开发技术：实体组件、插值动画、粒子[等等](https://github.com/AlmasB/FXGL/wiki/Core-Features)
- 游戏很容易打包成一个可执行的 .jar 或原生镜像

### 适合：

- 任何 2D 游戏（横向卷轴 / 平台游戏 / 街机 / 角色扮演游戏）
- 任何具有复杂 UI 控件/动画的业务应用程序
- 实验 3D
- 爱好/学术/商业项目
- 教学/学习/提高游戏开发技能
- 应用创意的快速原型制作

### 最小的例子

```
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

## 入门

如需快速深入了解代码，请参阅独立的[基本示例](https://github.com/AlmasB/FXGL/tree/dev/fxgl-samples/src/main/java/basics)。否则，请参阅：

- [展示预告片](https://youtu.be/fuDQg7W0v4g)
- [将 FXGL 与 IntelliJ 一起使用](https://youtu.be/LhmlFC6KE2Q)
- [维基和书面教程](https://github.com/AlmasB/FXGL/wiki)
- [YouTube 教程](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
- [示例代码演示](https://github.com/lhDream/FXGL/blob/dev/fxgl-samples)
- [FXGL 游戏](https://github.com/AlmasB/FXGLGames)（附源码）
- 在 `itch.io `上发布了[演示](https://fxgl.itch.io/)

### Maven

- [Maven](https://github.com/AlmasB/FXGL-MavenGradle)模板项目，如果你使用 Java 和/或 Kotlin (Java 17+)

确保设置`<release>17</release>`为`maven-compiler-plugin`.

```
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>17.1</version>
</dependency>
```

注意：`0.5.4`用于 Java 8-10 和`11.17`Java 11-16。

### Gradle

- 如果您使用 Java 和/或 Kotlin (Java 17+)，则为[Gradle模板项目](https://github.com/AlmasB/FXGL-MavenGradle)

如有错误请以模板为准。

```
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:17.1'
}
```

注意：`0.5.4`用于 Java 8-10 和`11.17`Java 11-16。

### 模块化

如果您希望开发模块化应用程序，这里有一个完整的示例`module-info.java`：

```
open module app.name {
    requires com.almasb.fxgl.all;
}
```

### Uber jar

从[Releases](https://github.com/AlmasB/FXGL/releases)下载最新的 uber jar

## 社区

- 英国布莱顿大学
- 英国诺丁汉大学
- 美国佐治亚理工学院
- 美国kidOYO
- 美国沃尔顿高中
- 美国科南特高中
- 丹麦新西兰商业技术学院
- 巴西南里奥格兰德联邦教育、科学和技术学院
- FHNW 工程/计算机科学学院，瑞士

如果您的机构想要使用或正在使用 FXGL，请在[Chat](https://gitter.im/AlmasB/FXGL)中添加注释以添加到列表中。

社区教程：

- Journaldev 的[太空游侠](https://www.journaldev.com/40219/space-rangers-game-java-fxgl)
- webtechie 的[几何大战](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/)
- dykstrom 的[Mazela -Man](https://dykstrom.github.io/mazela-man-web/home/)

社区项目（使用`fxgl`主题标识）：

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [消耗](https://ergoscrit.itch.io/consume)
- [FXGL 滑动拼图](https://github.com/beryx/fxgl-sliding-puzzle)

如果您希望在此处展示您的项目，只需在[Chat](https://gitter.im/AlmasB/FXGL)中添加注释即可。

### 开发团队

角色描述在[贡献指南](https://github.com/lhDream/FXGL/blob/dev/CONTRIBUTING.md)中给出。

维护者（合作者）：

- [Almas Baimagambetov](https://github.com/AlmasB)

协调员：

- [Adam Bocco](https://github.com/adambocco)

测试人员：

- [Carl Dea](https://github.com/carldea)
- [Frank Delporte](https://github.com/FDelporte)

### 贡献与支持

如果您想从源代码构建 FXGL 或想贡献，请参阅[贡献指南](https://github.com/lhDream/FXGL/blob/dev/CONTRIBUTING.md)（包括非代码）。FXGL 是完全模块化的，因此新的贡献者不需要了解整个代码库，只需要了解做出贡献的模块即可。贡献将根据[行为准则](https://github.com/lhDream/FXGL/blob/dev/CODE_OF_CONDUCT.md)进行审查。

[您可以通过简单地为 repo 加注星标或成为赞助商](https://github.com/sponsors/AlmasB)来支持 FXGL 的开发或表现出兴趣。

### 赞助商

用户：

- @Marsl10
- @SergeMerzliakov
- @mbains
- @sabit86
- @hendrikebbers
- @ImperaEtConquer
- @thejeed

公司：

- @karakun

### 接触

- 通过[GitHub 讨论](https://github.com/AlmasB/FXGL/discussions)或在[StackOverflow](https://stackoverflow.com/search?q=fxgl)上使用标签`javafx`和`fxgl`
- 使用[#fxgl 发推文](https://twitter.com/search?src=typd&q=%23fxgl)
- 与友好的 FXGL 社区[聊天](https://gitter.im/AlmasB/FXGL)