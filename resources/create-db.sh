#!/usr/bin/env bash

DB_HOST=localhost
DB_NAME=namelss
TEST_DB_NAME=namelss_test
DB_USER=namelss
createuser -h ${DB_HOST} -D -A -e -s ${DB_USER}
createdb -h ${DB_HOST} -U ${DB_USER} -O${DB_USER} -w -Eutf8 ${DB_NAME}
createdb -h ${DB_HOST} -U ${DB_USER} -O${DB_USER} -w -Eutf8 ${TEST_DB_NAME}
echo ${DB_NAME} " and " ${TEST_DB_NAME} "DB created"
