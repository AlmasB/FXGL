# FXGL
Simple and easy to use 2D gaming library based on JavaFX 8

# Usage
Download the latest .jar file under jar/ and add it to the build path
in your IDE. That's it, you're all set!

# Examples
Link - https://www.youtube.com/channel/UCmjXvUa36DjqCJ1zktXVbUA/videos
Videos 18-23 will walk you through the basics of FXGL

# Use Case
FXGL is perfect for small to medium sized games and for beginner / intermediate programmers in JavaFX.
For larger projects it may not be as suitable, whereas advanced programmers will probably want to work
with JavaFX directly.

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
            
