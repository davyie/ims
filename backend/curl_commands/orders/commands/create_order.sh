curl -X POST "http://localhost:8080/command/" \
-H "Content-Type: application/json" \
-d '{
  "name": "exampleName4",
  "quantity": 15,
  "amount": 49.99,
  "status": "active"
}'