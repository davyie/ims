curl -X DELETE "http://localhost:8080/api/market/command/delete/item" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "marketName": "Sample Market",
    "itemName": "Sample Item",
    "quantity": 2
  }'