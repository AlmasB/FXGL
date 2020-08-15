#!/bin/bash

mkdir tmp
cd tmp

git clone --quiet https://github.com/AlmasB/builds-fxgl

# in ./tmp/builds-fxgl
cd builds-fxgl

git config user.email "$UP_USER_EMAIL"
git config user.name "up-server"

git rm *.txt
git commit -m "auto-remove"
touch notify.txt
git add *.txt
git commit -m "auto-add"

git push --quiet "https://$UP_GH_TOKEN@github.com/AlmasB/builds-fxgl.git" master > /dev/null 2>&1

# return back to .
cd ../..

echo "Notified fxgl builder"