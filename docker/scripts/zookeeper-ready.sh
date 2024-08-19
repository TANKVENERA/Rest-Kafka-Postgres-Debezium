#!/bin/bash
echo Waiting for Zookeeper ...
OK=$(echo ruok | nc 127.0.0.1 $ZOOKEEPER_CLIENT_PORT)
if [ "$OK" == "imok" ]; then
  echo Zookeeper is ready
	exit 0
else
  echo Zookeeper is not yet ready ...
	exit 1
fi