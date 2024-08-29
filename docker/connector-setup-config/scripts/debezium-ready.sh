#!/bin/sh

echo "Create debezium configuration..."
status=$(curl -X POST -H 'Content-Type: application/json' --data @/connector-setup-config/debezium-client-config.json -s -o /dev/null -w '%{http_code}' 'debezium-connect:8083/connectors');

if [ 409 -eq "${status}" ]
  then
    echo "Debezium client already exists"
  elif [ 201 -eq "${status}" ];
   then
    echo "Debezium connector client successfully created"
  else
      echo "Unknown status: ${status}"
      exit 1
fi




