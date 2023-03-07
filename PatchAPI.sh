#!/usr/bin/env bash

# PatchAPI <Patch Name>

PS1="$"

cd Plazma-API

git add .
git commit -m $1

cd ../

./gradlew rebuildAPIPatches