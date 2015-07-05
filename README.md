# FXGL
Simple and easy to use Java 2D gaming library based on JavaFX 8

# Usage
Download the latest .jar file under jar/ and add it to the build path
in your IDE. That's it, you're all set!

# Notes
FXGL is in early stages of development. Hence, the API and functionality are likely to change from version to version. So I wouldn't recommend building a full scale application if you want to be able to use latest versions of FXGL.

# Changelog
FXGL 0.1.3 (latest) supports:
* Full JavaFX Integration (FXGL is built on top of JavaFX 8)
* JBox2D Physics Engine Integration (v.2.2.1.1, https://github.com/jbox2d/jbox2d)
* Basic Game Loop
* Basic Logging
* Basic Performance Monitor
* Entity Component System
* Global and Scoped Event System
* Input Bindings
* Automated Asset Management (".png", ".jpg", ".wav", ".mp3", ".txt", custom binary formats)
* Automated Collision Handling (also unified, physics collisions are hooked into FXGL)
* Networking (both TCP and UDP)
* AI Pathfinding (A star search)
* Simple Particles
* Quick Time Events (QTE)
* Intro Video / Animation (also supports custom intro)
* Other minor game dev features

# Examples
Link - https://www.youtube.com/AlmasB0/videos<br />
Videos marked "FXGL" will walk you through the basics

# Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
For larger projects it may not be as suitable, whereas advanced programmers will probably want to work
with JavaFX directly. If you have a use case that FXGL doesn't cover, drop me an email stating the use case and what you have already tried.

# Directory Structure for FXGL Applications
This somewhat matches the Eclipse structure but should work with other IDEs (TODO: needs verification).
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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(your packages / code)
            
# License
MIT, so you can do anything you want with the code. Most of the code is for teaching purposes, so it probably doesn't have much production value.

#Contact
almaslvl@gmail.com, https://plus.google.com/+AlmasB0/about