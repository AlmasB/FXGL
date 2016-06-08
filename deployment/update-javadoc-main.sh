#!/bin/sh

# Usage: update-javadoc-main.sh JAVADOC_JAR_FILE

# make dir desktop/tmp/javadoc if !exists

# unjar there

# update javadoc

# remove dir

mkdir -p ~/Desktop/tmp/javadoc

cp $1 ~/Desktop/tmp/javadoc

cd ~/Desktop/tmp/javadoc

jar -xf *.jar

rm *.jar

cd ~/workspace/java/FXGL/deployment

./update-javadoc.sh ~/Desktop/tmp/javadoc