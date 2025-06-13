#!/bin/bash

# Define the API endpoint
API_URL="localhost:8080/commands/"

# Define the data payload
DATA=$(cat <<EOF
{
    "name": "Sample Item",
    "description": "This is a sample description."
}
EOF
)

# Make the POST request
curl -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$DATA"