# FXGL
Simple and easy to use Java 2D gaming library based on JavaFX 8<br/>
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL.png" />

# Few Simple Projects So Far<br/>
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Menu.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Pacman.png" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Physics.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_Platformer.jpg" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL_RPG.png" />
<img src="http://almasb.github.io/LearnJavaGameDev/tutorials/images/fxgl/FXGL24_FXWars2.jpg" />
Sprites can be found on http://opengameart.org/

# Prerequisites
Oracle JDK 1.8.0_40+

# Setup (General)
Download the latest .jar file under jar/ and add it to the build path
in your IDE. That's it, you're all set! See below setup for some IDEs

# Setup (NetBeans, tested with 8.0.2)
1. File -> New Project -> Java -> Java Application -> Next
2. Choose Project Name (optional: create main class) -> Finish
3. In the Projects view, right-click Libraries -> Add Jar/Folder -> Navigate and Select downloaded FXGL jar

# Setup (Eclipse, tested with 4.5)
1. File -> New Java Project
2. Choose Project Name -> Finish
3. Right-click on the created project -> Build Path -> Configure Build Path -> 
    Libraries Tab -> Add External JAR -> Navigate and Select downloaded FXGL jar

# Notes
FXGL is in early stages of development. Hence, the API and functionality are likely to change from version to version.
So I wouldn't recommend building a full scale application if you want to be able to use latest versions of FXGL.
The plan is to make the API robust, stable and intuitive by v 0.5.
(Considering 10 patches = 1 minor version bump, it's a long way from now). As of 0.1.4 the code is somewhat
thoroughly documented with javadoc. If certain parts of documentation are ambiguous or incorrect/missing please let me know.
Any testing, feedback and bug reports are welcome <br/>

3D features and port to mobile will be considered in the future.

# Basic Usage
The samples/ folder will be constantly updated to include demonstrations of various features.

# Changelog
FXGL 0.1.6 (latest) supports:
* Full JavaFX Integration (FXGL is built on top of JavaFX 8)
* JBox2D Physics Engine Integration (v.2.2.1.1, https://github.com/jbox2d/jbox2d)
* Basic Game Loop
* Basic Logging
* Basic Performance Monitor
* Basic Audio System
* Basic Particle System with Canvas Rendering
* Multi-Layer Rendering
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
* Main Menu + Game Menu (also supports custom Main Menu and Game Menu)
* Custom Modifiable UI elements (Dialogs, Bars, Buttons, etc)
* Saving / Loading System
* Other minor game dev features

# Next Release Features
The issue tracker contains information about possible features to be added in
the next release.

# Examples
Video Tutorials Playlist - <a href="https://www.youtube.com/watch?v=mPE8p8p_YjQ&list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp">YouTube Link</a> <br/>
The videos will walk you through the basics.

# Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
It is primarily aimed at people who wish to learn and practise game development. Hence the readability of
code is favored over performance. Nevertheless, as development progresses, the code may reach
high level quality.
For larger projects the library may not be as suitable, whereas advanced programmers will probably want to work
with JavaFX directly. If you have a use case that FXGL doesn't cover, drop me an email stating the use case and what you have already tried.

# Directory Structure for FXGL Applications
This matches the Eclipse/NetBeans structure but should work with other IDEs.
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
            
# License
MIT, so you can do anything you want with the code. Most of the code is for teaching purposes, so it probably doesn't have much production value.

#Contact
almaslvl@gmail.com, https://plus.google.com/+AlmasB0/about