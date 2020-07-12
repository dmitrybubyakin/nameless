#!/usr/bin/env bash

MIGRATION_NAME=$1
NOW=$(date +%s)
MIGRATIONS_DIR=resources/migrations

touch ${MIGRATIONS_DIR}/${NOW}-${MIGRATION_NAME}.up.sql
touch ${MIGRATIONS_DIR}/${NOW}-${MIGRATION_NAME}.down.sql
