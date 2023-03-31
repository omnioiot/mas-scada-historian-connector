#!/bin/bash
#
# Script to install package of MAS SCADA Historian Connector

set -x

mkdir -p buildimg/masshcv2/bin
mkdir -p buildimg/masshcv2/lib
cp bin/* buildimg/masshcv2/bin/.
cp target/*.jar buildimg/masshcv2/lib/.
cp target/dependencies/*.jar buildimg/masshcv2/lib/.
cp lib/* buildimg/masshcv2/lib/.
rm -f buildimg/masshcv2/lib/junit*
rm -f buildimg/masshcv2/bin/package.sh
cd buildimg
if [ -n "$TRAVIS_BUILD_DIR" ]; then
    tar -czf $TRAVIS_BUILD_DIR/mas-scada-historian-connector.tgz ./masshcv2
else
    tar -czf ./mas-scada-historian-connector.tgz ./masshcv2
fi

