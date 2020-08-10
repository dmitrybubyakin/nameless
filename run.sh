#!/bin/sh

java -Ddb=$1 -Dhikari=$2 -jar nameless.jar server
