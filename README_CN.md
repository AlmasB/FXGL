![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## 语言

&emsp;&emsp;[English](https://github.com/AlmasB/FXGL/blob/dev/README.md)

&emsp;&emsp;简体中文

&emsp;&emsp;[Русский](https://github.com/AlmasB/FXGL/blob/dev/README_RU.md)

## 关于

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

JavaFX 游戏开发框架

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)[![sponsor](https://img.shields.io/badge/sponsor-%241-brightgreen)](https://github.com/sponsors/AlmasB)
[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/fxgl)

## 为什么选择FXGL ?

- 无需安装或设置
- “开箱即用”：Java 8-21、Win/Mac/Linux/Android 8+/iOS 11.0+/Web
- 简单干净的API，相比起其他引擎API级别更高
- JavaFX 的超集：无需学习新的 UI API
- 真实世界的游戏开发技术：实体组件、插值动画、粒子[等等](https://github.com/AlmasB/FXGL/wiki/Core-Features)
- 游戏很容易打包成一个可执行的 .jar 或原生镜像

### 适合：

- 任何 2D 游戏（横向卷轴 /platformer/ 街机 / RPG游戏）
- 任何具有复杂 UI 控件/动画的业务应用程序
- 实验性 3D 特性
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

- [FXGL 书](https://link.springer.com/book/10.1007/978-1-4842-8625-8?sap-outbound-id=3352BB472E8E602B4B29844F1A86CCC4374DDF6E)
- [预告片展示](https://youtu.be/fuDQg7W0v4g)
- [在IntelliJ中使用FXGL](https://youtu.be/LhmlFC6KE2Q)
- [维基和书面教程](https://github.com/AlmasB/FXGL/wiki)
- [YouTube 教程](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
- [示例代码演示](https://github.com/AlmasB/FXGL/blob/dev/fxgl-samples)
- [FXGL 游戏](https://github.com/AlmasB/FXGLGames)（附源码）
- [演示](https://fxgl.itch.io/)在 `itch.io `上发布

### Maven

- [Maven](https://github.com/AlmasB/FXGL-MavenGradle)项目模板，编程语言为 Java 和/或 Kotlin

```
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>21.1</version>
</dependency>
```

### Gradle

- [Gradle项目模板](https://github.com/AlmasB/FXGL-MavenGradle)，编程语言为 Java 和/或 Kotlin

如有错误请以模板为准。

```
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:21.1'
}
```

### 模块化

如果您希望开发模块化应用程序，这里有一个完整的示例`module-info.java`：

```
open module app.name {
    requires com.almasb.fxgl.all;
}
```

### 独立完整包含所有依赖的 jar 文件

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
- 瑞士西北高等专业学院 工程/计算机科学学院

如果您的机构想要使用或正在使用 FXGL，请在[Discussions](https://github.com/AlmasB/FXGL/discussions)中添加注释以添加到列表中。

社区教程：

- Journaldev 的[太空游侠](https://www.journaldev.com/40219/space-rangers-game-java-fxgl)
- webtechie 的[几何大战](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/)
- dykstrom 的[Mazela -Man](https://dykstrom.github.io/mazela-man-web/home/)

社区项目（使用`fxgl`主题标识）：

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [消耗](https://ergoscrit.itch.io/consume)
- [FXGL 滑动拼图](https://github.com/beryx/fxgl-sliding-puzzle)

如果您希望在此处展示您的项目，只需在[Discussions](https://github.com/AlmasB/FXGL/discussions)中添加注释即可。

### 开发团队

角色描述在[贡献指南](https://github.com/AlmasB/FXGL/blob/dev/CONTRIBUTING.md)中给出。

维护者（合作者）：

- [Almas Baimagambetov](https://github.com/AlmasB)

协调员：

- [Chengen Zhao](https://github.com/chengenzhao)

测试人员：

- [Carl Dea](https://github.com/carldea)
- [Frank Delporte](https://github.com/FDelporte)

### 贡献与支持

如果您想从源代码构建 FXGL 或想贡献，请参阅[贡献指南](https://github.com/AlmasB/FXGL/blob/dev/CONTRIBUTING.md)（包括非代码）。FXGL 是完全模块化的，因此新的贡献者不需要了解整个代码库，只需要了解做出贡献的模块即可。贡献将根据[行为准则](https://github.com/AlmasB/FXGL/blob/dev/CODE_OF_CONDUCT.md)进行审查。

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
- @chikega

公司：

- @karakun

### 与我们取得联系

- 通过[GitHub 讨论](https://github.com/AlmasB/FXGL/discussions)
- 或在[StackOverflow](https://stackoverflow.com/search?q=fxgl)上使用标签`javafx`和`fxgl`
- 使用[#fxgl 发推文](https://twitter.com/search?src=typd&q=%23fxgl)
