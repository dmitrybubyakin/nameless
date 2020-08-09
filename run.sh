#!/bin/sh

echo Your container args are: "$@"
java -Ddb=$1 -Dhikari=$2 -jar namelss.jar server
