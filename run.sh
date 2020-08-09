#!/bin/sh

echo Your container args are: "$1" $2
ls
java -jar nameless.jar server
