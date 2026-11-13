#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
rm -f build/*.class
javac --release 11 -d build src/*.java
jar --create --file RestaurantRush_GUI.jar --main-class GuiMain -C build .
jar --create --file RestaurantRush_CLI.jar --main-class Main -C build .
