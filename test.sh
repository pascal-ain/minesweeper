#!/bin/sh

sbt clean coverage test && xdg-open "$(sbt coverageReport | grep "index.html" | sed -e 's/.*\[//; s/\]//')" # the path to the report
