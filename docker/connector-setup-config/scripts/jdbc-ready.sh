#!/bin/sh

echo "Create jdbc configuration..."
status=$(curl -X POST -H 'Content-Type: application/json' --data @/connector-setup-config/jdbc-client-config.json -s -o /dev/null -w '%{http_code}' 'kafka-connect:8083/connectors');

if [ 409 -eq "${status}" ]
  then
    echo "JDBC client already exists"
  elif [ 201 -eq "${status}" ];
     then
      echo "JDBC connector client successfully created"
  else
    echo "Unknown status: ${status}"
    exit 1
fi
