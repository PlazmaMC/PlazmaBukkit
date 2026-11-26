#!/usr/bin/env bash

function getCommits() {
    curl -s -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/"$1"/compare/"$2"..."$3" | jq -r '.commits[] | "'"$1"'@\(.sha[:8]) \(.commit.message | split("\r\n")[0] | split("\n")[0])" | sub("\\[ci( |-)skip]"; "[ci/skip]") | sub("\\(#(?<issue>\\d+)\\)"; "('"$1"'#\(.issue))")'
}

function createMessage() {
    local repo="$1" old="$2" new="$3"
    name=$(echo "$repo" | cut -d '/' -f 2)
    echo -e "chore: Updated Upstream ($repo@${new:0:8})\n\nUpstream has released updates that appear to apply and compile correctly.\n\n### Changes:\n$(getCommits "$repo" "$old" "$new")"
}

if [ -z "$1" ]; then
  old=$(git diff gradle.properties | awk '/^-paperCommit =/{print $NF}')
  new=$(/bin/cat gradle.properties | awk '/^paperCommit =/{print $NF}')

  createMessage "PaperMC/Paper" "$old" "$new"
else
  if [ -z "$2" ] || [ -z "$3" ]; then
    echo "Usage: $0 <repo> <old_commit> <new_commit>"
    exit 1
  fi

  createMessage "$1" "$2" "$3"
fi
