curl -X POST "http://localhost:8080/warehouse/add" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "John Doe",
    "description": "This is a sample description for the POST request."
  }'