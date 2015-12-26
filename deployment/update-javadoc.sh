#!/bin/sh

echo "Updating Javadoc..."

if test $# -eq 0
    then
        echo "Missing new javadoc location"
        exit 1
fi

if test -d -$1
    then
        echo "$1 is not a directory"
        exit 1
fi

# move to top dir of project
cd ../

# select gh-pages branch
git checkout gh-pages

# move to javadoc dir
cd javadoc/

# clean old javadoc
rm -rf *

# copy new javadoc
cp -r $1/* ./

# move to top dir
cd ../

# add & commit & push
git add javadoc/
git commit -m 'updated javadoc'
git push

# go back to master branch
git checkout master