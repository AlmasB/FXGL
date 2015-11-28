## FXGL
Simple and easy to use (hopefully!) JavaFX 8 game library<br/>
[![Release](https://img.shields.io/github/release/AlmasB/FXGL.svg?label=maven)](https://jitpack.io/#AlmasB/FXGL)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/AlmasB/FXGL/blob/master/LICENSE)

## Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
It is primarily aimed at people who wish to learn and practise game development.
It also takes care of the common boilerplate code, so it can be used for fast prototyping.
For larger projects the library may not be as suitable, whereas advanced programmers will probably want to work
with JavaFX directly.
If you have a use case (feature) that FXGL doesn't cover, raise an issue, carefully describing the use case.

## Prerequisites
Oracle JDK 1.8.0_40+

## Build
```bash
mvn package
```
This will generate FXGL-0.1.8.jar, sources and javadoc.

## Setup
Choose setup steps based on your IDE/build tool.

## Setup (Maven)
```maven
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.AlmasB</groupId>
    <artifactId>FXGL</artifactId>
    <version>0.1.8</version>
</dependency>
```

## Setup (Gradle)
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.AlmasB:FXGL:0.1.8'
}
```

## Setup (General)
Download FXGL-0.1.8.jar from <a href="https://github.com/AlmasB/FXGL/releases">Releases</a> (or the one built yourself)
and add it to the build path in your IDE. That's it, you're all set!

## Setup (NetBeans, tested with 8.0.2)
1. File -> New Project -> Java -> Java Application -> Next
2. Choose Project Name (optional: create main class) -> Finish
3. In the Projects view, right-click Libraries -> Add Jar/Folder -> Navigate and Select downloaded FXGL jar

## Setup (Eclipse, tested with 4.5.1)
1. File -> New Java Project
2. Choose Project Name -> Finish
3. Right-click on the created project -> Build Path -> Configure Build Path -> 
    Libraries Tab -> Add External JAR -> Navigate and Select downloaded FXGL jar
    
## Setup (IntelliJ IDEA, tested with 15)
1. Create Project
2. File -> Project Structure
3. Libraries -> Add Java -> Navigate and Select downloaded FXGL jar
    
## Directory Structure for FXGL Applications
This matches a typical IDE directory structure. For Maven users source root is "src/main/java" and assets
should be in "src/main/resources".
This allows easy packaging and deployment, as all assets packaged into jar will continue loading with
exactly the same code.

project directory (typically project name)<br />
&nbsp;&nbsp;&nbsp;&nbsp;src (source code directory)<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assets<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;textures (image files ".png", ".jpg")<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;audio (audio files ".wav")<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;music (music files ".mp3")<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;text (text files ".txt")<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;data (binary data files with custom extensions)<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ui/css (stylesheets for customizing UI elements)<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ui/fonts (fonts ".ttf", ".otf")<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(your packages / code)

## Basic Usage / Examples
The samples/ folder will be constantly updated to include demonstrations of various features.
Video Tutorials Playlist - <a href="https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp">YouTube Link</a> <br/>
The videos will walk you through the basics.

## Notes
If certain parts of documentation are ambiguous/incorrect/missing please let me know or raise an issue.
Any testing, feedback and bug reports are welcome <br/>

3D features and port to mobile will be considered in the future. <br/>

This is only a hobby / side project (for the time being anyway), so the development progress may vary.
Most of the code follows "some" design principles and practices, but overall I wouldn't consider the code to be high quality
and as it is now it doesn't have much production value.

## Latest Release Features
FXGL 0.1.8 supports:
* Full JavaFX Integration (FXGL is built on top of JavaFX 8)
* JBox2D Physics Engine Integration (v.2.3.0, https://github.com/jbox2d/jbox2d)
* Basic Game Loop
* Basic Logging
* Basic Performance Monitor
* Basic Audio System
* Basic Particle System with Canvas Rendering
* Multi-Layer Rendering
* Dynamic Texture Manipulation (Texture Processing + Sprite Sheet Animations)
* Entity Component/Control System
* Time Management System
* Global and Scoped Event System
* Input Bindings (Keys + Mouse)
* Automated Asset Management (".png", ".jpg", ".wav", ".mp3", ".txt", ".ttf/.otf" custom binary formats)
* Automated Collision Handling (also unified, physics collisions are hooked into FXGL)
* Automated Target Screen Resolution (+Fullscreen)
* Networking (both TCP and UDP)
* AI Pathfinding (A star search)
* Quick Time Events (QTE) (<b>API INCOMPLETE</b>)
* Intro Video / Animation (also supports custom intro)
* Few Built-in Styles of Main Menu + Game Menu (also supports custom Main Menu and Game Menu)
* Custom Modifiable UI elements (Dialogs, Bars, Buttons, etc)
* Custom Global CSS to use with existing menus / UI elements
* Saving / Loading System
* User Profiles (Save/Load/Restore Game Settings)
* Achievement System
* In-game Notification System
* Other minor game dev features

## Next Release Features
The issue tracker contains information about possible features to be added in
the next release.
            
## Few Simple Projects So Far<br/>
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_menu2.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_cannon.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Menu.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Pacman.png" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Physics.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Platformer.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_RPG.png" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL24_FXWars2.jpg" />
Sprites can be found on http://opengameart.org/

## Contact
Email: almaslvl@gmail.com<br/>
<a href="https://plus.google.com/+AlmasB0/about">Google+</a>