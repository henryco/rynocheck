#!/bin/bash

./gradlew clean fatJar

(pkill -f gradle) || true
