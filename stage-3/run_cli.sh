#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac --release 11 -d build src/*.java
java -cp build Main "$@"
