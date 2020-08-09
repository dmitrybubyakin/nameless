#!/bin/sh

echo Your container args are: "$1" $2
java -Ddb=$1 -Dhikari=$2 -jar namelss.jar server
