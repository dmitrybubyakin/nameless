#!/bin/sh

echo Your container args are: "$1"
java -Dconfig=$1 -jar nameless.jar server
