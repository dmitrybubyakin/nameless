#!/usr/bin/env bash

DB_HOST=localhost
DB_NAME=namelss
TEST_DB_NAME=namelss_test
DB_USER=postgres
createdb -h ${DB_HOST} -U ${DB_USER} -O${DB_USER} -w -Eutf8 ${DB_NAME}
createdb -h ${DB_HOST} -U ${DB_USER} -O${DB_USER} -w -Eutf8 ${TEST_DB_NAME}
echo ${DB_NAME} "DB created"
