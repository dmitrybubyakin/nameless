#!/bin/sh

echo Your container args are: "$1"
DB=$1 HIKARI=$2 java -jar nameless.jar server
