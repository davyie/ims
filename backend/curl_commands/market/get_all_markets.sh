#!/bin/bash

# Define the API endpoint
API_URL="http://localhost:8080/api/market/query/get/all"

# Make the GET request
curl -X GET "$API_URL" -H "Content-Type: application/json"