curl -X PUT "http://localhost:8080/warehouse/inventory/add?warehouseId=68503d580cf2162da4176815" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "item": {
      "name": "Sample Item",
      "description": "Sample description"
    },
    "quantity": 4
  }'