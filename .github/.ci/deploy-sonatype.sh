#!/bin/bash

echo "Deploying to sonatype"

openssl aes-256-cbc -pass pass:$ENC_PASSWORD -in .github/.ci/sign.asc.enc -out .github/.ci/sign.asc -d

gpg -q --fast-import .github/.ci/sign.asc
  
mvn deploy --settings .github/.ci/.travis.settings.xml -DskipTests=true

echo "Deployed to sonatype"