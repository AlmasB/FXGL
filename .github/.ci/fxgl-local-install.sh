#!/bin/bash

# run from top-level FXGL directory

mvn clean

mvn -T 4 install -pl :fxgl -am -DskipTests=true -Dgpg.skip=true -Dlicense.skip=true -Dpmd.skip=true