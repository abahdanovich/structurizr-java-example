#!/usr/bin/env bash

set -e
set -o pipefail

cd "`dirname $0`"

APP_NAME=structurizr-app
APP_PACKAGE=com.ab.dna

# https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
rm -rf $APP_NAME
mvn archetype:generate \
  -DgroupId=$APP_PACKAGE \
  -DartifactId=$APP_NAME \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.4 \
  -DinteractiveMode=false
cd $APP_NAME
mvn package
java -cp target/$APP_NAME-1.0-SNAPSHOT.jar $APP_PACKAGE.App
