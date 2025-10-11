#!/bin/bash

curl -X POST http://localhost:8080/api/market/command/create \
-H "Content-Type: application/json" \
-d '{
    "name": "Sample Market",
    "price": 100.50,
    "items": []
}'