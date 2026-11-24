#!/usr/bin/env bash
set -Eeuo pipefail

WORK_DIR_NAME=".work"

REPO="https://github.com/PurpurMC/Purpur.git"
REF="ver/26.3"
NAME="purpur"


SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}" )" &> /dev/null && pwd )
WORK_DIR=$(realpath "$SCRIPT_DIR/../$WORK_DIR_NAME")

function cleanup() {
    local exit=$?
    local lineno=$1

    popd &> /dev/null || true
    exit 0
}
trap 'cleanup $LINENO' ERR


HEADER=$(cat << EOF
From 0000000000000000000000000000000000000000 Mon Sep 17 00:00:00 2001
From: Automated <noreply+automated@papermc.io>
Date: Mon, 17 Sep 2001 00:00:00 +0000
Subject: [PATCH]
EOF
)

### Tasks
function checkout_upstream() {
    local work_dir="$1" repo="$2" ref="$3" name="$4"

    source_dir=$(realpath "$work_dir/$name")
    git clone --quiet --depth=1 --recursive --branch="$ref" "$repo" "$source_dir"
    local exit=$?

    if [[ $exit == 0 ]]; then
        printf "$source_dir"
    fi

    return $exit
}

function apply_patches() {
    local source_dir="$1"

    pushd "$source_dir"
    "$source_dir/gradlew" --no-daemon applyAllPatches
    local exit=$?

    return $exit
}

function write_patch_header() {
    printf "$HEADER $1\n\n\n"
}

function generate_api_patches() {
    local source_dir="$1" base="$2" source="$3" output="$4" name="$5"

    pushd "$source_dir/$base"

    cp -r "$source_dir/$source/src" "$source_dir/$base"

    git add "$source_dir/$base/src"
    git commit -m "$source changes"

    write_patch_header "$name API Changes" > "$output"
    (git diff --diff-algorithm=myers --full-index 'file^..HEAD') >> "$output"
    local exit=$?

    popd
    return $exit
}

function generate_impl_patches() {
    local source_dir="$1" base="$2" source="$3" output="$4" name="$5"

    pushd "$source_dir/$base"

    for src in $(/bin/ls -I 'minecraft' "$source_dir/$source/src"); do
        cp -r "$source_dir/$source/src/$src" "$source_dir/$base/src"

        git add "$source_dir/$base/src/$src"
        git commit -m "$source $src changes"
    done
    unset src

    write_patch_header "$name Server Changes" > "$output"
    (git diff --diff-algorithm=myers --full-index 'file^..HEAD' -- ':!src/main/resources/logo.png') >> "$output"
    local exit=$?

    popd
    return $exit
}

function generate_minecraft_patches() {
    local source_dir="$1" source="$2" output="$3" name="$4"

    minecraft="$source_dir/$source/src/minecraft"
    pushd "$minecraft"

    write_patch_header "$name Minecraft Changes" > "$output"
    for src in $(/bin/ls "$minecraft"); do
        pushd "$minecraft/$src"
        (git diff --diff-algorithm=myers --full-index 'file^..HEAD') >> "$output"
        popd
    done
    popd
}


### Task Group
function combine_patches_mono() {
    local work_dir="$1" repo="$2" ref="$3" name="$4" upstream="paper"

    source_dir=$(checkout_upstream "$work_dir" "$repo" "$ref" "$name")
    apply_patches "$source_dir"

    generate_api_patches "$source_dir" "$upstream-api" "$name-api" "$work_dir/../plazma-api/paper-patches/features/0001-Purpur-API-Changes.patch" "Purpur"
    generate_impl_patches "$source_dir" "$upstream-server" "$name-server" "$work_dir/../plazma-server/paper-patches/features/0001-Purpur-Server-Changes.patch" "Purpur"
    generate_minecraft_patches "$source_dir" "$name-server" "$work_dir/../plazma-server/minecraft-patches/features/0001-Purpur-Minecraft-Changes.patch" "Purpur"

    "$source_dir/gradlew" --stop
    rm -rf "$source_dir"
}


rm -rf "$WORK_DIR"
mkdir -p "$WORK_DIR"

pushd "$WORK_DIR"
combine_patches_mono "$WORK_DIR" "$REPO" "$REF" "$NAME"
popd
