#!/bin/sh

echo Your container args are: "$1" $2
ls
java -Ddb=$1 -Dhikari=$2 -jar nameless.jar server
