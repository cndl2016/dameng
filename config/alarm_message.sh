#!/bin/bash
echo "send alarm message to: $2";
curl --proxy "$3" -H "Content-Type: application/json" -X "POST" -d "$1" "$2"
echo -e '\n';
exit