curl -X PUT "http://localhost:8080/warehouse/inventory/decrement?warehouseId=68501a8a5f8ec46c075ba9a8" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "itemId": 1,
    "quantity": 5
  }'