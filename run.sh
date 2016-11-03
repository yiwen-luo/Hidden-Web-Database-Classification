#!/bin/bash

DIR=$(dirname $0)

java -classpath ${DIR}/target/HiddenWebDatabaseClassification-1.0-SNAPSHOT-jar-with-dependencies.jar Main $@
