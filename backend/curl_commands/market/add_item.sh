curl -X POST "http://localhost:8080/api/market/command/add/item" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "warehouseId": "68503d580cf2162da4176815",
    "marketName": "Sample Market",
    "itemId": 1,
    "quantity": 5
  }'