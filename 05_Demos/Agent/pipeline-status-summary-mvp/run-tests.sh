#!/usr/bin/env sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
mkdir -p "$OUT/classes"

find "$ROOT/src/main/java" "$ROOT/src/test/java" -name '*.java' -print > "$OUT/sources.txt"
javac -encoding UTF-8 -d "$OUT/classes" @"$OUT/sources.txt"
java -ea -cp "$OUT/classes" com.projectphoenix.agentcore.AgentCoreMvpTest
