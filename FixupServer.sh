#!/usr/bin/env bash

# FixupServer

PS1="$"

cd Plazma-Server

git add .
git commit -m "fixup"
git format-patch -1

cd ../